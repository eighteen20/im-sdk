package cn.ctrlcv.im.common.route;

import java.util.List;

/**
 * interface Name: RouteHandler
 * interface Description: TODO
 *
 * @author liujm
 * @date 2023-03-15
 */
public interface RouteHandler {

    /**
     * 获取一个注册在 zookeeper 中的服务地址
     *
     * @param values 可选的服务地址
     * @param key 获取服务地址的依据
     * @return 服务地址
     */
    String routerServer(List<String> values, String key);
}
