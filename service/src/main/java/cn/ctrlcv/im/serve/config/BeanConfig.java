package cn.ctrlcv.im.serve.config;

import cn.ctrlcv.im.common.config.ImConfig;
import cn.ctrlcv.im.common.route.RouteHandler;
import cn.ctrlcv.im.common.route.algorithm.random.RandomHandler;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

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
     *
     *
     * @return
     */
    @Bean
    public RouteHandler routeHandler() {
        return new RandomHandler();
    }

    @Bean
    public ZkClient buildZKClient() {
        return new ZkClient(imConfig.getZkAddr(), imConfig.getZkConnectTimeOut());
    }
}
