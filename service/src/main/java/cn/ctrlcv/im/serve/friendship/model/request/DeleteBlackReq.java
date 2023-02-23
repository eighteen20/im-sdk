package cn.ctrlcv.im.serve.friendship.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * Class Name: DeleteBlackReq
 * Class Description: 移除黑名单请求参数
 *
 * @author liujm
 * @date 2023-02-23
 */
@Data
public class DeleteBlackReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    private String fromId;

    @NotBlank(message = "好友id不能为空")
    private String toId;

}
