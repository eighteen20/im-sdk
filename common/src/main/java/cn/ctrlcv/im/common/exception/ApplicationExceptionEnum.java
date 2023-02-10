package cn.ctrlcv.im.common.exception;

/**
 * interface Name: ApplicationExceptionEnum
 * interface Description: 响应枚举接口，提供获取参数的方法
 *
 * @author liujm
 * @date 2023-02-03
 */
public interface ApplicationExceptionEnum {

    /**
     * 获取响应码
     *
     * @return int
     */
    int getCode();

    /**
     * 获取结果
     *
     * @return int
     */
    String getError();
}
