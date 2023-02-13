package cn.ctrlcv.im.serve.friendship.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * Class Name: AddFriendReq
 * Class Description: 添加好友的请求参数
 *
 * @author liujm
 * @date 2023-02-09
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AddFriendReq extends RequestBase {

    @NotBlank(message = "fromId不能为空")
    private String fromId;

    @NotNull(message = "toItem不能为空")
    private FriendDTO toItem;


}
