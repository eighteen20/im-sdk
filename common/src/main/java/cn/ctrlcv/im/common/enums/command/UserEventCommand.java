package cn.ctrlcv.im.common.enums.command;

/**
 * enum Name: UserEventCommand
 * enum Description: 用户事件指令枚举
 *
 * @author liujm
 * @date 2023-03-19
 */
public enum UserEventCommand implements Command {
    /**
     * 用户资料修改通知  4000
     */
    USER_MODIFY(0xFA0),


    ;

    private int command;

    UserEventCommand(int command) {
        this.command = command;
    }

    @Override
    public int getCommand() {
        return 0;
    }
}
