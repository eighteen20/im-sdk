package cn.ctrlcv.im.codec.config;

import lombok.Data;

/**
 * Class Name: BootstrapConfig
 * Class Description: IM SDK 配置
 *
 * @author liujm
 * @date 2023-03-13
 */
@Data
public class BootstrapConfig {

    private TcpConfig im;

    @Data
    public static class TcpConfig {
        private Integer tcpPort;
        private Integer webSocketPort;
        private Integer bossThreadSize;
        private Integer workThreadSize;
    }

}
