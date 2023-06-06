package cn.ctrlcv.im.serve.user.service;

import cn.ctrlcv.im.serve.user.model.UserStatusChangeNotifyContent;

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
     * @param content
     */
    void processUserOnlineStatusNotify(UserStatusChangeNotifyContent content);

}
