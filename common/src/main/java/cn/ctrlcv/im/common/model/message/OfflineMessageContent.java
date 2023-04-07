package cn.ctrlcv.im.common.model.message;

import lombok.Data;

/**
 * Class Name: OfflineMessageContent
 * Class Description: 离线消息内容
 *
 * @author liujm
 * @date 2023-04-07
 */
@Data
public class OfflineMessageContent {

    private Integer appId;

    /** messageBodyId*/
    private Long messageKey;

    /** messageBody*/
    private String messageBody;

    private Long messageTime;

    private String extra;

    private Integer delFlag;

    private String fromId;

    private String toId;

    /** 序列号*/
    private Long messageSequence;

    private String messageRandom;

    private Integer conversationType;

    private String conversationId;

}
