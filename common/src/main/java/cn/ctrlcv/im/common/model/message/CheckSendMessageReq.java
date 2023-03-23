package cn.ctrlcv.im.common.model.message;

import lombok.Data;

/**
 * Class Name: CheckSendMessageReq
 * Class Description: 检查消息发送请求参数
 *
 * @author liujm
 * @date 2023-03-23
 */
@Data
public class CheckSendMessageReq {

    private String fromId;

    private String toId;

    private Integer appId;

    /**
     * 单聊或者群聊
     */
    private Integer command;
}
