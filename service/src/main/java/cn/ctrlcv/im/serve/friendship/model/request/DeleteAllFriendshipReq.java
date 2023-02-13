package cn.ctrlcv.im.serve.friendship.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * Class Name: DeleteFriendshipReq
 * Class Description: 删除所有好友请求参数
 *
 * @author liujm
 * @date 2023-02-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteAllFriendshipReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

}
