package cn.ctrlcv.im.common.enums.command;

/**
 * enum Name: CommandType
 * enum Description: 指令类型
 *
 * @author liujm
 * @date 2023-06-06
 */
public enum CommandType {

    /**
     * 用户
     */
    USER("4"),

    /**
     * 好友
     */
    FRIEND("3"),

    /**
     * 群组
     */
    GROUP("2"),

    /**
     * 消息
     */
    MESSAGE("1"),

    ;

    private final String commandType;

    public String getCommandType() {
        return commandType;
    }

    CommandType(String commandType) {
        this.commandType = commandType;
    }

    public static CommandType getCommandType(String ordinal) {
        for (int i = 0; i < CommandType.values().length; i++) {
            if (CommandType.values()[i].getCommandType().equals(ordinal)) {
                return CommandType.values()[i];
            }
        }
        return null;
    }
}
