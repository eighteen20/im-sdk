package cn.ctrlcv.im.common.enums.command;

/**
 * enum Name: FriendshipEventCommand
 * enum Description:  好友事件命令
 *
 * @author liujm
 * @date 2023-03-19
 */
public enum FriendshipEventCommand implements  Command {

    /**
     * 添加好友 3000 = 0x0BB8
     */
    FRIENDSHIP_ADD(0x0BB8),

    /**
     * 更新好友 3001 = 0x0BB9
     */
    FRIENDSHIP_UPDATE(0x0BB9),

    /**
     * 删除好友 3002 = 0x0BBA
     */
    FRIENDSHIP_DELETE(0x0BBA),

    /**
     * 好友申请 3003 = 0x0BBB
     */
    FRIENDSHIP_REQUEST(0x0BBB),

    /**
     * 好友申请已读 3004 = 0x0BBC
     */
    FRIENDSHIP_REQUEST_READ(0x0BBC),

    /**
     * 好友申请审批 3005 = 0x0BBD
     */
    FRIENDSHIP_REQUEST_APPROVAL(0x0BBD),

    /**
     * 添加黑名单 3010 = 0x0BC2
     */
    FRIENDSHIP_BLACK_ADD(0x0BC2),

    /**
     * 移除黑名单 3011 = 0x0BC3
     */
    FRIENDSHIP_BLACK_DELETE(0x0BC3),

    /**
     * 新建好友分组 3012 = 0x0BC4
     */
    FRIENDSHIP_GROUP_ADD(0x0BC4),

    /**
     * 删除好友分组 3013 = 0x0BC5
     */
    FRIENDSHIP_GROUP_DELETE(0x0BC5),

    /**
     * 好友分组添加成员 3014 = 0x0BC6
     */
    FRIENDSHIP_GROUP_MEMBER_ADD(0x0BC6),

    /**
     * 好友分组移除成员 3015 = 0x0BC7
     */
    FRIENDSHIP_GROUP_MEMBER_DELETE(0x0BC7),

    /**
     * 删除所有好友 3016 = 0x0BC8
     */
    FRIENDSHIP_ALL_DELETE(0x0BC8),
    ;

    private int command;

    FriendshipEventCommand(int command) {
        this.command = command;
    }

    @Override
    public int getCommand() {
        return 0;
    }
}
