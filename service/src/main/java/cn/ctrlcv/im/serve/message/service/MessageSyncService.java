package cn.ctrlcv.im.serve.message.service;

import cn.ctrlcv.im.codec.pack.message.MessageReadedPack;
import cn.ctrlcv.im.codec.pack.message.RecallMessageNotifyPack;
import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.ConversationTypeEnum;
import cn.ctrlcv.im.common.enums.DelFlagEnum;
import cn.ctrlcv.im.common.enums.MessageErrorCode;
import cn.ctrlcv.im.common.enums.command.Command;
import cn.ctrlcv.im.common.enums.command.GroupEventCommand;
import cn.ctrlcv.im.common.enums.command.MessageCommand;
import cn.ctrlcv.im.common.model.ClientInfo;
import cn.ctrlcv.im.common.model.SyncReq;
import cn.ctrlcv.im.common.model.SyncResp;
import cn.ctrlcv.im.common.model.message.MessageReadedContent;
import cn.ctrlcv.im.common.model.message.MessageReceiverAckContent;
import cn.ctrlcv.im.common.model.message.OfflineMessageContent;
import cn.ctrlcv.im.common.model.message.RecallMessageContent;
import cn.ctrlcv.im.serve.conversation.service.ConversationService;
import cn.ctrlcv.im.serve.group.dao.mapper.ImGroupMemberMapper;
import cn.ctrlcv.im.serve.message.dao.ImMessageBodyEntity;
import cn.ctrlcv.im.serve.message.dao.mapper.ImMessageBodyMapper;
import cn.ctrlcv.im.serve.sequence.RedisSeq;
import cn.ctrlcv.im.serve.utils.ConversationIdGenerate;
import cn.ctrlcv.im.serve.utils.GroupMessageProducer;
import cn.ctrlcv.im.serve.utils.MessageProducer;
import cn.ctrlcv.im.serve.utils.SnowflakeIdWorker;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Class Name: MessageSyncService
 * Class Description: TODO
 *
 * @author liujm
 * @date 2023-03-23
 */
@Service
public class MessageSyncService {

    @Resource
    private MessageProducer messageProducer;
    @Resource
    private ConversationService conversationService;
    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private ImMessageBodyMapper messageBodyMapper;
    @Resource
    private RedisSeq redisSeq;
    @Resource
    private ImGroupMemberMapper groupMemberMapper;
    @Resource
    private GroupMessageProducer groupMessageProducer;


    /**
     * 消息接收确认
     *
     * @param messageReceiverAckContent 消息接收确认内容
     */
    public void receiveAck(MessageReceiverAckContent messageReceiverAckContent) {
        messageProducer.sendToUser(messageReceiverAckContent.getToId(), MessageCommand.MSG_RECEIVE_ACK,
                messageReceiverAckContent, messageReceiverAckContent.getAppId());
    }

    /**
     * 消息已读
     *
     * @param readedContent 消息已读内容
     */
    public void readedMark(MessageReadedContent readedContent) {
        MessageReadedPack readedPack = new MessageReadedPack();
        BeanUtils.copyProperties(readedContent, readedPack);

        // 更新会话的Seq
        conversationService.markConversation(readedContent);
        // 发送给同步端
        sendToSync(readedContent, readedPack, MessageCommand.MSG_READED);
        // 发送已读通知回执给发送端
        messageProducer.sendToUser(readedContent.getToId(), MessageCommand.MSG_READED_RECEIPT,
                readedPack, readedContent.getAppId());
    }

    /**
     * 群组消息已读标记
     *
     * @param readedContent 消息已读内容
     */
    public void groupReadedMark(MessageReadedContent readedContent) {
        conversationService.markConversation(readedContent);
        MessageReadedPack readedPack = new MessageReadedPack();
        BeanUtils.copyProperties(readedContent, readedPack);
        sendToSync(readedContent, readedPack, GroupEventCommand.MSG_GROUP_READED);
        if (!readedContent.getFromId().equals(readedContent.getToId())) {
            // 发送已读通知回执给发送端
            messageProducer.sendToUser(readedPack.getToId(), GroupEventCommand.MSG_GROUP_READED_RECEIPT, readedPack,
                    readedContent.getAppId());
        }
    }

