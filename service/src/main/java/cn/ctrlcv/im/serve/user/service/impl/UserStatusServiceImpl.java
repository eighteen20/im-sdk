package cn.ctrlcv.im.serve.user.service.impl;

import cn.ctrlcv.im.codec.pack.user.UserCustomStatusChangeNotifyPack;
import cn.ctrlcv.im.codec.pack.user.UserStatusChangeNotifyPack;
import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.command.UserEventCommand;
import cn.ctrlcv.im.common.model.ClientInfo;
import cn.ctrlcv.im.common.model.UserSession;
import cn.ctrlcv.im.serve.friendship.service.IFriendshipService;
import cn.ctrlcv.im.serve.user.model.UserStatusChangeNotifyContent;
import cn.ctrlcv.im.serve.user.model.request.SubscribeUserOnlineStatusReq;
import cn.ctrlcv.im.serve.user.model.request.UserSetCustomStatusReq;
import cn.ctrlcv.im.serve.user.service.IUserStatusService;
import cn.ctrlcv.im.serve.utils.MessageProducer;
import cn.ctrlcv.im.serve.utils.UserSessionUtils;
import com.alibaba.fastjson.JSONObject;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;
import java.util.Set;

/**
 * Class Name: UserStatusServiceImpl
 * Class Description: 用户状态服务实现
 *
 * @author liujm
 * @date 2023-06-05
 */
@Service
public class UserStatusServiceImpl implements IUserStatusService {

    @Resource
    private UserSessionUtils userSessionUtils;
    @Resource
    private MessageProducer messageProducer;
    @Resource
    private IFriendshipService imFriendService;
    @Resource
    private StringRedisTemplate stringRedisTemplate;


    @Override
    public void processUserOnlineStatusNotify(UserStatusChangeNotifyContent content) {
        List<UserSession> userSession = userSessionUtils.getUserSession(content.getAppId(), content.getUserId());
        UserStatusChangeNotifyPack userStatusChangeNotifyPack = new UserStatusChangeNotifyPack();
        BeanUtils.copyProperties(content, userStatusChangeNotifyPack);
        userStatusChangeNotifyPack.setClient(userSession);

        syncSender(userStatusChangeNotifyPack, content.getUserId(), content);
        dispatcher(userStatusChangeNotifyPack, content.getUserId(), content.getAppId());
    }


    /**
     * 同步发送给自己的其他端
     * <br>
     * TODO 可以将Command改为入参，就能实现自定义在线状态了
     *
     * @param pack       通知包
     * @param userId     用户ID
     * @param clientInfo 客户端信息
     */
    private void syncSender(Object pack, String userId, ClientInfo clientInfo) {
        messageProducer.sendToUserExceptClient(userId, UserEventCommand.USER_ONLINE_STATUS_CHANGE_NOTIFY_SYNC,
                pack, clientInfo);
    }

    /**
     * 分发给好友和订阅者
     * <br>
     * TODO 可以将Command改为入参，就能实现自定义在线状态了
     *
     * @param pack   通知包
     * @param userId 用户ID
     * @param appId  应用ID
     */
    private void dispatcher(Object pack, String userId, Integer appId) {
        List<String> allFriendId = imFriendService.getAllFriendId(userId, appId);
        for (String fid : allFriendId) {
            messageProducer.sendToUser(fid, UserEventCommand.USER_ONLINE_STATUS_CHANGE_NOTIFY, pack, appId);
        }

        // 发送给临时订阅的人
        String userKey = appId + Constants.RedisKey.SUBSCRIBE + userId;
        Set<Object> keys = stringRedisTemplate.opsForHash().keys(userKey);
        for (Object key : keys) {
            String filed = (String) key;
            long expire = Long.parseLong((String) stringRedisTemplate.opsForHash().get(userKey, filed));
            if (expire > 0 && expire > System.currentTimeMillis()) {
                messageProducer.sendToUser(filed, UserEventCommand.USER_ONLINE_STATUS_CHANGE_NOTIFY, pack, appId);
            } else {
                stringRedisTemplate.opsForHash().delete(userKey, filed);
            }
        }
    }


    /**
     * 存储结构：
     * <p>
     * hash结构存储
     * <br>
     * key: 用户ID。value: [订阅者ID:过期时间]
     * </p>
     */
    @Override
    public ResponseVO<?> subscribeUserOnlineStatus(SubscribeUserOnlineStatusReq req) {
        long subExpireTime = 0L;
        if (req != null && req.getSubTime() > 0) {
            subExpireTime = System.currentTimeMillis() + req.getSubTime();
        }

        for (String beSubUserId : req.getSubUserId()) {
            String userKey = req.getAppId() + Constants.RedisKey.SUBSCRIBE + beSubUserId;
            stringRedisTemplate.opsForHash().put(userKey, req.getOperator(), Long.toString(subExpireTime));
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> setCustomStatus(UserSetCustomStatusReq req) {
        UserCustomStatusChangeNotifyPack userCustomStatusChangeNotifyPack = new UserCustomStatusChangeNotifyPack();
        userCustomStatusChangeNotifyPack.setCustomStatus(req.getCustomStatus());
        userCustomStatusChangeNotifyPack.setCustomText(req.getCustomText());
        userCustomStatusChangeNotifyPack.setUserId(req.getUserId());
        stringRedisTemplate.opsForValue().set(req.getAppId() + Constants.RedisKey.USER_CUSTOMER_STATUS + req.getUserId()
                , JSONObject.toJSONString(userCustomStatusChangeNotifyPack));

        syncSender(userCustomStatusChangeNotifyPack, req.getUserId(),
                new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));
        dispatcher(userCustomStatusChangeNotifyPack, req.getUserId(), req.getAppId());

        return ResponseVO.successResponse();
    }
}
