package cn.ctrlcv.im.common.enums;

/**
 * enum Name: ImConnectStatusEnum
 * enum Description: 用户连接状态枚举
 *
 * @author liujm
 * @date 2023-03-14
 */
public enum ImConnectStatusEnum {

    /**
     * 管道链接状态,1=在线，2=离线。。
     */
    ONLINE_STATUS(1),

    OFFLINE_STATUS(2),
    ;

    private Integer code;

    ImConnectStatusEnum(Integer code){
        this.code=code;
    }

    public Integer getCode() {
        return code;
    }

}
