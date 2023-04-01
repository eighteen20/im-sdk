package cn.ctrlcv.im.codec.pack;

import lombok.Data;

/**
 * Class Name: ChatMessageAck
 * Class Description: 发送消息后，服务端返回的消息确认包
 *
 * @author liujm
 * @date 2023-03-21
 */
@Data
public class ChatMessageAck {

    private String messageId;
    private Long messageSequence;

    public ChatMessageAck() {
    }

    public ChatMessageAck(String messageId) {
        this.messageId = messageId;
    }

    public ChatMessageAck(String messageId, Long messageSequence) {
        this.messageId = messageId;
        this.messageSequence = messageSequence;
    }


}
