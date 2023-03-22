package cn.ctrlcv.im.codec;

import cn.ctrlcv.im.codec.pack.MessagePack;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;
import io.netty.handler.codec.http.websocketx.BinaryWebSocketFrame;

import java.util.List;

/**
 * Class Name: WebSocketMessageEncoder
 * Class Description:
 *
 * @author liujm
 * @date 2023-03-22
 */
public class WebSocketMessageEncoder extends MessageToMessageEncoder<MessagePack> {
    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, MessagePack msg, List<Object> out) throws Exception {
        try {
            String s = JSONObject.toJSONString(msg);
            ByteBuf byteBuf = Unpooled.directBuffer(8 + s.length());
            byte[] bytes = s.getBytes();
            byteBuf.writeInt(msg.getCommand());
            byteBuf.writeInt(bytes.length);
            byteBuf.writeBytes(bytes);
            out.add(new BinaryWebSocketFrame(byteBuf));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
