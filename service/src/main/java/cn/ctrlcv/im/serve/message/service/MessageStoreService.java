package cn.ctrlcv.im.serve.message.service;

import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.DelFlagEnum;
import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.common.model.message.*;
import cn.ctrlcv.im.serve.message.dao.ImGroupMessageHistoryEntity;
import cn.ctrlcv.im.serve.message.dao.ImMessageHistoryEntity;
import cn.ctrlcv.im.serve.message.dao.mapper.ImGroupMessageHistoryMapper;
import cn.ctrlcv.im.serve.message.dao.mapper.ImMessageBodyMapper;
import cn.ctrlcv.im.serve.utils.SnowflakeIdWorker;
import com.alibaba.fastjson.JSONObject;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;

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
}