    /**
     * 发送给同步端
     *
     * @param readedContent 消息已读内容
     */
    private void sendToSync(MessageReadedContent readedContent, MessageReadedPack readedPack, Command command) {
        // 发送给自己的其他端
        messageProducer.sendToUser(readedContent.getFromId(), command, readedPack, readedContent);
    }

    /**
     * 离线消息同步
     *
     * @param req {@link SyncReq}
     * @return {@link SyncResp}
     */
    public SyncResp<OfflineMessageContent> syncOfflineMessage(SyncReq req) {
        SyncResp<OfflineMessageContent> resp = new SyncResp<>();

        String key = req.getAppId() + ":" + Constants.RedisKey.OFFLINE_MESSAGE + ":" + req.getOperator();
        // 获取离线消息最大的sequence
        long maxSeq = 0L;
        ZSetOperations<String, Object> opsForZSet = redisTemplate.opsForZSet();
        Set<ZSetOperations.TypedTuple<Object>> set = opsForZSet.reverseRangeWithScores(key, 0, 0);
        if (!CollectionUtils.isEmpty(set)) {
            List<ZSetOperations.TypedTuple<Object>> list = new ArrayList<>(set);
            DefaultTypedTuple<Object> typedTuple = (DefaultTypedTuple<Object>) list.get(0);
            maxSeq = Objects.requireNonNull(typedTuple.getScore()).longValue();
        }

        List<OfflineMessageContent> list = new ArrayList<>();
        resp.setMaxSequence(maxSeq);
        Set<ZSetOperations.TypedTuple<Object>> querySet = opsForZSet
                .rangeByScoreWithScores(key, req.getLastSequence(), maxSeq, 0, req.getMaxLimit());
        for (ZSetOperations.TypedTuple<Object> typedTuple : querySet) {
            String value = String.valueOf(typedTuple.getValue());
            OfflineMessageContent messageContent = JSONObject.parseObject(value, OfflineMessageContent.class);
            list.add(messageContent);
        }
        resp.setDataList(list);
        if (!CollectionUtils.isEmpty(list)) {
            OfflineMessageContent content = list.get(list.size() - 1);
            resp.setCompleted(maxSeq <= content.getMessageKey());
        }

        return resp;
    }

