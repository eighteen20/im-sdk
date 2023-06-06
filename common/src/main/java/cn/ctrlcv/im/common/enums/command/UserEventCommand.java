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
     * 用户资料修改通知  4000=0xFA0
     */
    USER_MODIFY(0xFA0),

    /**
     * 用户在线状态变更 4001=0xFA1
     */
    USER_ONLINE_STATUS_CHANGE(0xFA1),


    /**
     * 用户在线状态通知报文 4004=0xFA4
     */
    USER_ONLINE_STATUS_CHANGE_NOTIFY(0xFA4),

    /**
     * 用户在线状态通知同步报文 4005=0xFA5
     */
    USER_ONLINE_STATUS_CHANGE_NOTIFY_SYNC(0xFA5),
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
