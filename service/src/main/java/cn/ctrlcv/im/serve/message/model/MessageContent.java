package cn.ctrlcv.im.serve.message.model;

import cn.ctrlcv.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class Name: MessageContent
 * Class Description: 消息内容
 *
 * @author liujm
 * @date 2023-03-21
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageContent extends ClientInfo {

    /**
     * 消息ID
     */
    private String messageId;


    /**
     * 消息发送者
     */
    private String fromId;


    /**
     * 消息接收者
     */
    private String toId;

    /**
     * 消息内容
     */
    private String messageBody;
}
