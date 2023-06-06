package cn.ctrlcv.im.common.model.message;

import cn.ctrlcv.im.common.model.ClientInfo;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class Name: RecallMessageContent
 * Class Description: 撤回消息
 *
 * @author liujm
 * @date 2023-06-06
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class RecallMessageContent  extends ClientInfo {

    private Long messageKey;

    private String fromId;

    private String toId;

    /**
     * 消息时间
     */
    private Long messageTime;

    private Long messageSequence;

    private Integer conversationType;

}
