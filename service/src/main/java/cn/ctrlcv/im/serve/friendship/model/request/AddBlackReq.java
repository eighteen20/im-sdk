package cn.ctrlcv.im.serve.friendship.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;

/**
 * Class Name: AddBlackReq
 * Class Description: 新增黑名单请求参数
 *
 * @author liujm
 * @date 2023-02-23
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AddBlackReq extends RequestBase {

    @NotBlank(message = "用户id不能为空")
    private String fromId;

    private String toId;

}
