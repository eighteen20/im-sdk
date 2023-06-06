package cn.ctrlcv.im.codec.pack.message;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class Name: RecallMessageNotifyPack
 * Class Description: 撤回消息通知报文
 *
 * @author liujm
 * @date 2023-06-06
 */
@Data
@NoArgsConstructor
public class RecallMessageNotifyPack {
    private String fromId;

    private String toId;

    private Long messageKey;

    private Long messageSequence;
}
