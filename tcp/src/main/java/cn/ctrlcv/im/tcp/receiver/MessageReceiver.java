package cn.ctrlcv.im.tcp.receiver;

import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.tcp.utils.MqFactory;
import com.rabbitmq.client.AMQP;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DefaultConsumer;
import com.rabbitmq.client.Envelope;
import lombok.extern.slf4j.Slf4j;

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
     * 监听消息，接收消息
     */
    private static void startReceiverMessage() {
        try {
            Channel channel = MqFactory.getChannel(Constants.RabbitConstants.MESSAGE_SERVICE_2_IM);
            channel.queueDeclare(Constants.RabbitConstants.MESSAGE_SERVICE_2_IM, true, false, false, null);
            channel.queueBind(Constants.RabbitConstants.MESSAGE_SERVICE_2_IM, Constants.RabbitConstants.MESSAGE_SERVICE_2_IM, "");
            channel.basicConsume(Constants.RabbitConstants.MESSAGE_SERVICE_2_IM, false, new DefaultConsumer(channel) {
                @Override
                public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
                    // TODO 处理接收到的消息
                    log.info(new String(body));
                }
            });
        } catch (IOException | TimeoutException e) {
            log.error("消息接收异常：{}", e.getMessage());

            e.printStackTrace();
        }
    }

    /**
     * 测试用
     */
    public static void init() {
        startReceiverMessage();
    }

}
