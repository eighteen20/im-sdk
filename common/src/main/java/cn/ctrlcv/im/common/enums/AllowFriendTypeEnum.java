package cn.ctrlcv.im.common.enums;

/**
 * enum Name: AllowFriendTypeEnum
 * enum Description: 添加好友方式的枚举
 *
 * @author liujm
 * @date 2023-02-23
 */
public enum AllowFriendTypeEnum {

    /**
     * 验证
     */
    NEED(2),

    /**
     * 不需要验证
     */
    NOT_NEED(1),

    ;


    private int code;

    AllowFriendTypeEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }

}
