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


}
