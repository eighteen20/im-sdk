package cn.ctrlcv.im.common.enums;

/**
 * enum Name: MessageTypeEnum
 * enum Description: 私有协议-消息头-消息类型枚举
 * <p>默认类型：JSON</p>
 *
 * @author liujm
 * @date 2023-03-13
 */
public enum MessageTypeEnum  {

    /**
     * JSON类型消息
     */
    JSON(0x0, "JSON"),

    /**
     * protoBuf类型消息
     */
    PROTO_BUF(0x1, "protoBuf"),

    /**
     * xml类型消息
     */
    XML(0x2, "XML")
    ;

    private int type;
    private String typeName;

    MessageTypeEnum(int type, String typeName) {
        this.type = type;
        this.typeName = typeName;
    }

    public int getType() {
        return type;
    }
}
