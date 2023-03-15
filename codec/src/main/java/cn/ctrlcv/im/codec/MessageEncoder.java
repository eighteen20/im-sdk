package cn.ctrlcv.im.codec;

import cn.ctrlcv.im.codec.pack.MessagePack;
import com.alibaba.fastjson.JSONObject;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * Class Name: MessageEncoder
 * Class Description: IM-SDK 消息编码器
 * <p>私有协议规则，前4位表示长度，接着command4位，后面是数据</p>
 *
 * @author liujm
 * @date 2023-03-15
 */
public class MessageEncoder extends MessageToByteEncoder  {
    @Override
    protected void encode(ChannelHandlerContext context, Object msg, ByteBuf out) throws Exception {
        if(msg instanceof MessagePack){
            MessagePack msgBody = (MessagePack) msg;
            String s = JSONObject.toJSONString(msgBody.getData());
            byte[] bytes = s.getBytes();
            out.writeInt(msgBody.getCommand());
            out.writeInt(bytes.length);
            out.writeBytes(bytes);
        }
    }
}
