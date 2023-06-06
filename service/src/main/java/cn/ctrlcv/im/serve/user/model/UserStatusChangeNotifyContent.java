package cn.ctrlcv.im.serve.user.model;

import cn.ctrlcv.im.common.model.ClientInfo;
import lombok.Data;

/**
 * Class Name: UserStatusChangeNotifyContent
 * Class Description: 用户状态变更通知内容
 *
 * @author liujm
 * @date 2023-06-05
 */
@Data
public class UserStatusChangeNotifyContent extends ClientInfo {

    private String userId;

    /**
     * 服务端状态 1上线 2离线
     */
    private Integer status;

}
