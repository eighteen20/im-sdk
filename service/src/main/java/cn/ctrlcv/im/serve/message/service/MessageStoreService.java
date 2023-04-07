package cn.ctrlcv.im.serve.message.service;

import cn.ctrlcv.im.common.config.ImConfig;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.ConversationTypeEnum;
import cn.ctrlcv.im.common.enums.DelFlagEnum;
import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.common.model.message.*;
import cn.ctrlcv.im.serve.conversation.service.ConversationService;
import cn.ctrlcv.im.serve.message.dao.ImGroupMessageHistoryEntity;
import cn.ctrlcv.im.serve.message.dao.ImMessageHistoryEntity;
import cn.ctrlcv.im.serve.message.dao.mapper.ImGroupMessageHistoryMapper;
import cn.ctrlcv.im.serve.message.dao.mapper.ImMessageBodyMapper;
import cn.ctrlcv.im.serve.utils.SnowflakeIdWorker;
import com.alibaba.fastjson.JSONObject;
import org.apache.commons.lang3.StringUtils;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Class Name: MessageStoreService
 * Class Description: 消息持久化服务
 *
 * @author liujm
 * @date 2023-03-22
 */
@Service
public class MessageStoreService {

    @Resource
    private ImMessageBodyMapper imMessageBodyMapper;

//    @Resource
//    private ImMessageHistoryMapper imMessageHistoryMapper;

    @Resource
    private ImGroupMessageHistoryMapper groupMessageHistoryMapper;

    @Resource
    private RabbitTemplate rabbitTemplate;

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    @Resource
    private ConversationService conversationService;

    @Resource
    private ImConfig imConfig;


    /**
     * 持久化私聊消息.
     *
     * @param messageContent 消息内容
     */
    @Transactional(rollbackFor = ApplicationException.class)
    public void storeP2pMessage(MessageContent messageContent) {
        // 1.messageContent转化为ImMessageBodyEntity
        ImMessageBody imMessageBody = this.convertToImMessageBodyEntity(messageContent);
        // 2.插入ImMessageBodyEntity
//        this.imMessageBodyMapper.insert(imMessageBodyEntity);
        // 3.messageContent转化为ImMessageHistoryEntity
//        List<ImMessageHistoryEntity> imMessageHistoryEntityList =
//                this.convertToImMessageHistoryEntityList(messageContent, imMessageBodyEntity.getMessageKey());
        // 4.批量插入ImMessageHistoryEntity
//        this.imMessageHistoryMapper.insertBatchSomeColumn(imMessageHistoryEntityList);

//        messageContent.setMessageKey(imMessageBodyEntity.getMessageKey());

        // 异步持久化消息
        DoStoreP2pMessageDTO dto = new DoStoreP2pMessageDTO();
        dto.setMessageContent(messageContent);
        dto.setMessageBody(imMessageBody);
        messageContent.setMessageKey(imMessageBody.getMessageKey());
        rabbitTemplate.convertAndSend(Constants.RabbitConstants.STORE_P2P_MESSAGE, "", JSONObject.toJSONString(dto));
    }

    /**
     * 持久化群聊消息.
     * 1 GroupChatMessageContent转化为ImMessageBodyEntity
     * 2.插入ImMessageBodyEntity
     * 3.GroupChatMessageContent转化为单个ImGroupMessageHistoryEntity
     * 4.插入ImGroupMessageHistoryEntity
     * @param messageContent  消息内容
     */
    public void storeGroupMessage(GroupChatMessageContent messageContent) {
        // 1 GroupChatMessageContent转化为ImMessageBodyEntity
        ImMessageBody imMessageBody = this.convertToImMessageBodyEntity(messageContent);
        // 2.插入ImMessageBodyEntity
//        this.imMessageBodyMapper.insert(imMessageBodyEntity);
        // 3.GroupChatMessageContent转化为单个ImGroupMessageHistoryEntity
//        ImGroupMessageHistoryEntity groupMessageHistory =
//                this.convertToImGroupMessageHistoryEntityList(messageContent, imMessageBodyEntity.getMessageKey());
        // 4.插入ImGroupMessageHistoryEntity
//        this.groupMessageHistoryMapper.insert(groupMessageHistory);

//        messageContent.setMessageKey(imMessageBodyEntity.getMessageKey());

       // 异步持久化消息
        DoStoreGroupMessageDTO dto = new DoStoreGroupMessageDTO();
        dto.setMessageBody(imMessageBody);
        dto.setMessageContent(messageContent);
        rabbitTemplate.convertAndSend(Constants.RabbitConstants.STORE_GROUP_MESSAGE, "", JSONObject.toJSONString(dto));
        messageContent.setMessageKey(imMessageBody.getMessageKey());
    }

