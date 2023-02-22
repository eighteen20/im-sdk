package cn.ctrlcv.im.common.enums;

/**
 * enum Name: CheckFriendShipTypeEnum
 * enum Description: 好友校验checkType参数的枚举类型
 *
 * @author liujm
 * @date 2023-02-14
 */
public enum CheckFriendShipTypeEnum {

    /**
     * 1 单方校验；
     */
    SINGLE(1),

    /**
     * 2双方校验。
     */
    BOTH(2);

    private int type;

    CheckFriendShipTypeEnum(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
