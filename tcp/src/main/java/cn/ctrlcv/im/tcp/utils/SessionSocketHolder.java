package cn.ctrlcv.im.tcp.utils;

import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class Name: SessionSocketHolder
 * Class Description: 管理Channel
 *
 * @author liujm
 * @date 2023-03-13
 */
public class SessionSocketHolder {

    private static  final Map<String, NioSocketChannel> CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 保存channel
     *
     * @param userId 用户ID
     * @param channel {@link NioSocketChannel}
     */
    public static void put(String userId, NioSocketChannel channel) {
        CHANNEL_MAP.put(channel.remoteAddress().getHostName(), channel);
    }

    /**
     * 获取channel
     *
     * @param userId 用户ID
     * @return {@link NioSocketChannel}
     */
    public static NioSocketChannel get(String userId) {
        return CHANNEL_MAP.get(userId);
    }
}
