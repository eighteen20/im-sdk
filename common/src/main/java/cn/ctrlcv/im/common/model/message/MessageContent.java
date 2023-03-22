package cn.ctrlcv.im.common.model.message;

import cn.ctrlcv.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class Name: MessageContent
 * Class Description: 消息内容
 *
 * @author liujm
 * @date 2023-03-22
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageContent extends ClientInfo {

    private String messageId;

    private String fromId;

    private String toId;

    private String messageBody;

    private Long messageTime;

    private String extra;

    private Long messageKey;

    private long messageSequence;

}

