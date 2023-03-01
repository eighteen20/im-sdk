package cn.ctrlcv.im.serve.friendship.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Class Name: AddFriendShipGroupMemberReq
 * Class Description: 新增分组内好友接口的请求参数
 *
 * @author liujm
 * @date 2023-02-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AddFriendShipGroupMemberReq extends RequestBase {

    @NotBlank(message = "用户Id不能为空")
    private String fromId;

    @NotBlank(message = "分组名称不能为空")
    private String groupName;

    @NotEmpty(message = "请选择用户")
    private List<String> toIds;

}
