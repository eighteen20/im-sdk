package cn.ctrlcv.im.common.enums;

/**
 * enum Name: ImUrlRouteWayEnum
 * enum Description: 路由选择的负载均衡算法枚举
 *
 * @author liujm
 * @date 2023-03-16
 */
public enum ImUrlRouteWayEnum {

    /**
     * 随机
     */
    RANDOM(1,"cn.ctrlcv.im.common.route.algorithm.random.random.RandomHandle"),


    /**
     *  轮训
     */
    LOOP(2,"cn.ctrlcv.im.common.route.algorithm.random.loop.LoopHandle"),

    /**
     * HASH
     */
    HASH(3,"cn.ctrlcv.im.common.route.algorithm.random.consistenthash.ConsistentHashHandle"),
    ;


    private int code;
    private String clazz;

    /**
     * 不能用 默认的 enumType b= enumType.values()[i]; 因为本枚举是类形式封装
     * @param ordinal
     * @return
     */
    public static ImUrlRouteWayEnum getHandler(int ordinal) {
        for (int i = 0; i < ImUrlRouteWayEnum.values().length; i++) {
            if (ImUrlRouteWayEnum.values()[i].getCode() == ordinal) {
                return ImUrlRouteWayEnum.values()[i];
            }
        }
        return null;
    }

    ImUrlRouteWayEnum(int code, String clazz){
        this.code=code;
        this.clazz=clazz;
    }

    public String getClazz() {
        return clazz;
    }

    public int getCode() {
        return code;
    }

}
