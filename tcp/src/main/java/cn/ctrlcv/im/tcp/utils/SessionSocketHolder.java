package cn.ctrlcv.im.tcp.utils;

import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.ImConnectStatusEnum;
import cn.ctrlcv.im.common.model.UserClientDTO;
import cn.ctrlcv.im.common.model.UserSession;
import cn.ctrlcv.im.tcp.redis.RedisManager;
import com.alibaba.fastjson.JSONObject;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import org.apache.commons.lang3.StringUtils;
import org.redisson.api.RMap;
import org.redisson.api.RedissonClient;

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

    private static final Map<UserClientDTO, NioSocketChannel> CHANNEL_MAP = new ConcurrentHashMap<>();

    /**
     * 保存channel
     *
     * @param appId      应用ID
     * @param clientType 客户端类型
     * @param userId     用户ID
     * @param channel    {@link NioSocketChannel}
     */
    public static void put(Integer appId, Integer clientType, String userId, NioSocketChannel channel) {
        UserClientDTO clientDTO = new UserClientDTO();
        clientDTO.setAppId(appId);
        clientDTO.setClientType(clientType);
        clientDTO.setUserId(userId);
        CHANNEL_MAP.put(clientDTO, channel);
    }

    /**
     * 获取channel
     *
     * @param appId      应用ID
     * @param clientType 客户端类型
     * @param userId     用户ID
     * @return {@link NioSocketChannel}
     */
    public static NioSocketChannel get(Integer appId, Integer clientType, String userId) {
        UserClientDTO clientDTO = new UserClientDTO();
        clientDTO.setAppId(appId);
        clientDTO.setClientType(clientType);
        clientDTO.setUserId(userId);
        return CHANNEL_MAP.get(clientDTO);
    }


    /**
     * 移除channel,在用户登出时
     *
     * @param appId      应用ID
     * @param clientType 客户端类型
     * @param userId     用户ID
     */
    public static void remove(Integer appId, Integer clientType, String userId) {
        UserClientDTO clientDTO = new UserClientDTO();
        clientDTO.setAppId(appId);
        clientDTO.setClientType(clientType);
        clientDTO.setUserId(userId);
        CHANNEL_MAP.remove(clientDTO);
    }

    /**
     * 移除channel,在用户登出时
     *
     * @param channel {@link NioSocketChannel}
     */
    public static void remove(NioSocketChannel channel) {
        CHANNEL_MAP.entrySet().stream().filter(e -> e.getValue() == channel).forEach(e -> CHANNEL_MAP.remove(e.getKey()));
    }

    /**
     * 用户离线
     *
     * @param channel {@link NioSocketChannel}
     */
    public static void offlineUserSession(NioSocketChannel channel) {
        String userId = (String) channel.attr(AttributeKey.valueOf(Constants.USER_ID)).get();
        Integer appId = (Integer) channel.attr(AttributeKey.valueOf(Constants.APP_ID)).get();
        Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();

        SessionSocketHolder.remove(appId, clientType, userId);

        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<Object, Object> map = redissonClient.getMap(appId + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId);
        String session = map.get(clientType).toString();

        if (StringUtils.isNotBlank(session)) {
            UserSession userSession = JSONObject.parseObject(session, UserSession.class);
            userSession.setConnectState(ImConnectStatusEnum.OFFLINE_STATUS.getCode());
            map.put(clientType.toString(), JSONObject.toJSONString(userSession));
        }

        channel.close();
    }


    /**
     * 退出后台
     *
     * @param channel {@link NioSocketChannel}
     */
    public static void removeUserSession(NioSocketChannel channel) {
        String userId = (String) channel.attr(AttributeKey.valueOf(Constants.USER_ID)).get();
        Integer appId = (Integer) channel.attr(AttributeKey.valueOf(Constants.APP_ID)).get();
        Integer clientType = (Integer) channel.attr(AttributeKey.valueOf(Constants.CLIENT_TYPE)).get();

        SessionSocketHolder.remove(appId, clientType, userId);

        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<Object, Object> map = redissonClient.getMap(appId + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId);
        map.remove(clientType);

        channel.close();
    }


}
