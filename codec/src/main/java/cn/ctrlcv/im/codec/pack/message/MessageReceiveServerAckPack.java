package cn.ctrlcv.im.codec.pack.message;

import lombok.Data;

/**
 * Class Name: MessageReceiveServerAckPack
 * Class Description: 消息接收确认内容
 *
 * @author liujm
 * @date 2023-03-23
 */
@Data
public class MessageReceiveServerAckPack {

    private Long messageKey;

    private String fromId;

    private String toId;

    private Long messageSequence;

    private Boolean serverSend;

}
