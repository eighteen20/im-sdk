package cn.ctrlcv.im.serve.user.service;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.user.model.UserStatusChangeNotifyContent;
import cn.ctrlcv.im.serve.user.model.request.SubscribeUserOnlineStatusReq;
import cn.ctrlcv.im.serve.user.model.request.UserSetCustomStatusReq;

/**
 * interface Name: IUserStatusService
 * interface Description: 用户状态服务接口
 *
 * @author liujm
 * @date 2023-06-05
 */
public interface IUserStatusService {

    /**
     * 用户上线状态变更通知
     *
     * @param content {@link UserStatusChangeNotifyContent}
     */
    void processUserOnlineStatusNotify(UserStatusChangeNotifyContent content);


    /**
     * 订阅用户上线状态
     *
     * @param req {@link SubscribeUserOnlineStatusReq}
     * @return 无
     */
    ResponseVO<?> subscribeUserOnlineStatus(SubscribeUserOnlineStatusReq req);

    /**
     * 设置用户自定义状态
     *
     * @param req {@link UserSetCustomStatusReq}
     * @return 无
     */
    ResponseVO<?> setCustomStatus(UserSetCustomStatusReq req);
}
