package cn.ctrlcv.im.serve.message.service;

import cn.ctrlcv.im.codec.pack.message.ChatMessageAck;
import cn.ctrlcv.im.codec.pack.message.MessageReceiveServerAckPack;
import cn.ctrlcv.im.common.model.ResponseVO;
import cn.ctrlcv.im.common.config.ImConfig;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.ConversationTypeEnum;
import cn.ctrlcv.im.common.enums.command.MessageCommand;
import cn.ctrlcv.im.common.model.ClientInfo;
import cn.ctrlcv.im.common.model.message.MessageContent;
import cn.ctrlcv.im.common.model.message.OfflineMessageContent;
import cn.ctrlcv.im.serve.message.model.request.P2pSendMessageReq;
import cn.ctrlcv.im.serve.message.model.response.SendMessageResp;
import cn.ctrlcv.im.serve.sequence.RedisSeq;
import cn.ctrlcv.im.serve.utils.CallbackService;
import cn.ctrlcv.im.serve.utils.ConversationIdGenerate;
import cn.ctrlcv.im.serve.utils.MessageProducer;
import com.alibaba.fastjson.JSONObject;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Class Name: P2PMessageService
 * Class Description: P2P消息服务
 *
 * @author liujm
 * @date 2023-03-21
 */
@Slf4j
@Service
public class P2pMessageService {

    @Resource
    private CheckSendMessageService checkSendMessageService;

    @Resource
    private MessageProducer messageProducer;

    @Resource
    private MessageStoreService messageStoreService;

    @Resource
    private RedisSeq redisSeq;

    @Resource
    private ImConfig imConfig;

    @Resource
    private CallbackService callbackService;

    /**
     * 线程池, 加快消息处理速度
     */
    private final ThreadPoolExecutor threadPoolExecutor;

    /*
     * 线程池初始化
     */ {
        AtomicInteger num = new AtomicInteger(0);
        threadPoolExecutor = new ThreadPoolExecutor(8, 8, 60, TimeUnit.SECONDS,
                new LinkedBlockingQueue<>(1000), r -> {
            Thread thread = new Thread(r);
            thread.setDaemon(true);
            thread.setName("p2p-message-service-" + num.getAndIncrement());
            return thread;
        });
    }

    public void process(MessageContent messageContent) {

        // 用messageId从redis中取出消息
        MessageContent cache = messageStoreService.getMessageFromMessageIdCache(messageContent.getAppId(), messageContent.getMessageId(), MessageContent.class);
        if (cache != null) {
            // 如果消息已经存在，直接分发给接收方
            threadPoolExecutor.execute(() -> {
                this.sendAckToFromer(messageContent, ResponseVO.successResponse());
                // 发给发送方同步在线的其他设备
                this.sendToFromerOtherDevice(cache, cache);
                // 将消息发动给接收方的在线端口
                List<ClientInfo> clientInfoList = this.sendToReceiver(cache);
                if (clientInfoList == null || clientInfoList.isEmpty()) {
                    // 发送接收确认给发送方，要带上是服务端发送的标志
                    this.receiveAck(messageContent);
                }
            });
            return;
        }

        //回调
        ResponseVO responseVO = ResponseVO.successResponse();
        if (imConfig.isSendMessageAfterCallback()) {
            responseVO = callbackService.beforeCallback(messageContent.getAppId(), Constants.CallbackCommand.SEND_MESSAGE_BEFORE
                    , JSONObject.toJSONString(messageContent));
        }

        if (!responseVO.isOk()) {
            ack(messageContent, responseVO);
            return;
        }

        // key = appId + seq + (fromId + toId) / (groupId)
        Long seq = redisSeq.nextSeq(messageContent.getAppId() + ":" + Constants.SeqConstants.MESSAGE + ":" +
                ConversationIdGenerate.generateP2PConversationId(messageContent.getFromId(), messageContent.getToId())
        );
        messageContent.setMessageSequence(seq);

        threadPoolExecutor.execute(() -> {
            try {
                // 消息持久化
                this.messageStoreService.storeP2pMessage(messageContent);
                // 插入离线消息
                OfflineMessageContent offlineMessageContent = new OfflineMessageContent();
                BeanUtils.copyProperties(messageContent, offlineMessageContent);
                offlineMessageContent.setConversationType(ConversationTypeEnum.P2P.getCode());
                this.messageStoreService.storeOfflineMessage(offlineMessageContent);

                // 回ACK给发起方
                this.sendAckToFromer(messageContent, ResponseVO.successResponse());
                // 发给发送方同步在线的其他设备
                this.sendToFromerOtherDevice(messageContent, messageContent);
                // 将消息发动给接收方的在线端口
                List<ClientInfo> clientInfoList = this.sendToReceiver(messageContent);
                // 将messageId存到redis中
                messageStoreService.setMessageFromMessageIdCache(messageContent.getAppId(), messageContent.getMessageId(), messageContent);
                if (clientInfoList == null || clientInfoList.isEmpty()) {
                    // 发送接收确认给发送方，要带上是服务端发送的标志
                    this.receiveAck(messageContent);
                }

                if(imConfig.isSendMessageAfterCallback()){
                    callbackService.callback(messageContent.getAppId(),Constants.CallbackCommand.SEND_MESSAGE_AFTER,
                            JSONObject.toJSONString(messageContent));
                }

                log.info("消息处理完成：{}",messageContent.getMessageId());
            } catch (Exception e) {
                log.error("P2P消息处理异常", e);
            }
        });

    }

