package cn.ctrlcv.im.tcp.receiver;

import cn.ctrlcv.im.codec.pack.MessagePack;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.tcp.receiver.process.BaseProcess;
import cn.ctrlcv.im.tcp.receiver.process.ProcessFactory;
import cn.ctrlcv.im.tcp.utils.MqFactory;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.io.IOException;
import java.util.concurrent.TimeoutException;

/**
 * Class Name: MessageReceiver
 * Class Description: MQ 监听逻辑层的消息投递
 *
 * @author liujm
 * @date 2023-03-14
 */
@Slf4j
public class MessageReceiver {

    /**
     * 服务节点编号
     */
    private static Integer brokerId;


    /**
     * 监听消息，接收消息
     */
    private static void startReceiverMessage() {
        try {
            Channel channel = MqFactory.getChannel(Constants.RabbitConstants.MESSAGE_SERVICE_2_IM + brokerId);
            channel.queueDeclare(Constants.RabbitConstants.MESSAGE_SERVICE_2_IM + brokerId, true, false, false, null);
            channel.queueBind(Constants.RabbitConstants.MESSAGE_SERVICE_2_IM + brokerId, Constants.RabbitConstants.MESSAGE_SERVICE_2_IM, String.valueOf(brokerId));
            channel.basicConsume(Constants.RabbitConstants.MESSAGE_SERVICE_2_IM + brokerId, false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    try {
                        MessagePack messagePack = JSONObject.parseObject(new String(body), MessagePack.class);
                        BaseProcess process = ProcessFactory.getMessageProcess(messagePack.getCommand());
                        process.processMessage(messagePack);
                        channel.basicAck(envelope.getDeliveryTag(), false);
                    } catch (IOException e) {
                        e.printStackTrace();
                        channel.basicNack(envelope.getDeliveryTag(), false, false);
                    }
                }
            });
        } catch (IOException | TimeoutException e) {
            log.error("消息接收异常：{}", e.getMessage());

            e.printStackTrace();
        }
    }

    public static void init(Integer brokerId) {
        if (ObjectUtils.isEmpty(brokerId)) {
            MessageReceiver.brokerId = brokerId;
        }
        startReceiverMessage();
    }

    /**
     * 测试用
     */
    public static void init() {
        startReceiverMessage();
    }

}