    /**
     * 消息撤回
     * <p>
     * 流程：
     * <br>
     * 1. 修改历史消息的状态
     * <br>
     * 2. 修改离线消息的状态
     * <br>
     * 3. ack给发送方
     * <br>
     * 4. 发送给同步端
     * <br>
     * 5. 分发给消息的接收方
     * </p>
     *
     * @param content 撤回消息内容
     */
    public void recallMessage(RecallMessageContent content) {
        Long messageTime = content.getMessageTime();
        Long now = System.currentTimeMillis();

        RecallMessageNotifyPack pack = new RecallMessageNotifyPack();
        BeanUtils.copyProperties(content, pack);

        if (120000L < now - messageTime) {
            recallAck(pack, ResponseVO.errorResponse(MessageErrorCode.MESSAGE_RECALL_TIME_OUT), content);
            return;
        }

        QueryWrapper<ImMessageBodyEntity> query = new QueryWrapper<>();
        query.eq("app_id", content.getAppId());
        query.eq("message_key", content.getMessageKey());
        ImMessageBodyEntity body = messageBodyMapper.selectOne(query);

        if (body == null) {
            // ack返回失败 不存在的消息不能撤回
            recallAck(pack, ResponseVO.errorResponse(MessageErrorCode.MESSAGE_BODY_IS_NOT_EXIST), content);
            return;
        }
        if (body.getDelFlag() == DelFlagEnum.DELETE.getCode()) {
            // ack返回失败 已经撤回的消息不能再次撤回
            recallAck(pack, ResponseVO.errorResponse(MessageErrorCode.MESSAGE_IS_RECALLED), content);
            return;
        }

        body.setDelFlag(DelFlagEnum.DELETE.getCode());
        messageBodyMapper.update(body, query);

        if (content.getConversationType() == ConversationTypeEnum.P2P.getCode()) {
            // 找到fromId的队列
            String fromKey = content.getAppId() + ":" + Constants.RedisKey.OFFLINE_MESSAGE + ":" + content.getFromId();
            // 找到toId的队列
            String toKey = content.getAppId() + ":" + Constants.RedisKey.OFFLINE_MESSAGE + ":" + content.getToId();

            OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
            BeanUtils.copyProperties(content, offlineMessageContent);
            offlineMessageContent.setDelFlag(DelFlagEnum.DELETE.getCode());
            offlineMessageContent.setMessageKey(content.getMessageKey());
            offlineMessageContent.setConversationType(ConversationTypeEnum.P2P.getCode());
            offlineMessageContent.setConversationId(
                    conversationService.convertConversationId(
                            offlineMessageContent.getConversationType(), content.getFromId(), content.getToId())
            );
            offlineMessageContent.setMessageBody(body.getMessageBody());

            long seq = redisSeq.nextSeq(content.getAppId() + ":" + Constants.SeqConstants.MESSAGE + ":"
                    + ConversationIdGenerate.generateP2PConversationId(content.getFromId(), content.getToId()));
            offlineMessageContent.setMessageSequence(seq);

            long messageKey = SnowflakeIdWorker.nextId();

            redisTemplate.opsForZSet().add(fromKey, JSONObject.toJSONString(offlineMessageContent), messageKey);
            redisTemplate.opsForZSet().add(toKey, JSONObject.toJSONString(offlineMessageContent), messageKey);

            //ack
            recallAck(pack, ResponseVO.successResponse(), content);
            //分发给同步端
            messageProducer.sendToUserExceptClient(content.getFromId(), MessageCommand.MSG_RECALL_NOTIFY, pack, content);
            //分发给对方
            messageProducer.sendToUser(content.getToId(), MessageCommand.MSG_RECALL_NOTIFY, pack, content.getAppId());
        } else {
            List<String> groupMemberId = groupMemberMapper.getGroupMemberId(content.getAppId(), content.getToId());
            long seq = redisSeq.nextSeq(content.getAppId() + ":" + Constants.SeqConstants.MESSAGE + ":"
                    + ConversationIdGenerate.generateP2PConversationId(content.getFromId(), content.getToId()));
            //ack
            recallAck(pack, ResponseVO.successResponse(), content);
            //发送
            messageProducer.sendToUserExceptClient(content.getFromId(), MessageCommand.MSG_RECALL_NOTIFY, pack
                    , content);
            for (String memberId : groupMemberId) {
                String toKey = content.getAppId() + ":" + Constants.SeqConstants.MESSAGE + ":" + memberId;
                OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
                offlineMessageContent.setDelFlag(DelFlagEnum.DELETE.getCode());
                BeanUtils.copyProperties(content, offlineMessageContent);
                offlineMessageContent.setConversationType(ConversationTypeEnum.GROUP.getCode());
                offlineMessageContent.setConversationId(
                        conversationService.convertConversationId(
                                offlineMessageContent.getConversationType(), content.getFromId(), content.getToId())
                );
                offlineMessageContent.setMessageBody(body.getMessageBody());
                offlineMessageContent.setMessageSequence(seq);
                redisTemplate.opsForZSet().add(toKey, JSONObject.toJSONString(offlineMessageContent), seq);

                groupMessageProducer.producer(content.getFromId(), MessageCommand.MSG_RECALL_NOTIFY, pack, content);
            }
        }
    }

    /**
     * 撤回消息ack
     *
     * @param recallPack
     * @param success
     * @param clientInfo
     */
    private void recallAck(RecallMessageNotifyPack recallPack, ResponseVO<?> success, ClientInfo clientInfo) {
        messageProducer.sendToUser(recallPack.getFromId(), MessageCommand.MSG_RECALL_ACK, success, clientInfo);
    }


}
