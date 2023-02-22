package cn.ctrlcv.im.serve.friendship.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Class Name: CheckFriendShipReq
 * Class Description: 批量校验好友关系请求参数
 *
 * @author liujm
 * @date 2023-02-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class CheckFriendShipReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotEmpty(message = "toId不能为空")
    private List<String> toIds;

    @NotBlank(message = "checkType不能为空")
    private Integer checkType;
}
