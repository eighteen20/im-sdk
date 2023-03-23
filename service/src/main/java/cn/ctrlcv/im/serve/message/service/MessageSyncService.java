package cn.ctrlcv.im.serve.message.service;

import cn.ctrlcv.im.common.enums.command.MessageCommand;
import cn.ctrlcv.im.common.model.message.MessageReceiverAckContent;
import cn.ctrlcv.im.serve.utils.MessageProducer;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;

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

    /**
     * 消息接收确认
     *
     * @param messageReceiverAckContent 消息接收确认内容
     */
    public void receiveAck(MessageReceiverAckContent messageReceiverAckContent) {
        messageProducer.sendToUser(messageReceiverAckContent.getToId(), MessageCommand.MSG_RECEIVE_ACK, messageReceiverAckContent, messageReceiverAckContent.getAppId());
    }
}
