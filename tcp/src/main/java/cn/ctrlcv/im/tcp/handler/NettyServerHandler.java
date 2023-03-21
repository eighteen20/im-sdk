package cn.ctrlcv.im.tcp.handler;

import cn.ctrlcv.im.codec.pack.LoginPack;
import cn.ctrlcv.im.codec.proto.Message;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.ImConnectStatusEnum;
import cn.ctrlcv.im.common.enums.command.SystemCommand;
import cn.ctrlcv.im.common.model.UserClientDTO;
import cn.ctrlcv.im.common.model.UserSession;
import cn.ctrlcv.im.tcp.publish.MqMessageProducer;
import cn.ctrlcv.im.tcp.redis.RedisManager;
import cn.ctrlcv.im.tcp.utils.SessionSocketHolder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.redisson.api.RMap;
import org.redisson.api.RTopic;
import org.redisson.api.RedissonClient;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Class Name: NettyServerHandler
 * Class Description: TODO
 *
 * @author liujm
 * @date 2023-03-13
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

    private Integer brokerId;

    public NettyServerHandler(Integer brokerId) {
        this.brokerId = brokerId;
    }

    @Override
    protected void channelRead0(ChannelHandlerContext context, Message msg) throws Exception {
        int command = msg.getMessageHeader().getCommand();

        if (SystemCommand.LOGIN.getCommand() == command) {

            LoginPack loginPack = JSON.parseObject(JSONObject.toJSONString(msg.getMessagePack()),
                    new TypeReference<LoginPack>() {
                    }.getType());
            context.channel().attr(AttributeKey.valueOf(Constants.USER_ID)).set(loginPack.getUserId());
            context.channel().attr(AttributeKey.valueOf(Constants.APP_ID)).set(msg.getMessageHeader().getAppId());
            context.channel().attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).set(msg.getMessageHeader().getClientType());
            context.channel().attr(AttributeKey.valueOf(Constants.IMEI)).set(msg.getMessageHeader().getImei());

            UserSession userSession = new UserSession();
            userSession.setAppId(msg.getMessageHeader().getAppId());
            userSession.setClientType(msg.getMessageHeader().getClientType());
            userSession.setUserId(loginPack.getUserId());
            userSession.setConnectState(ImConnectStatusEnum.ONLINE_STATUS.getCode());
            userSession.setBrokerId(this.brokerId);
            userSession.setImei(msg.getMessageHeader().getImei());

            try {
                userSession.setBrokerHost(InetAddress.getLocalHost().getHostAddress());
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }

            // 存入Redis
            RedissonClient redissonClient = RedisManager.getRedissonClient();
            RMap<String, String> map = redissonClient.getMap(msg.getMessageHeader().getAppId()
                    + Constants.RedisConstants.USER_SESSION_CONSTANTS + loginPack.getUserId());
            map.put(msg.getMessageHeader().getClientType() + ":" + msg.getMessageHeader().getImei(), JSONObject.toJSONString(userSession));

            // 将channel 存起来
            SessionSocketHolder.put(msg.getMessageHeader().getAppId(), msg.getMessageHeader().getClientType(),
                    loginPack.getUserId(), msg.getMessageHeader().getImei(), (NioSocketChannel) context.channel());

            /* redis 发布用户上线的消息，让其他服务检查是否需要有设备踢下线 */
            UserClientDTO dto = new UserClientDTO();
            dto.setImei(msg.getMessageHeader().getImei());
            dto.setAppId(msg.getMessageHeader().getAppId());
            dto.setClientType(msg.getMessageHeader().getClientType());
            dto.setUserId(loginPack.getUserId());
            RTopic topic = redissonClient.getTopic(Constants.RedisConstants.USER_LOGIN_CHANNEL);
            topic.publish(JSONObject.toJSONString(dto));

        } else if (SystemCommand.LOGOUT.getCommand() == command) {
            SessionSocketHolder.removeUserSession((NioSocketChannel) context.channel());
        } else if (SystemCommand.PING.getCommand() == command) {
            // 最后一次读写事件时间
            context.channel().attr(AttributeKey.valueOf(Constants.READ_TIME)).set(System.currentTimeMillis());
        } else {
            MqMessageProducer.sendMessage(msg, command);
        }

    }
}
