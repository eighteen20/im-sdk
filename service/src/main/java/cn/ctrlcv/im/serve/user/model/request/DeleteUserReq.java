package cn.ctrlcv.im.serve.user.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Class Name: DeleteUserReq
 * Class Description: 删除用户的请求参数
 *
 * @author liujm
 * @date 2023-02-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DeleteUserReq extends RequestBase {

    @NotEmpty(message = "用户id不能为空")
    private List<String> userId;
}
