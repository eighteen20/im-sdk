package cn.ctrlcv.im.codec.pack.user;

import lombok.Data;

/**
 * Class Name: UserModifyPack
 * Class Description: 用户资料修改通知对象
 *
 * @author liujm
 * @date 2023-03-19
 */
@Data
public class UserModifyPack {

    /**
     * 用户id
     */
    private String userId;

    /**
     * 用户名称
     */
    private String nickName;

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

}
