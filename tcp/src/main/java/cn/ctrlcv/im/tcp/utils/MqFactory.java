package cn.ctrlcv.im.tcp.utils;

import cn.ctrlcv.im.codec.config.BootstrapConfig;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeoutException;

/**
 * Class Name: MqFactory
 * Class Description: 初始化MQ
 *
 * @author liujm
 * @date 2023-03-14
 */
@Slf4j
public class MqFactory {

    private static ConnectionFactory factory = null;

    private static Channel defaultChannel;

    private static ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    /**
     * 初始化RabbitMQ
     *
     * @param rabbitmq {@link BootstrapConfig.Rabbitmq}
     */
    public static void init(BootstrapConfig.Rabbitmq rabbitmq) {
        if (factory == null) {
            factory = new ConnectionFactory();
            factory.setHost(rabbitmq.getHost());
            factory.setPort(rabbitmq.getPort());
            factory.setUsername(rabbitmq.getUserName());
            factory.setPassword(rabbitmq.getPassword());
            factory.setVirtualHost(rabbitmq.getVirtualHost());
            log.info(" ========== RabbitMQ 初始化成功  ==========");
        }
    }


    /**
     * 获取Channel
     *
     * @param channelName
     * @return
     * @throws IOException
     * @throws TimeoutException
     */
    public static Channel getChannel(String channelName) throws IOException, TimeoutException {
        Channel channel = channelMap.get(channelName);
        if (channel == null) {
            channel = getConnection().createChannel();
            channelMap.put(channelName, channel);
        }
        return channel;
    }


    private static Connection getConnection() throws IOException, TimeoutException {
        return factory.newConnection();
    }


}
