package cn.ctrlcv.im.common.enums;

import cn.ctrlcv.im.common.exception.ApplicationExceptionEnum;

/**
 * enum Name: ConversationErrorCodeEnum
 * enum Description: 会话错误码枚举
 *
 * @author liujm
 * @date 2023-04-04
 */
public enum ConversationErrorCodeEnum  implements ApplicationExceptionEnum {

    /**
     * 会话修改参数错误
     */
    CONVERSATION_UPDATE_PARAM_ERROR(50000, "会话修改参数错误"),

    ;

    private int code;
    private String error;

    ConversationErrorCodeEnum(int code, String error){
        this.code = code;
        this.error = error;
    }

    @Override
    public int getCode() {
            return 0;
    }

    @Override
    public String getError() {
        return null;
    }
}
