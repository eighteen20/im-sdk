package cn.ctrlcv.im.common.enums;

import cn.ctrlcv.im.common.exception.ApplicationExceptionEnum;

/**
 * enum Name: UserErrorCodeEnum
 * enum Description: 给用户返回的错误类型枚举
 *
 * @author liujm
 * @date 2023-02-08
 */
public enum UserErrorCodeEnum implements ApplicationExceptionEnum {

    /**
     * 用户模块错误分类
     */
    IMPORT_SIZE_BEYOND(20000, "导入數量超出上限"),
    USER_IS_NOT_EXIST(20001, "用户不存在"),
    SERVER_GET_USER_ERROR(20002, "服务获取用户失败"),
    MODIFY_USER_ERROR(20003, "更新用户失败"),
    SERVER_NOT_AVAILABLE(71000, "没有可用的服务"),
    ;

    private int code;
    private String error;

    UserErrorCodeEnum(int code, String error) {
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
