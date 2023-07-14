package cn.ctrlcv.im.serve.user.dao;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

/**
 * 系统用户表
 * @author ljm19
 */
@TableName("im_user_data")
@Data
public class ImUserDataEntity implements Serializable {
    /**
     * 用户ID
     */
    private String userId;

    /**
     * 应用ID
     */
    private Integer appId;

    /**
     * 昵称
     */
    private String nickName;

    /**
     * 性别 1-男，2-女，0-未设置/未知
     */
    private Integer userGender;

    /**
     * 生日
     */
    private String birthDay;

    /**
     * 所在地
     */
    private String location;

    /**
     * 个性签名
     */
    private String selfSignature;

    /**
     * 添加好友的方式  1-无需验证，2-需要验证
     */
    private Integer friendAllowType;

    /**
     * 头像地址
     */
    private String photo;

    /**
     * 密码
     */
    private String password;

    /**
     * 管理员禁止用户添加好友  0-未禁用，1-已禁用
     */
    private Integer disableAddFriend;

    /**
     * 禁言标志
     */
    private Integer silentFlag;

    /**
     * 禁用标志
     */
    private Integer forbiddenFlag;

    /**
     * 用户类型  1-im用户
     */
    private Integer userType;

    /**
     * 删除标志
     */
    private Integer delFlag;

    /**
     * 扩展字段（JSON）
     */
    private String extra;

    private static final long serialVersionUID = 1L;
}