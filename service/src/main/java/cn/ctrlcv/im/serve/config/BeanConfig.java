package cn.ctrlcv.im.serve.config;

import cn.ctrlcv.im.common.config.ImConfig;
import cn.ctrlcv.im.common.enums.ImUrlRouteWayEnum;
import cn.ctrlcv.im.common.enums.RouteHashMethodEnum;
import cn.ctrlcv.im.common.enums.SystemErrorEnum;
import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.common.route.RouteHandler;
import cn.ctrlcv.im.common.route.algorithm.random.consistenthash.AbstractConsistentHash;
import cn.hutool.core.util.ObjectUtil;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Class Name: BeanConfig
 * Class Description:初始化自定义配置, 依赖注入
 *
 * @author liujm
 * @date 2023-03-15
 */
@Configuration
public class BeanConfig {

    private final ImConfig imConfig;

    public BeanConfig(ImConfig imConfig) {
        this.imConfig = imConfig;
    }

    /**
     * 注册路由负载均衡算法
     *
     * @return {@link RouteHandler}
     */
    @Bean
    public RouteHandler routeHandler() {
        Integer imRouteWay = this.imConfig.getImRouteWay();
        String way;
        ImUrlRouteWayEnum handler = ImUrlRouteWayEnum.getHandler(imRouteWay);

        assert handler != null;
        way = handler.getClazz();
        RouteHandler routeHandler;
        try {
            Class<?> aClass = Class.forName(way);
            routeHandler = (RouteHandler) aClass.newInstance();
            if (handler == ImUrlRouteWayEnum.HASH) {
                Method setHash = aClass.getMethod("setHash", AbstractConsistentHash.class);
                Integer consistentHashWay = this.imConfig.getConsistentHashWay();
                String hashWay;

                RouteHashMethodEnum routeHash = RouteHashMethodEnum.getHandler(consistentHashWay);
                assert routeHash != null;
                hashWay = routeHash.getClazz();
                AbstractConsistentHash hash = (AbstractConsistentHash) Class.forName(hashWay).newInstance();

                setHash.invoke(routeHandler, hash);
            }
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | NoSuchMethodException |
                 InvocationTargetException e) {
            throw new RuntimeException(e);
        }

        return routeHandler;
    }

    @Bean
    public ZkClient buildZkClient() {
        return new ZkClient(imConfig.getZkAddr(), imConfig.getZkConnectTimeOut());
    }
}
