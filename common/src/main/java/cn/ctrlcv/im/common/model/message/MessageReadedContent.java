package cn.ctrlcv.im.common.model.message;

import cn.ctrlcv.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class Name: MessageReadedContent
 * Class Description: 消息已读内容
 *
 * @author liujm
 * @date 2023-04-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class MessageReadedContent extends ClientInfo {

    private long messageSequence;

    private String fromId;

    private String groupId;

    private String toId;

    private Integer conversationType;

}
