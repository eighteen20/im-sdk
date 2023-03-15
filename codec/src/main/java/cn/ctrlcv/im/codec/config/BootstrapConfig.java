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

        /**
         * TCP 端口
         */
        private Integer tcpPort;

        /**
         * webSocket 端口
         */
        private Integer webSocketPort;

        /**
         * 主线程数
         */
        private Integer bossThreadSize;

        /**
         *
         * 工作线程数
         */
        private Integer workThreadSize;

        /**
         * 心跳超时时间(毫秒)
         */
        private Long heartBeatTime;

        /**
         * redis 配置文件
         */
        private RedisConfig redis;

        /**
         * redis 配置文件
         */
        private Rabbitmq rabbitmq;

        /**
         * zk配置
         */
        private ZkConfig zkConfig;


        /**
         * 分布式环境下， 用于区分服务， 不可重复
         */
        private Integer brokerId;


        /**
         * <p>
         *     <p>多端同步模式：</p>
         *     <p>1 - 只允许一端在线，手机/电脑/web 踢掉除了本client+imel的设备</p>
         *     <p>2 - 允许手机/电脑的一台设备 + web在线 踢掉除了本client+imel的非web端设备</p>
         *     <p>3 - 允许手机和电脑单设备 + web 同时在线 踢掉非本client+imel的同端设备</p>
         *     <p>4 - 允许所有端多设备登录 不踢任何设备</p>
         * </p>
         * TODO 以后可以配置到表里（appId + 配置）
         */
        private Integer loginModel;
    }


    @Data
    public static class RedisConfig {

        /**
         * 单机模式：single 哨兵模式：sentinel 集群模式：cluster
         */
        private String mode;
        /**
         * 数据库
         */
        private Integer database;
        /**
         * 密码
         */
        private String password;
        /**
         * 超时时间
         */
        private Integer timeout;
        /**
         * 最小空闲数
         */
        private Integer poolMinIdle;
        /**
         * 连接超时时间(毫秒)
         */
        private Integer poolConnTimeout;
        /**
         * 连接池大小
         */
        private Integer poolSize;

        /**
         * redis单机配置
         */
        private RedisSingle single;

        /**
         * rabbitMQ 配置
         */
        private Rabbitmq rabbitmq;

    }

    /**
     * redis单机配置
     */
    @Data
    public static class RedisSingle {
        /**
         * 地址
         */
        private String address;
    }


    /**
     * rabbitmq哨兵模式配置
     */
    @Data
    public static class Rabbitmq {
        private String host;

        private Integer port;

        private String virtualHost;

        private String userName;

        private String password;
    }


    /**
     * ZooKeeper 配置
     */
    @Data
    public static class ZkConfig {
        /**
         * zk连接地址
         */
        private String zkAddr;

        /**
         * zk连接超时时间
         */
        private Integer zkConnectTimeOut;
    }

}
