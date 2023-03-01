package cn.ctrlcv.im.serve.friendship.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Class Name: AddFriendShipGroupReq
 * Class Description: 新建分组接口参数
 *
 * @author liujm
 * @date 2023-02-28
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AddFriendShipGroupReq extends RequestBase {

    @NotBlank(message = "用户Id不能为空")
    public String fromId;

    @NotBlank(message = "分组名称不能为空")
    private String groupName;

    private List<String> toIds;

}
