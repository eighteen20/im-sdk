package cn.ctrlcv.im.common.enums;

import cn.ctrlcv.im.common.exception.ApplicationExceptionEnum;

/**
 * enum Name: BaseErrorCode
 * enum Description: 响应结果枚举
 *
 * @author liujm
 * @date 2023-02-03
 */
public enum BaseErrorCodeEnum implements ApplicationExceptionEnum {

    /**
     * 响应成功
     */
    SUCCESS(200,"success"),

    /**
     * 内部错误，响应失败
     */
    SYSTEM_ERROR(90000,"服务器内部错误,请联系管理员"),

    /**
     * 参数错误，响应失败
     */
    PARAMETER_ERROR(90001,"参数校验错误"),


            ;

    private int code;
    private String error;

    BaseErrorCodeEnum(int code, String error){
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
