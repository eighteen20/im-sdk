package cn.ctrlcv.im.common.enums;

/**
 * enum Name: UserForbiddenFlagEnum
 * enum Description: 用户禁用标识
 *
 * @author liujm
 * @date 2023-03-21
 */
public enum UserForbiddenFlagEnum {

    /**
     * 0 正常；
     */
    NORMAL(0),

    /**
     * 1 禁用。
     */
    FORBIDDEN(1),
    ;

    private final int code;

    UserForbiddenFlagEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }

}
