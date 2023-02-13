package cn.ctrlcv.im.serve.friendship.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Class Name: UpdateFriendReq
 * Class Description: 更新好友请求参数
 *
 * @author liujm
 * @date 2023-02-13
 */
@Data
public class UpdateFriendReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotNull(message = "toItem不能为空")
    private FriendDTO toItem;

}
