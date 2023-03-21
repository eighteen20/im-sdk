package cn.ctrlcv.im.common.enums;

import cn.ctrlcv.im.common.exception.ApplicationExceptionEnum;

/**
 * enum Name: MessageErrorCode
 * enum Description: 消息错误码
 *
 * @author liujm
 * @date 2023-03-21
 */
public enum MessageErrorCode implements ApplicationExceptionEnum {

    /**
     * 50002 发送方被禁言
     */
    FROMER_IS_MUTE(50002,"发送方被禁言"),

    /**
     * 50003 发送方被禁用
     */
    FROMER_IS_FORBIDDEN(50003,"发送方被禁用"),

    /**
     * 50004 接收方被禁言
     */
    MESSAGE_BODY_IS_NOT_EXIST(50004,"消息体不存在"),

    /**
     * 50005 接收方被禁用
     */
    MESSAGE_RECALL_TIME_OUT(50005,"消息已超过可撤回时间"),

    /**
     * 50006 消息已被撤回
     */
    MESSAGE_IS_RECALLED(50006,"消息已被撤回"),

    ;

    private int code;
    private String error;

    MessageErrorCode(int code, String error){
        this.code = code;
        this.error = error;
    }
    public int getCode() {
        return this.code;
    }

    public String getError() {
        return this.error;
    }

}
