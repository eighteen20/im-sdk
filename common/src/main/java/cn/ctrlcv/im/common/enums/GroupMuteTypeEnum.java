package cn.ctrlcv.im.common.enums;

/**
 * enum Name: GroupMuteTypeEnum
 * enum Description: 群组禁言类型
 *
 * @author liujm
 * @date 2023-03-22
 */
public enum GroupMuteTypeEnum {

    /**
     * 0 不禁言；
     */
    NOT_MUTE(0),

    /**
     * 1 禁言。
     */
    MUTE(1),

    ;

    /**
     * 不能用 默认的 enumType b= enumType.values()[i]; 因为本枚举是类形式封装
     * @param ordinal
     * @return
     */
    public static GroupMuteTypeEnum getEnum(Integer ordinal) {

        if(ordinal == null){
            return null;
        }

        for (int i = 0; i < GroupMuteTypeEnum.values().length; i++) {
            if (GroupMuteTypeEnum.values()[i].getCode() == ordinal) {
                return GroupMuteTypeEnum.values()[i];
            }
        }
        return null;
    }

    private int code;

    GroupMuteTypeEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }

}
