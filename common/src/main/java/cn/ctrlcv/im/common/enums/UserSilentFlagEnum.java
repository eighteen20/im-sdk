package cn.ctrlcv.im.common.enums;

/**
 * enum Name: UserSilentFlagEnum
 * enum Description: 用户禁言标识
 *
 * @author liujm
 * @date 2023-03-21
 */
public enum UserSilentFlagEnum {

    /**
     * 0 正常；
     */
    NORMAL(0),

    /**
     * 1 禁言。
     */
    MUTE(1),
    ;

    private final int code;

    UserSilentFlagEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }
}
