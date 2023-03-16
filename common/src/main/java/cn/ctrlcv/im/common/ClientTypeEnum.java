package cn.ctrlcv.im.common;

/**
 * enum Name: ClientTypeEnum
 * enum Description: 客户端类型枚举
 *
 * @author liujm
 * @date 2023-03-15
 */
public enum ClientTypeEnum {

    /**
     *
     */
    WEBAPI(0,"webApi"),
    WEB(1,"web"),
    IOS(2,"ios"),
    ANDROID(3,"android"),
    WINDOWS(4,"windows"),
    MAC(5,"mac"),
    ;

    private int code;
    private String name;

    ClientTypeEnum(int code, String name){
        this.code = code;
        this.name = name;
    }
    public int getCode() {
        return this.code;
    }

    public String getName() {
        return this.name;
    }

}
