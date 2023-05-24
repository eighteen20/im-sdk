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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
     * @param imei       客户端设备唯一识别号
     * @param channel    {@link NioSocketChannel}
     */
    public static void put(Integer appId, Integer clientType, String userId, String imei, NioSocketChannel channel) {
        UserClientDTO clientDTO = new UserClientDTO();
        clientDTO.setAppId(appId);
        clientDTO.setClientType(clientType);
        clientDTO.setUserId(userId);
        clientDTO.setImei(imei);
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
    public static NioSocketChannel get(Integer appId, Integer clientType, String userId, String imei) {
        UserClientDTO clientDTO = new UserClientDTO();
        clientDTO.setAppId(appId);
        clientDTO.setClientType(clientType);
        clientDTO.setUserId(userId);
        clientDTO.setImei(imei);
        return CHANNEL_MAP.get(clientDTO);
    }


    /**
     * 移除channel,在用户登出时
     *
     * @param appId      应用ID
     * @param clientType 客户端类型
     * @param userId     用户ID
     */
    public static void remove(Integer appId, Integer clientType, String userId, String imei) {
        UserClientDTO clientDTO = new UserClientDTO();
        clientDTO.setAppId(appId);
        clientDTO.setClientType(clientType);
        clientDTO.setUserId(userId);
        clientDTO.setImei(imei);
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
        String imei = (String) channel.attr(AttributeKey.valueOf(Constants.IMEI)).get();

        SessionSocketHolder.remove(appId, clientType, userId, imei);

        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<Object, Object> map = redissonClient.getMap(appId + Constants.RedisKey.USER_SESSION_CONSTANTS + userId);
        String session = map.get(clientType + ":" + imei).toString();

        if (StringUtils.isNotBlank(session)) {
            UserSession userSession = JSONObject.parseObject(session, UserSession.class);
            userSession.setConnectState(ImConnectStatusEnum.OFFLINE_STATUS.getCode());
            map.put(clientType.toString() + ":" + imei, JSONObject.toJSONString(userSession));
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
        String imei = (String) channel.attr(AttributeKey.valueOf(Constants.IMEI)).get();

        SessionSocketHolder.remove(appId, clientType, userId, imei);

        RedissonClient redissonClient = RedisManager.getRedissonClient();
        RMap<Object, Object> map = redissonClient.getMap(appId + Constants.RedisKey.USER_SESSION_CONSTANTS + userId);
        map.remove(clientType + ":" + imei);

        channel.close();
    }


    /**
     * 获取当前netty节点，指定用户的所有的channel
     *
     * @param appId 应用ID
     * @param id 用户Id
     * @return {@link List}<{@link NioSocketChannel}>
     */
    public static List<NioSocketChannel> get(Integer appId , String id) {

        Set<UserClientDTO> channelInfos = CHANNEL_MAP.keySet();
        List<NioSocketChannel> channels = new ArrayList<>();

        channelInfos.forEach(channel ->{
            if(channel.getAppId().equals(appId) && id.equals(channel.getUserId())){
                channels.add(CHANNEL_MAP.get(channel));
            }
        });

        return channels;
    }


    /**
     * 获取当前netty节点，指定用户的所有的channel
     *
     * @param appId 应用ID
     * @param toId 用户Id
     * @param clientType 客户端类型
     * @param imei 客户端设备唯一识别号
     *
     * @return {@link NioSocketChannel}
     */
    public static NioSocketChannel get(Integer appId, String toId, int clientType, String imei) {
        UserClientDTO clientDTO = new UserClientDTO();
        clientDTO.setAppId(appId);
        clientDTO.setClientType(clientType);
        clientDTO.setUserId(toId);
        clientDTO.setImei(imei);
        return CHANNEL_MAP.get(clientDTO);
    }
}
