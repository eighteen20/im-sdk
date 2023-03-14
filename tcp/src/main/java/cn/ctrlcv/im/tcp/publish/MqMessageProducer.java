package cn.ctrlcv.im.tcp.publish;

import cn.ctrlcv.im.tcp.utils.MqFactory;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Class Name: MqMessageProducer
 * Class Description: Mq 往逻辑层投递消息
 *
 * @author liujm
 * @date 2023-03-14
 */
@Slf4j
public class MqMessageProducer {

    /**
     * 发送消息
     *
     * @param message 各类消息
     */
    public static void sendMessage(Object message) {
        Channel channel = null;
        // TODO 后面补充
        String channelName = "";

        try {
            channel = MqFactory.getChannel(channelName);
            channel.basicPublish(channelName, "", null, JSONObject.toJSONString(message).getBytes());
        } catch (IOException | TimeoutException e) {
            log.error("消息投递异常：{}", e.getMessage());
        }
    }
}
