package cn.ctrlcv.im.serve.user.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotEmpty;

/**
 * Class Name: ModifyUserInfoReq
 * Class Description: 修改用户的请求参数
 *
 * @author liujm
 * @date 2023-02-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ModifyUserInfoReq extends RequestBase {

    @NotEmpty(message = "用户id不能为空")
    private String userId;

    /**
     * 用户名称
     */
    private String nickName;

    /**
     * 位置
     */
    private String location;

    /**
     * 生日
     */
    private String birthDay;

    private String password;

    /**
     * 头像
     */
    private String photo;

    /**
     * 性别
     */
    private String userGender;

    /**
     * 个性签名
     */
    private String selfSignature;

    /**
     * 加好友验证类型（Friend_AllowType） 1需要验证
     */
    private Integer friendAllowType;

    private String extra;
}
