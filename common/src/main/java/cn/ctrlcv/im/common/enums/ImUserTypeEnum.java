package cn.ctrlcv.im.common.enums;

/**
 * enum Name: ImUserTypeEnum
 * enum Description: 用户类型枚举
 *
 * @author liujm
 * @date 2023-03-21
 */
public enum ImUserTypeEnum {

    /**
     * 普通用户
     */
    IM_USER(1),


    /**
     * 管理员
     */
    APP_ADMIN(100),
    ;

    private final int code;

    ImUserTypeEnum(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

}
