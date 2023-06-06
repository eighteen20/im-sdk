package cn.ctrlcv.im.tcp.publish;

import cn.ctrlcv.im.codec.proto.Message;
import cn.ctrlcv.im.codec.proto.MessageHeader;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.command.CommandType;
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
        String com = command.toString();
        String commandSub = com.substring(0, 1);
        CommandType commandType = CommandType.getCommandType(commandSub);
        String channelName = "";
        if (commandType == CommandType.MESSAGE) {
            channelName = Constants.RabbitConstants.IM_2_MESSAGE_SERVICE;
        } else if (commandType == CommandType.GROUP) {
            channelName = Constants.RabbitConstants.IM_2_GROUP_SERVICE;
        } else if (commandType == CommandType.FRIEND) {
            channelName = Constants.RabbitConstants.IM_2_FRIENDSHIP_SERVICE;
        } else if (commandType == CommandType.USER) {
            channelName = Constants.RabbitConstants.IM_2_USER_SERVICE;
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

    /**
     * 发送消息
     *
     * @param message 消息
     * @param header  消息头 {@link MessageHeader}
     * @param command 消息类型
     */
    public static void sendMessage(Object message, MessageHeader header, Integer command) {
        Channel channel;
        String com = command.toString();
        String commandSub = com.substring(0, 1);
        CommandType commandType = CommandType.getCommandType(commandSub);
        String channelName = "";
        if (commandType == CommandType.MESSAGE) {
            channelName = Constants.RabbitConstants.IM_2_MESSAGE_SERVICE;
        } else if (commandType == CommandType.GROUP) {
            channelName = Constants.RabbitConstants.IM_2_GROUP_SERVICE;
        } else if (commandType == CommandType.FRIEND) {
            channelName = Constants.RabbitConstants.IM_2_FRIENDSHIP_SERVICE;
        } else if (commandType == CommandType.USER) {
            channelName = Constants.RabbitConstants.IM_2_USER_SERVICE;
        }

        try {
            channel = MqFactory.getChannel(channelName);

            JSONObject o = (JSONObject) JSON.toJSON(message);
            o.put("command", command);
            o.put("clientType", header.getClientType());
            o.put("imei", header.getImei());
            o.put("appId", header.getAppId());
            channel.basicPublish(channelName, "",
                    null, o.toJSONString().getBytes());
        } catch (Exception e) {
            log.error("发送消息出现异常：{}", e.getMessage());
        }
    }
}
