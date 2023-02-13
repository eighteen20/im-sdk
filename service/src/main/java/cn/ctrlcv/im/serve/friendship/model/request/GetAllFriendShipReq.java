package cn.ctrlcv.im.serve.friendship.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * Class Name: GetAllFriendShipReq
 * Class Description: 拉取所有好友的请求参数
 *
 * @author liujm
 * @date 2023-02-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetAllFriendShipReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    private String fromId;

}