    private void ack(MessageContent messageContent, ResponseVO responseVO) {
        log.info("msg ack,msgId={},checkResult{}", messageContent.getMessageId(), responseVO.getCode());

        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId(), messageContent.getMessageSequence());
        responseVO.setData(chatMessageAck);
        //發消息
        messageProducer.sendToUser(messageContent.getFromId(), MessageCommand.MSG_ACK,
                responseVO, messageContent
        );
    }


    /**
     * 接收方离线，系统回ACK给发送方
     *
     * @param messageContent 消息内容
     * @return 发送消息响应
     */
    public void receiveAck(MessageContent messageContent) {
        MessageReceiveServerAckPack pack = new MessageReceiveServerAckPack();
        pack.setFromId(messageContent.getToId());
        pack.setToId(messageContent.getFromId());
        pack.setMessageKey(messageContent.getMessageKey());
        pack.setMessageSequence(messageContent.getMessageSequence());
        pack.setServerSend(true);
        messageProducer.sendToUser(messageContent.getFromId(), MessageCommand.MSG_RECEIVE_ACK, pack,
                new ClientInfo(messageContent.getAppId(), messageContent.getClientType(), messageContent.getImei()));
    }


    /**
     * 私有方法，将消息发动给接收方的在线端口
     *
     * @param messageContent 消息内容
     */
    private List<ClientInfo> sendToReceiver(MessageContent messageContent) {
        // 将消息发动给接收方的在线端口
        return messageProducer.sendToUser(messageContent.getToId(), MessageCommand.MSG_P2P, messageContent, messageContent.getAppId());
    }

    /**
     * 私有方法，消息发给发送方同步在线的其他设备
     *
     * @param messageContent 消息内容
     * @param clientInfo     客户端信息
     */
    private void sendToFromerOtherDevice(MessageContent messageContent, ClientInfo clientInfo) {
        // 发给发送方同步在线的其他设备
        messageProducer.sendToUserExceptClient(messageContent.getFromId(), MessageCommand.MSG_P2P, messageContent, clientInfo);
    }


    /**
     * 私有方法，IM服务接收到消息后，给发起方回复一个ACK数据包
     *
     * @param messageContent 消息内容
     * @param responseVO     校验结果
     */
    private void sendAckToFromer(MessageContent messageContent, ResponseVO responseVO) {
        log.info("msg ack,msgId={},checkResult{}", messageContent.getMessageId(), responseVO.getCode());
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId(), messageContent.getMessageSequence());
        responseVO.setData(chatMessageAck);
        //發消息
        messageProducer.sendToUser(messageContent.getFromId(), MessageCommand.MSG_ACK, responseVO, messageContent);
    }

    /**
     * Im服务权限校验,
     * 校验用户是否被禁言和禁用；校验发送和接收方是否是好友关系
     *
     * @param fromId 发送方
     * @param toId   接收方
     * @param appId  应用ID
     * @return responseVO {@link ResponseVO}
     */
    public ResponseVO checkImServicePermission(String fromId, String toId, Integer appId) {
        // 校验用户是否被禁言和禁用
        ResponseVO checkRes = this.checkSendMessageService.checkUserForbiddenOrSilent(fromId, appId);
        if (!checkRes.isOk()) {
            return checkRes;
        }
        // 校验发送和接收方是否是好友关系
        return this.checkSendMessageService.checkIsFriend(fromId, toId, appId);
    }

    /**
     * 发送消息
     *
     * @param req 消息请求
     * @return 消息响应
     */
    public SendMessageResp send(P2pSendMessageReq req) {
        SendMessageResp sendMessageResp = new SendMessageResp();
        MessageContent message = new MessageContent();
        BeanUtils.copyProperties(req, message);
        //插入数据
        messageStoreService.storeP2pMessage(message);
        sendMessageResp.setMessageKey(message.getMessageKey());
        sendMessageResp.setMessageTime(System.currentTimeMillis());

        //2.发消息给同步在线端
        sendToFromerOtherDevice(message, message);
        //3.发消息给对方在线端
        sendToReceiver(message);
        return sendMessageResp;
    }
}