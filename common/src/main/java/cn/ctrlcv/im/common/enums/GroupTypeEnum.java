package cn.ctrlcv.im.common.enums;

/**
 * enum Name: GroupTypeEnum
 * enum Description: 群组类型枚举
 *
 * @author liujm
 * @date 2023-03-02
 */
public enum GroupTypeEnum {
    /**
     * 群类型 1私有群（类似微信） 2公开群(类似qq）
     */
    PRIVATE(1),

    PUBLIC(2),

    ;

    /**
     * 不能用 默认的 enumType b= enumType.values()[i]; 因为本枚举是类形式封装
     * @param ordinal
     * @return
     */
    public static GroupTypeEnum getEnum(Integer ordinal) {

        if(ordinal == null){
            return null;
        }

        for (int i = 0; i < GroupTypeEnum.values().length; i++) {
            if (GroupTypeEnum.values()[i].getCode() == ordinal) {
                return GroupTypeEnum.values()[i];
            }
        }
        return null;
    }

    private int code;

    GroupTypeEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }

}
