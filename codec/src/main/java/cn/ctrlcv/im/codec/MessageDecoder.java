package cn.ctrlcv.im.codec;

import cn.ctrlcv.im.codec.proto.Message;
import cn.ctrlcv.im.codec.utils.ByteBufToMessageUtils;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;

import java.util.List;

/**
 * Class Name: MessageDecoder
 * Class Description: IM-SDK 消息解码器
 *
 * @author liujm
 * @date 2023-03-13
 */
public class MessageDecoder extends ByteToMessageDecoder {
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf in, List<Object> out) throws Exception {
        if (in.readableBytes() < 28) {
            return;
        }

        Message message = ByteBufToMessageUtils.transition(in);
        if(message == null){
            return;
        }

        out.add(message);
    }
}
