package cn.ctrlcv.im.common.enums;

import cn.ctrlcv.im.common.exception.ApplicationExceptionEnum;

/**
 * enum Name: GateWayErrorCodeEnum
 * enum Description: 拦截器错误码枚举
 *
 * @author liujm
 * @date 2023-03-21
 */
public enum GateWayErrorCodeEnum implements ApplicationExceptionEnum {
    /**
     * 用户签名不存在
     */
    USER_SIGN_NOT_EXIST(60000, "用户签名不存在"),

    /**
     * appId不存在
     */
    APPID_NOT_EXIST(60001, "appId不存在"),

    /**
     * 操作人不存在
     */
    OPERATOR_NOT_EXIST(60002, "操作人不存在"),

    /**
     * 用户签名不正确
     */
    USER_SIGN_IS_ERROR(60003, "用户签名不正确"),

    /**
     * 用户签名与操作人不匹配
     */
    USER_SIGN_OPERATE_NOT_MATE(60005, "用户签名与操作人不匹配"),


    /**
     * 用户签名已过期
     */
    USER_SIGN_IS_EXPIRED(60004, "用户签名已过期"),

    ;

    private int code;
    private String error;

    GateWayErrorCodeEnum(int code, String error) {
        this.code = code;
        this.error = error;
    }

    public int getCode() {
        return this.code;
    }

    public String getError() {
        return this.error;
    }


}
