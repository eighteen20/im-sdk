package cn.ctrlcv.im.serve.message.service;

import cn.ctrlcv.im.codec.pack.ChatMessageAck;
import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.enums.command.MessageCommand;
import cn.ctrlcv.im.common.model.ClientInfo;
import cn.ctrlcv.im.common.model.message.MessageContent;
import cn.ctrlcv.im.serve.message.model.request.P2pSendMessageReq;
import cn.ctrlcv.im.serve.message.model.response.SendMessageResp;
import cn.ctrlcv.im.serve.utils.MessageProducer;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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

    public void process(MessageContent messageContent) {
        String fromId = messageContent.getFromId();
        String toId = messageContent.getToId();
        Integer appId = messageContent.getAppId();

        // 前置校验
        ResponseVO responseVO = this.checkImServicePermission(fromId, toId, appId);
        if (responseVO.isOk()) {
            // 消息持久化
            this.messageStoreService.storeP2pMessage(messageContent);
            // 回ACK给发起方
            this.sendAckToFromer(messageContent, responseVO);
            // 发给发送方同步在线的其他设备
            this.sendToFromerOtherDevice(messageContent, messageContent);
            // 将消息发动给接收方的在线端口
            this.sendToReceiver(messageContent);
        } else {
            // 校验失败，返回错误信息
            // 回ACK给发起方
            this.sendAckToFromer(messageContent, responseVO);
        }

    }

    /**
     * 私有方法，将消息发动给接收方的在线端口
     *
     * @param messageContent 消息内容
     */
    private void sendToReceiver(MessageContent messageContent) {
        // 将消息发动给接收方的在线端口
        messageProducer.sendToUser(messageContent.getToId(), MessageCommand.MSG_P2P, messageContent, messageContent.getAppId());
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
        ChatMessageAck chatMessageAck = new ChatMessageAck(messageContent.getMessageId());
        responseVO.setData(chatMessageAck);
        //發消息
        messageProducer.sendToUser(messageContent.getFromId(), MessageCommand.MSG_ACK, responseVO, messageContent);
    }

    /**
     * 私有方法：Im服务权限校验,
     * 校验用户是否被禁言和禁用；校验发送和接收方是否是好友关系
     *
     * @param fromId 发送方
     * @param toId   接收方
     * @param appId  应用ID
     * @return responseVO {@link ResponseVO}
     */
    private ResponseVO checkImServicePermission(String fromId, String toId, Integer appId) {
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