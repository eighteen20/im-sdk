package cn.ctrlcv.im.common.model.message;

import lombok.Data;

/**
 * Class Name: ImMessageBody
 * Class Description: 消息体
 *
 * @author liujm
 * @date 2023-03-23
 */
@Data
public class ImMessageBody {

    private Integer appId;

    /** messageBodyId*/
    private Long messageKey;

    /** messageBody*/
    private String messageBody;

    private String securityKey;

    private Long messageTime;

    private Long createTime;

    private String extra;

    private Integer delFlag;

}
