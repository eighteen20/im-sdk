package cn.ctrlcv.im.serve.message.mq;

import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.command.GroupEventCommand;
import cn.ctrlcv.im.common.enums.command.MessageCommand;
import cn.ctrlcv.im.common.model.message.GroupChatMessageContent;
import cn.ctrlcv.im.common.model.message.MessageReadedContent;
import cn.ctrlcv.im.serve.message.service.GroupMessageService;
import cn.ctrlcv.im.serve.message.service.MessageSyncService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.Exchange;
import org.springframework.amqp.rabbit.annotation.Queue;
import org.springframework.amqp.rabbit.annotation.QueueBinding;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Class Name: GroupChatOperateReceiver
 * Class Description: 群聊消息接收分发
 *
 * @author liujm
 * @date 2023-03-22
 */
@Slf4j
@Component
public class GroupChatOperateReceiver {

    @Resource
    private GroupMessageService groupMessageService;

    @Resource
    private MessageSyncService messageSyncService;

    /**
     * 监听订阅的MQ消息
     *
     * @param message
     * @param headers
     * @param channel
     */
    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = Constants.RabbitConstants.IM_2_GROUP_SERVICE, durable = "true"),
                    exchange = @Exchange(value = Constants.RabbitConstants.IM_2_GROUP_SERVICE, type = "topic", durable = "true")
            ),
            concurrency = "1"
    )
    public void onChatMessage(@Payload Message message, @Header Map<String, Object> headers, Channel channel) throws IOException {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info(" ===== CHAT MESSAGE FROM QUEUE：{}", msg);

        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        try {

            JSONObject jsonObject = JSON.parseObject(msg);
            Integer command = jsonObject.getInteger("command");

            if (command.equals(GroupEventCommand.MSG_GROUP.getCommand())) {
                // 处理消息
                GroupChatMessageContent messageContent = jsonObject.toJavaObject(GroupChatMessageContent.class);
                groupMessageService.process(messageContent);
            } else if (command.equals(GroupEventCommand.MSG_GROUP_READED.getCommand())) {
                MessageReadedContent readedContent = jsonObject.toJavaObject(MessageReadedContent.class);
                messageSyncService.groupReadedMark(readedContent);
            }
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理群聊消息出现异常：{}", e.getMessage());
            log.error("RMQ_CHAT_TRAN_ERROR", e);
            log.error("NACK_MSG:{}", msg);
            //第一个false 表示不批量拒绝，第二个false表示不重回队列
            channel.basicNack(deliveryTag, false, false);
        }
    }

}
