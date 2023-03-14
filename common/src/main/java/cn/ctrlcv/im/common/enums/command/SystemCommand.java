package cn.ctrlcv.im.common.enums.command;

/**
 * enum Name: SystemCommand
 * enum Description: 系统指令
 *
 * @author liujm
 * @date 2023-03-13
 */
public enum SystemCommand implements Command {
    /**
     * 登录 9000
     */
    LOGIN(0x2328);

    private int command;

    SystemCommand(int command) {
        this.command = command;
    }


    @Override
    public int getCommand() {
        return command;
    }
}
