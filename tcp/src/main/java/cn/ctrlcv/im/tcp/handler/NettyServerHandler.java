package cn.ctrlcv.im.tcp.handler;

import cn.ctrlcv.im.codec.pack.LoginPack;
import cn.ctrlcv.im.codec.proto.Message;
import cn.ctrlcv.im.common.enums.command.SystemCommand;
import cn.ctrlcv.im.tcp.utils.SessionSocketHolder;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.type.TypeReference;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

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
                    new TypeReference<LoginPack>() { }.getType());
            context.channel().attr(AttributeKey.valueOf("userId")).set(loginPack.getUserId());

            // 将channel 存起来
            SessionSocketHolder.put(loginPack.getUserId(), (NioSocketChannel) context.channel());
        }
    }
}
