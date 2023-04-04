package cn.ctrlcv.im.serve.conversation.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class Name: UpdateConversationReq
 * Class Description: 更新会话请求
 *
 * @author liujm
 * @date 2023-04-04
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UpdateConversationReq extends RequestBase {

    private String conversationId;

    private Integer isMute;

    private Integer isTop;

    private String fromId;

}
