package cn.ctrlcv.im.serve.message.service;

import cn.ctrlcv.im.codec.pack.message.MessageReadedPack;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.command.Command;
import cn.ctrlcv.im.common.enums.command.GroupEventCommand;
import cn.ctrlcv.im.common.enums.command.MessageCommand;
import cn.ctrlcv.im.common.model.SyncReq;
import cn.ctrlcv.im.common.model.SyncResp;
import cn.ctrlcv.im.common.model.message.MessageReadedContent;
import cn.ctrlcv.im.common.model.message.MessageReceiverAckContent;
import cn.ctrlcv.im.common.model.message.OfflineMessageContent;
import cn.ctrlcv.im.serve.conversation.service.ConversationService;
import cn.ctrlcv.im.serve.utils.MessageProducer;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
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

    /**
     * 消息接收确认
     *
     * @param messageReceiverAckContent 消息接收确认内容
     */
    public void receiveAck(MessageReceiverAckContent messageReceiverAckContent) {
        messageProducer.sendToUser(messageReceiverAckContent.getToId(), MessageCommand.MSG_RECEIVE_ACK, messageReceiverAckContent, messageReceiverAckContent.getAppId());
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
        messageProducer.sendToUser(readedContent.getToId(), MessageCommand.MSG_READED_RECEIPT, readedPack, readedContent.getAppId());
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
            messageProducer.sendToUser(readedPack.getToId(), GroupEventCommand.MSG_GROUP_READED_RECEIPT, readedPack, readedContent.getAppId());
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
}
