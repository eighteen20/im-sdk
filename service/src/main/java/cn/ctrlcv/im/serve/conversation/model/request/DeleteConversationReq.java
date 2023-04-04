package cn.ctrlcv.im.serve.conversation.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * Class Name: DeleteConversationReq
 * Class Description: 删除会话请求
 *
 * @author liujm
 * @date 2023-04-04
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteConversationReq extends RequestBase {

    @NotBlank(message = "会话id不能为空")
    private String conversationId;

    @NotBlank(message = "fromId不能为空")
    private String fromId;

}
