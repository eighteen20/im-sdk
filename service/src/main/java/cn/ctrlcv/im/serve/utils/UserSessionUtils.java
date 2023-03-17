package cn.ctrlcv.im.serve.utils;

import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.ImConnectStatusEnum;
import cn.ctrlcv.im.common.model.UserSession;
import com.alibaba.fastjson.JSONObject;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Class Name: UserSessionUtils
 * Class Description: 用户session  Utils
 *
 * @author liujm
 * @date 2023-03-17
 */
@Component
public class UserSessionUtils {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 获取用户所有的session
     *
     * @param appId  应用ID
     * @param userId 用户ID
     * @return {@link List}<{@link UserSession}>
     */
    public List<UserSession> getUserSession(Integer appId, String userId) {
        String userSessionKey = appId + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId;
        Map<Object, Object> entries = this.stringRedisTemplate.opsForHash().entries(userSessionKey);
        List<UserSession> list = new ArrayList<>();
        for (Object value : entries.values()) {
            String str = (String) value;
            UserSession userSession = JSONObject.parseObject(str, UserSession.class);
            if (userSession.getConnectState() == ImConnectStatusEnum.ONLINE_STATUS.getCode()) {
                list.add(userSession);
            }
        }

        return list;
    }

    /**
     * 获取用户 session
     *
     * @param appId      应用ID
     * @param userId     用户ID
     * @param clientType 当前客户端类型
     * @param imei       客户端IMEI号
     * @return {@link UserSession}
     */
    public UserSession getUserSession(Integer appId, String userId, Integer clientType, String imei) {
        String userSessionKey = appId + Constants.RedisConstants.USER_SESSION_CONSTANTS + userId;
        String hashKey = clientType + ":" + imei;
        Object obj = stringRedisTemplate.opsForHash().get(userSessionKey, hashKey);
        return JSONObject.parseObject(obj.toString(), UserSession.class);
    }

}
