package cn.ctrlcv.im.serve.user.model.request;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * Class Name: LoginReq
 * Class Description: 登录IM服务接口的请求参数
 *
 * @author liujm
 * @date 2023-03-15
 */
@Data
public class LoginReq {

    @NotNull(message = "用户id不能位空")
    private String userId;

    @NotNull(message = "appId不能为空")
    private Integer appId;

    private Integer clientType;

}
