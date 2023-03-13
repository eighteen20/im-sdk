package cn.ctrlcv.im.tcp.handler;

import cn.ctrlcv.im.codec.proto.Message;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

/**
 * Class Name: NettyServerHandler
 * Class Description: TODO
 *
 * @author liujm
 * @date 2023-03-13
 */
public class NettyServerHandler extends SimpleChannelInboundHandler<Message> {
    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, Message message) throws Exception {
        System.out.println(message);
    }
}
