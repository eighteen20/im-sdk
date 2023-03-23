package cn.ctrlcv.im.common.model.message;

import cn.ctrlcv.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class Name: MessageReceiverAckContent
 * Class Description: 消息接收确认内容
 *
 * @author liujm
 * @date 2023-03-23
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageReceiverAckContent extends ClientInfo {

    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageSequence;

}
