package cn.ctrlcv.im.codec.proto;

import lombok.Data;

/**
 * Class Name: Message
 * Class Description: 私有协议
 *
 * @author liujm
 * @date 2023-03-13
 */
@Data
public class Message {

    /**
     * 消息头对象
     * {@link MessageHeader}
     */
    private MessageHeader messageHeader;

    /**
     * 消息体
     */
    private Object messagePack;

    @Override
    public String toString() {
        return "Message{" +
                "messageHeader=" + messageHeader +
                ", messagePack=" + messagePack +
                '}';
    }
}
