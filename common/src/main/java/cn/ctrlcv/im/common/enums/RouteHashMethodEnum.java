package cn.ctrlcv.im.common.enums;

/**
 * enum Name: RouteHashMethodEnum
 * enum Description: 一致性hash算法使用的Map枚举
 *
 * @author liujm
 * @date 2023-03-16
 */
public enum RouteHashMethodEnum {

    /**
     * TreeMap
     */
    TREE(1, "cn.ctrlcv.im.common.route.algorithm.random.consistenthash.TreeMapConsistentHash"),

    /*
     * 自定义map: CUSTOMER(2, "cn.ctrlcv.im.common.route.algorithm.random.consistenthash.xxxxx")
     */

    ;


    private int code;
    private String clazz;

    /**
     * 不能用 默认的 enumType b= enumType.values()[i]; 因为本枚举是类形式封装
     *
     * @param ordinal
     * @return
     */
    public static RouteHashMethodEnum getHandler(int ordinal) {
        for (int i = 0; i < RouteHashMethodEnum.values().length; i++) {
            if (RouteHashMethodEnum.values()[i].getCode() == ordinal) {
                return RouteHashMethodEnum.values()[i];
            }
        }
        return null;
    }

    RouteHashMethodEnum(int code, String clazz) {
        this.code = code;
        this.clazz = clazz;
    }

    public String getClazz() {
        return clazz;
    }

    public int getCode() {
        return code;
    }
}
