package cn.ctrlcv.im.serve.message.service;

import cn.ctrlcv.im.codec.pack.message.ChatMessageAck;
import cn.ctrlcv.im.common.model.ResponseVO;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.ConversationTypeEnum;
import cn.ctrlcv.im.common.enums.command.GroupEventCommand;
import cn.ctrlcv.im.common.model.ClientInfo;
import cn.ctrlcv.im.common.model.message.GroupChatMessageContent;
import cn.ctrlcv.im.common.model.message.OfflineMessageContent;
import cn.ctrlcv.im.serve.group.service.IGroupMemberService;
import cn.ctrlcv.im.serve.message.model.request.GroupSendMessageReq;
import cn.ctrlcv.im.serve.message.model.response.SendMessageResp;
import cn.ctrlcv.im.serve.sequence.RedisSeq;
import cn.ctrlcv.im.serve.utils.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class Name: GroupMessageService
 * Class Description: 群聊消息服务
 *
 * @author liujm
 * @date 2023-03-22
 */
@Slf4j
@Service
public class GroupMessageService {

    @Resource
    private CheckSendMessageService checkSendMessageService;

    @Resource
    private MessageProducer messageProducer;

    @Resource
    private IGroupMemberService groupMemberService;

    @Resource
    private MessageStoreService messageStoreService;

    @Resource
    private RedisSeq redisSeq;


    private final ThreadPoolExecutor threadPoolExecutor;

    /*
     * 线程池初始化
     */ {
        AtomicInteger num = new AtomicInteger(0);
        threadPoolExecutor = new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000), r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("group-message-service-" + num.getAndIncrement());
            return thread;
        });
    }


    public void process(GroupChatMessageContent messageContent) {

        GroupChatMessageContent cache = messageStoreService.getMessageFromMessageIdCache(messageContent.getAppId(),
                messageContent.getMessageId(), GroupChatMessageContent.class);
        if (cache != null) {
            threadPoolExecutor.execute(() -> {
                this.sendAckToFromer(messageContent, ResponseVO.successResponse());
                // 发给发送方同步在线的其他设备
                this.sendToFromerOtherDevice(messageContent, messageContent);
                // 将消息发动给接收方的在线端口
                this.sendToReceiver(messageContent);
            });
            return;
        }

        Long seq = redisSeq.nextSeq(messageContent.getAppId() + ":" + Constants.SeqConstants.GROUP_MESSAGE + ":" + messageContent.getGroupId());
        messageContent.setMessageSequence(seq);

        messageContent.setMemberIds(groupMemberService.getGroupMemberId(messageContent.getGroupId(), messageContent.getAppId()));

        threadPoolExecutor.execute(() -> {
            try {
                messageStoreService.storeGroupMessage(messageContent);
                // 插入群聊离线消息
                OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
                BeanUtils.copyProperties(messageContent, offlineMessageContent);
                offlineMessageContent.setConversationType(ConversationTypeEnum.GROUP.getCode());
                offlineMessageContent.setToId(messageContent.getGroupId());
                messageStoreService.storeGroupOfflineMessage(offlineMessageContent, messageContent.getMemberIds());
                // 回ACK给发起方
                this.sendAckToFromer(messageContent, ResponseVO.successResponse());
                // 发给发送方同步在线的其他设备
                this.sendToFromerOtherDevice(messageContent, messageContent);
                // 将消息发动给接收方的在线端口
                this.sendToReceiver(messageContent);
                // 将messageId存到redis中
                messageStoreService.setMessageFromMessageIdCache(messageContent.getAppId(), messageContent.getMessageId(), messageContent);
            } catch (Exception e) {
                log.error("群聊消息处理异常", e);
            }
        });


    }

    /**
     * 私有方法，消息发给所有群成员的在线设备
     *
     * @param messageContent 消息内容
     */
    private void sendToReceiver(GroupChatMessageContent messageContent) {
        messageContent.getMemberIds().forEach(memberId -> {
            if (!memberId.equals(messageContent.getFromId())) {
                messageProducer.sendToUser(memberId, GroupEventCommand.MSG_GROUP, messageContent, messageContent.getAppId());
            }
        });
    }

    /**
     * 私有方法，发给发送方同步在线的其他设备
     *
     * @param messageContent 消息内容
     * @param clientInfo     客户端信息
     */
    private void sendToFromerOtherDevice(GroupChatMessageContent messageContent, ClientInfo clientInfo) {
        messageProducer.sendToUserExceptClient(messageContent.getFromId(), GroupEventCommand.MSG_GROUP, messageContent, clientInfo);
    }


    /**
     * 私有方法，IM服务接收到消息后，回复一个ACK数据包
     *
     * @param messageContent 消息内容
     * @param responseVO     校验结果
     */
    private void sendAckToFromer(GroupChatMessageContent messageContent, ResponseVO responseVO) {
        log.info("msg ack,msgId={},checkResult{}", messageContent.getMessageId(), responseVO.getCode());
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId());
        responseVO.setData(chatMessageAck);
        //發消息
        messageProducer.sendToUser(messageContent.getFromId(), GroupEventCommand.GROUP_MSG_ACK, responseVO, messageContent);
    }

    /**
     * 校验群消息发送前的一些条件
     *
     * @param fromId  发送方
     * @param groupId 群组ID
     * @param appId   应用ID
     * @return responseVO {@link ResponseVO}
     */
    private ResponseVO checkImServicePermission(String fromId, String groupId, Integer appId) {
        return this.checkSendMessageService.checkGroupMessage(fromId, groupId, appId);
    }

    /**
     * 发送群消息
     *
     * @param req {@link GroupSendMessageReq}
     * @return {@link SendMessageResp}
     */
    public SendMessageResp send(GroupSendMessageReq req) {
        SendMessageResp sendMessageResp = new SendMessageResp();
        GroupChatMessageContent message = new GroupChatMessageContent();
        BeanUtils.copyProperties(req, message);

        messageStoreService.storeGroupMessage(message);

        sendMessageResp.setMessageKey(message.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());
        //2.发消息给同步在线端
        sendToFromerOtherDevice(message, message);
        //3.发消息给对方在线端
        sendToReceiver(message);

        return sendMessageResp;
    }
}
