package cn.ctrlcv.im.serve.friendship.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * Class Name: GetFriendShipReq
 * Class Description: 拉取指定好友信息的请求参数
 *
 * @author liujm
 * @date 2023-02-13
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetFriendShipReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotBlank(message = "toId不能为空")
    private String toId;

}
