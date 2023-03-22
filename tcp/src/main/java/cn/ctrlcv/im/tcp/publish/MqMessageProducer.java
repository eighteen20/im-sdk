package cn.ctrlcv.im.tcp.publish;

import cn.ctrlcv.im.codec.proto.Message;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.tcp.utils.MqFactory;
import com.alibaba.fastjson.JSON;
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
    public static void sendMessage(Message message, Integer command) {
        Channel channel;
        String channelName = Constants.RabbitConstants.IM_2_MESSAGE_SERVICE;

        if (command.toString().startsWith("2")) {
            // 群聊消息
            channelName = Constants.RabbitConstants.IM_2_GROUP_SERVICE;
        }

        try {
            channel = MqFactory.getChannel(channelName);

            JSONObject json = (JSONObject) JSON.toJSON(message.getMessagePack());
            json.put("command", command);
            json.put("clientType", message.getMessageHeader().getClientType());
            json.put("imei", message.getMessageHeader().getImei());
            json.put("appId", message.getMessageHeader().getAppId());

            channel.basicPublish(channelName, "", null, json.toJSONString().getBytes());
        } catch (IOException | TimeoutException e) {
            log.error("消息投递异常：{}", e.getMessage());
        }
    }
}
