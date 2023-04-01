package cn.ctrlcv.im.common.enums;

/**
 * enum Name: ConversationTypeEnum
 * enum Description: 会话类型枚举
 *
 * @author liujm
 * @date 2023-04-02
 */
public enum ConversationTypeEnum {

    /**
     * 0 单聊
     */
    P2P(0),

    /**
     * 1群聊
     */
    GROUP(1),

    /**
     * 2机器人
     */
    ROBOT(2),

    /**
     * 3公众号
     */
    OFFICIAL_ACCOUNT(3)
    ;

    private int code;

    ConversationTypeEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }

}
