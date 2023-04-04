package cn.ctrlcv.im.common.enums.command;

/**
 * enum Name: ConversationEventCommand
 * enum Description: 会话事件命令
 *
 * @author liujm
 * @date 2023-04-04
 */
public enum ConversationEventCommand implements Command {

    /**
     * 删除会话 5000 = 0x1388
     */
    CONVERSATION_DELETE(0x1388),

    /**
     * 更新会话 5001 = 0x1389
     */
    CONVERSATION_UPDATE(0x1389),

    ;

    private int command;

    ConversationEventCommand(int command){
        this.command=command;
    }

    @Override
    public int getCommand() {
        return command;
    }
}
