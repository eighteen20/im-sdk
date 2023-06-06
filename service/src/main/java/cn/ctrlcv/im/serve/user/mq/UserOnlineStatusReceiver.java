package cn.ctrlcv.im.serve.user.mq;

import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.command.UserEventCommand;
import cn.ctrlcv.im.serve.user.model.UserStatusChangeNotifyContent;
import cn.ctrlcv.im.serve.user.service.IUserStatusService;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.rabbitmq.client.Channel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.core.Message;
import org.springframework.amqp.rabbit.annotation.*;
import org.springframework.amqp.support.AmqpHeaders;
import org.springframework.messaging.handler.annotation.Headers;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Objects;

/**
 * Class Name: UserOnlineStatusReceiver
 * Class Description: 用户在线状态变更接收
 *
 * @author liujm
 * @date 2023-06-05
 */
@Slf4j
@Component
public class UserOnlineStatusReceiver {

    @Resource
    private IUserStatusService userStatusService;


    /**
     * 订阅MQ单聊消息队列--处理
     *
     * @param message 消息体
     * @param headers 消息头
     * @param channel 通道
     * @throws Exception 异常
     */
    @RabbitListener(bindings = @QueueBinding(
            value = @Queue(value = Constants.RabbitConstants.IM_2_USER_SERVICE, durable = "true"),
            exchange = @Exchange(value = Constants.RabbitConstants.IM_2_USER_SERVICE, durable = "true")
    ), concurrency = "1")
    @RabbitHandler
    public void onChatMessage(@Payload Message message, @Headers Map<String, Object> headers, Channel channel) 
            throws Exception {
        long start = System.currentTimeMillis();
        Thread t = Thread.currentThread();
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info("CHAT MSG FROM QUEUE :::::" + msg);
        //deliveryTag 用于回传 rabbitmq 确认该消息处理成功
        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        try {
            JSONObject jsonObject = JSON.parseObject(msg);
            Integer command = jsonObject.getInteger("command");
            if(Objects.equals(command, UserEventCommand.USER_ONLINE_STATUS_CHANGE.getCommand())){
                UserStatusChangeNotifyContent content = JSON.parseObject(msg,
                        new TypeReference<UserStatusChangeNotifyContent>() {}.getType());
                userStatusService.processUserOnlineStatusNotify(content);
            }


            channel.basicAck(deliveryTag,false);
        }catch (Exception e){
            log.error("处理消息出现异常：{}",e.getMessage());
            log.error("RMQ_CHAT_TRAN_ERROR", e);
            log.error("NACK_MSG:{}", msg);
            //第一个false 表示不批量拒绝，第二个false表示不重回队列
            channel.basicNack(deliveryTag, false, false);
        }finally {
            long end = System.currentTimeMillis();
            log.debug("channel {} basic-Ack ,it costs {} ms,threadName = {},threadId={}", channel, end - start, t.getName(), t.getId());
        }
    }

}
