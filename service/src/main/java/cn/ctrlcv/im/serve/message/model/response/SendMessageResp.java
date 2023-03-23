package cn.ctrlcv.im.serve.message.model.response;

import lombok.Data;

/**
 * Class Name: SendMessageResp
 * Class Description: 发送消息响应参数
 *
 * @author liujm
 * @date 2023-03-23
 */
@Data
public class SendMessageResp {

    private Long messageKey;

    private Long messageTime;
}
