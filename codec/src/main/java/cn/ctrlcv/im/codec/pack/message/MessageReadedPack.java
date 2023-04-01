package cn.ctrlcv.im.codec.pack.message;

import lombok.Data;

/**
 * Class Name: MessageReadedPack
 * Class Description: 消息已读包
 *
 * @author liujm
 * @date 2023-04-02
 */
@Data
public class MessageReadedPack {

    private long messageSequence;

    private String fromId;

    private String groupId;

    private String toId;

    private Integer conversationType;

}
