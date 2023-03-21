package cn.ctrlcv.im.tcp.receiver.process;

import cn.ctrlcv.im.codec.pack.MessagePack;
import cn.ctrlcv.im.tcp.utils.SessionSocketHolder;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * Class Name: BaseProcess
 * Class Description: 消息处理基类
 *
 * @author liujm
 * @date 2023-03-21
 */
public abstract class BaseProcess {

    /**
     * 处理消息前需要的操作
     *
     */
    public abstract void processBefore();


    /**
     * 处理消息
     *
     * @param message 消息
     */
    public void processMessage(MessagePack<?> message) {
        processBefore();
        NioSocketChannel nioSocketChannel =
                SessionSocketHolder.get(message.getAppId(), message.getToId(), message.getClientType(), message.getImei());
        if (nioSocketChannel != null) {
            nioSocketChannel.writeAndFlush(message);
        }
        processAfter();
    }

    /**
     * 处理消息后需要的操作
     *
     */
    public abstract void processAfter();

}