    /**
     * 消息内容转化为ImGroupMessageHistoryEntity
     *
     * @param messageContent 消息内容
     * @param messageKey     消息主键
     * @return ImGroupMessageHistoryEntity
     */
    private ImGroupMessageHistoryEntity convertToImGroupMessageHistoryEntityList(GroupChatMessageContent messageContent, Long messageKey) {
        ImGroupMessageHistoryEntity entity = new ImGroupMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, entity);
        entity.setMessageKey(messageKey);
        entity.setGroupId(messageContent.getGroupId());
        entity.setCreateTime(System.currentTimeMillis());
        return entity;
    }

    /**
     * 消息内容转化为ImMessageHistoryEntity
     *
     * @param messageContent 消息内容
     * @param messageKey     消息主键
     * @return ImMessageHistoryEntity
     */
    private List<ImMessageHistoryEntity> convertToImMessageHistoryEntityList(MessageContent messageContent, Long messageKey) {
        List<ImMessageHistoryEntity> list = new ArrayList<>();
        ImMessageHistoryEntity fromHistory = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, fromHistory);
        fromHistory.setOwnerId(messageContent.getFromId());
        fromHistory.setMessageKey(messageKey);
        fromHistory.setCreateTime(System.currentTimeMillis());

        ImMessageHistoryEntity toHistory = new ImMessageHistoryEntity();
        BeanUtils.copyProperties(messageContent, toHistory);
        toHistory.setOwnerId(messageContent.getToId());
        toHistory.setMessageKey(messageKey);
        toHistory.setCreateTime(System.currentTimeMillis());

        list.add(fromHistory);
        list.add(toHistory);
        return list;
    }

    /**
     * 消息内容转化为ImMessageBodyEntity
     *
     * @param messageContent 消息内容
     * @return ImMessageBodyEntity
     */
    private ImMessageBody convertToImMessageBodyEntity(MessageContent messageContent) {
        ImMessageBody messageBody = new ImMessageBody();
        messageBody.setMessageKey(SnowflakeIdWorker.nextId());
        messageBody.setAppId(messageContent.getAppId());
        messageBody.setCreateTime(System.currentTimeMillis());
        messageBody.setSecurityKey("");
        messageBody.setExtra(messageContent.getExtra());
        messageBody.setDelFlag(DelFlagEnum.NORMAL.getCode());
        messageBody.setMessageTime(messageContent.getMessageTime());
        messageBody.setMessageBody(messageContent.getMessageBody());
        return messageBody;
    }


    public void setMessageFromMessageIdCache(Integer appId, String messageId, Object messageContent) {
        String key = appId + ":" + Constants.RedisConstants.CACHE_MESSAGE + ":" + messageId;
        stringRedisTemplate.opsForValue().set(key, JSONObject.toJSONString(messageContent), 5, TimeUnit.MINUTES);
    }


    public <T> T getMessageFromMessageIdCache(Integer appId, String messageId, Class<T> clazz) {
        String key =appId + ":" + Constants.RedisConstants.CACHE_MESSAGE + ":" + messageId;
        String s = stringRedisTemplate.opsForValue().get(key);
        if (StringUtils.isNotBlank(s)) {
            return JSONObject.parseObject(s, clazz);
        }
        return null;
    }


    /**
     * 存储单人离线消息
     * <p>
     *     0. 判断队列的数据是否超过设定值。
     *     1. 找到fromId的消息队列。
     *     2. 插入数据到redis的zeset，根据messageKey作为score
     *     3. 判断队列的数据是否超过设定值。
     *     4. 找到toId的消息队列。
     *     5. 插入数据到redis的zeset，根据messageKey作为score
     * <p>
     *     数据存储则略：根据限制数量拉取
     *
     * @param offlineMessageContent {@link OfflineMessageContent}
     */
    public void storeOfflineMessage(OfflineMessageContent offlineMessageContent) {

        String fromIdKey = offlineMessageContent.getAppId() + ":" + Constants.RedisConstants.OFFLINE_MESSAGE + ":" + offlineMessageContent.getFromId();
        String toIdKey = offlineMessageContent.getAppId() + ":" + Constants.RedisConstants.OFFLINE_MESSAGE + ":" + offlineMessageContent.getToId();

        Long fromIdSize = stringRedisTemplate.opsForZSet().size(fromIdKey);
        Long toIdSize = stringRedisTemplate.opsForZSet().size(toIdKey);

        if (fromIdSize >= imConfig.getOfflineMessageCount()) {
            // 从队列中删除最早的数据
            stringRedisTemplate.opsForZSet().removeRange(fromIdKey, 0, 0);
        }
        String conversationIdForm = conversationService.generateConversationId(ConversationTypeEnum.P2P.getCode(),
                offlineMessageContent.getFromId(), offlineMessageContent.getToId());
        offlineMessageContent.setConversationId(conversationIdForm);
        stringRedisTemplate.opsForZSet().add(fromIdKey, JSONObject.toJSONString(offlineMessageContent), offlineMessageContent.getMessageKey());

        if (toIdSize >= imConfig.getOfflineMessageCount()) {
            // 从队列中删除最早的数据
            stringRedisTemplate.opsForZSet().removeRange(toIdKey, 0, 0);
        }
        String conversationIdTo = conversationService.generateConversationId(ConversationTypeEnum.P2P.getCode(),
                offlineMessageContent.getToId(), offlineMessageContent.getFromId());
        offlineMessageContent.setConversationId(conversationIdTo);
        stringRedisTemplate.opsForZSet().add(toIdKey, JSONObject.toJSONString(offlineMessageContent), offlineMessageContent.getMessageKey());
    }


    /**
     * 存储群聊离线消息
     *
     * @param offlineMessageContent {@link OfflineMessageContent}
     */
    public void storeGroupOfflineMessage(OfflineMessageContent offlineMessageContent, List<String> memberIds) {

        for (String memberId : memberIds) {
            String toIdKey = offlineMessageContent.getAppId() + ":" + Constants.RedisConstants.OFFLINE_MESSAGE + ":" + memberId;
            Long size = stringRedisTemplate.opsForZSet().size(toIdKey);
            if (size >= imConfig.getOfflineMessageCount()) {
                // 从队列中删除最早的数据
                stringRedisTemplate.opsForZSet().removeRange(toIdKey, 0, 0);
            }
            String conversationId = conversationService.generateConversationId(ConversationTypeEnum.GROUP.getCode(),
                    memberId, offlineMessageContent.getToId());
            offlineMessageContent.setConversationId(conversationId);
            stringRedisTemplate.opsForZSet().add(toIdKey, JSONObject.toJSONString(offlineMessageContent), offlineMessageContent.getMessageKey());
        }

    }

}
