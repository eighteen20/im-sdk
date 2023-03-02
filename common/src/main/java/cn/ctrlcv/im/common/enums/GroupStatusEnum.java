package cn.ctrlcv.im.common.enums;

/**
 * enum Name: GroupStatusEnum
 * enum Description: 群状态枚举
 *
 * @author liujm
 * @date 2023-03-02
 */
public enum GroupStatusEnum {

    /**
     * 1正常 2解散 其他待定比如封禁...
     */
    NORMAL(1),

    DESTROY(2),

    ;

    /**
     * 不能用 默认的 enumType b= enumType.values()[i]; 因为本枚举是类形式封装
     * @param ordinal
     * @return
     */
    public static GroupStatusEnum getEnum(Integer ordinal) {

        if(ordinal == null){
            return null;
        }

        for (int i = 0; i < GroupStatusEnum.values().length; i++) {
            if (GroupStatusEnum.values()[i].getCode() == ordinal) {
                return GroupStatusEnum.values()[i];
            }
        }
        return null;
    }

    private int code;

    GroupStatusEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }

}
