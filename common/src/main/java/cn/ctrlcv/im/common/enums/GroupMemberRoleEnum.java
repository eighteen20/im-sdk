package cn.ctrlcv.im.common.enums;

/**
 * enum Name: GroupMemberRoleEnum
 * enum Description: 群成员在群组中的角色
 *
 * @author liujm
 * @date 2023-03-02
 */
public enum GroupMemberRoleEnum {

    /**
     * 普通成员
     */
    ORDINARY(0),

    /**
     * 管理员
     */
    MANAGER(1),

    /**
     * 群主
     */
    OWNER(2),

    /**
     * 离开
     */
    LEAVE(3);
    ;


    private int code;

    /**
     * 不能用 默认的 enumType b= enumType.values()[i]; 因为本枚举是类形式封装
     * @param ordinal
     * @return
     */
    public static GroupMemberRoleEnum getItem(int ordinal) {
        for (int i = 0; i < GroupMemberRoleEnum.values().length; i++) {
            if (GroupMemberRoleEnum.values()[i].getCode() == ordinal) {
                return GroupMemberRoleEnum.values()[i];
            }
        }
        return null;
    }

    GroupMemberRoleEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }

}
