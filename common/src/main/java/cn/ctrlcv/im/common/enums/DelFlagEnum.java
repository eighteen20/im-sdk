package cn.ctrlcv.im.common.enums;

/**
 * Class Name: DelFlagEnum
 * Class Description: 数据伤处标志枚举类
 *
 * @author liujm
 * @date 2023-02-08
 */
public enum DelFlagEnum {

    /**
     * 0 正常；1 删除。
     */
    NORMAL(0),

    DELETE(1),
    ;

    private int code;

    DelFlagEnum(int code){
        this.code=code;
    }

    public int getCode() {
        return code;
    }

}
