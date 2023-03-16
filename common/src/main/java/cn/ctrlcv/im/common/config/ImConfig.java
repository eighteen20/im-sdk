package cn.ctrlcv.im.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Class Name: ImConfig
 * Class Description: 自定义配置
 *
 * @author liujm
 * @date 2023-03-15
 */
@Data
@Component
@ConfigurationProperties(prefix = "imconfig")
public class ImConfig {

    /**
     * zk连接地址
     */
    private String zkAddr;

    /**
     * zk连接超时时间
     */
    private Integer zkConnectTimeOut;

    /**
     * 路由的负载均衡算法, 1-随机，2-轮训，3-hash
     */
    private Integer imRouteWay = 3;

    /**
     * 如果选用一致性hash的话({@link ImConfig#getImRouteWay()} = 3).
     * 配置具体hash算法。1-TreeMap, 2-自定义Map
     */
    private Integer consistentHashWay = 1;


}
