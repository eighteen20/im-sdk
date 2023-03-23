package cn.ctrlcv.im.messagestore.mq;

import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.messagestore.dao.ImMessageBodyEntity;
import cn.ctrlcv.im.messagestore.model.DoStoreP2pMessageDTO;
import cn.ctrlcv.im.messagestore.service.StoreMessageService;
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
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Map;

/**
 * Class Name: StroeP2PMessageReceiver
 * Class Description: 单聊消息持久化接收器
 *
 * @author liujm
 * @date 2023-03-23
 */
@Slf4j
@Service
public class StoreP2pMessageReceiver {

    @Resource
    private StoreMessageService storeMessageService;

    @RabbitListener(
            bindings = @QueueBinding(
                    value = @Queue(value = Constants.RabbitConstants.STORE_P2P_MESSAGE, durable = "true"),
                    exchange = @Exchange(value = Constants.RabbitConstants.STORE_P2P_MESSAGE, type = "topic", durable = "true")
            ),
            concurrency = "1"
    )
    public void onChatMessage(@Payload Message message, @Header Map<String, Object> headers, Channel channel) throws IOException {
        String msg = new String(message.getBody(), StandardCharsets.UTF_8);
        log.info(" ===== CHAT MESSAGE FROM QUEUE：{}", msg);

        Long deliveryTag = (Long) headers.get(AmqpHeaders.DELIVERY_TAG);

        try {
            JSONObject jsonObject = JSON.parseObject(msg);
            ImMessageBodyEntity messageBody = jsonObject.getObject("messageBody", ImMessageBodyEntity.class);
            DoStoreP2pMessageDTO storeP2pMessageDTO = jsonObject.toJavaObject(DoStoreP2pMessageDTO.class);
            storeP2pMessageDTO.setImMessageBodyEntity(messageBody);
            storeMessageService.doStoreP2pMessage(storeP2pMessageDTO);
            channel.basicAck(deliveryTag, false);
        } catch (Exception e) {
            log.error("处理消息出现异常：{}", e.getMessage());
            log.error("RMQ_CHAT_TRAN_ERROR", e);
            log.error("NACK_MSG:{}", msg);
            //第一个false 表示不批量拒绝，第二个false表示不重回队列
            channel.basicNack(deliveryTag, false, false);
        }
    }


}
