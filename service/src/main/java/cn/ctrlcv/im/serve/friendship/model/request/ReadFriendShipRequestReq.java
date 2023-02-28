package cn.ctrlcv.im.serve.friendship.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * Class Name: ReadFriendShipRequestReq
 * Class Description: 查看好友申请记录参数
 *
 * @author liujm
 * @date 2023-02-23
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ReadFriendShipRequestReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    private String fromId;

}
