package cn.ctrlcv.im.common.enums;

import cn.ctrlcv.im.common.exception.ApplicationExceptionEnum;

/**
 * Class Name: SystemErrorEnum
 * Class Description: 系统级别的错误
 *
 * @author liujm
 * @date 2023-03-16
 */
public enum SystemErrorEnum implements ApplicationExceptionEnum {


    /**
     * 配置的负载均衡策略不存在
     */
    LOAD_BALANCING_POLICY_DOES_NOT_EXIST(10010, "路由负载均衡算法选择错误"),
    LOAD_BALANCING_POLICY_IMPLEMENTATION_DOES_NOT_EXIST(10011, "路由负载均衡算法实现配置错误，算法实现不存在")

    ;

    private int code;
    private String error;

    SystemErrorEnum(int code, String error) {
        this.code = code;
        this.error = error;
    }

    @Override
    public int getCode() {
        return 0;
    }

    @Override
    public String getError() {
        return null;
    }

}