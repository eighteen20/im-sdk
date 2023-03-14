package cn.ctrlcv.im.tcp.handler;

import cn.ctrlcv.im.codec.pack.LoginPack;
import cn.ctrlcv.im.codec.proto.Message;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.ImConnectStatusEnum;
import cn.ctrlcv.im.common.enums.command.SystemCommand;
import cn.ctrlcv.im.common.model.UserSession;
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
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

/**
 * Class Name: NettyServerHandler
 * Class Description: TODO
 *
 * @author liujm
 * @date 2023-03-13
 */
@Slf4j
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {
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

            UserSession userSession = new UserSession();
            userSession.setAppId(msg.getMessageHeader().getAppId());
            userSession.setClientType(msg.getMessageHeader().getClientType());
            userSession.setUserId(loginPack.getUserId());
            userSession.setConnectState(ImConnectStatusEnum.ONLINE_STATUS.getCode());

            // 存入Redis
            RedissonClient redissonClient = RedisManager.getRedissonClient();
            RMap<String, String> map = redissonClient.getMap(msg.getMessageHeader().getAppId() + Constants.RedisConstants.USER_SESSION_CONSTANTS + loginPack.getUserId());
            map.put(String.valueOf(msg.getMessageHeader().getClientType()), JSONObject.toJSONString(userSession));

            // 将channel 存起来
            SessionSocketHolder.put(msg.getMessageHeader().getAppId(), msg.getMessageHeader().getClientType(), loginPack.getUserId(), (NioSocketChannel) context.channel());
        } else if (SystemCommand.LOGOUT.getCommand() == command) {

            String userId = (String) context.channel().attr(AttributeKey.valueOf(Constants.USER_ID)).get();
            Integer appId = (Integer) context.channel().attr(AttributeKey.valueOf(Constants.APP_ID)).get();
            Integer clientType = (Integer) context.channel().attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();
            // 删除channel
            SessionSocketHolder.remove(appId, clientType, userId);
            // 清空redis中session
            RedissonClient redissonClient = RedisManager.getRedissonClient();
            RMap<Object, Object> map = redissonClient.getMap(appId + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId);
            map.remove(clientType);

            context.channel().close();
        }


    }
}
