package cn.ctrlcv.im.codec.pack.user;

import cn.ctrlcv.im.common.model.UserSession;
import lombok.Data;

import java.util.List;

/**
 * Class Name: UserStatusChangeNotifyPack
 * Class Description: 用户状态变更通知包
 *
 * @author liujm
 * @date 2023-06-05
 */
@Data
public class UserStatusChangeNotifyPack {

    private Integer appId;

    private String userId;

    /**
     * 0:离线 1:在线
     */
    private Integer status;

    private List<UserSession> client;

}
