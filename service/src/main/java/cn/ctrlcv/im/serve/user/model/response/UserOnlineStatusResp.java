package cn.ctrlcv.im.serve.user.model.response;

import cn.ctrlcv.im.common.model.UserSession;
import lombok.Data;

import java.util.List;

/**
 * Class Name: UserOnlineStatusResp
 * Class Description: 用户在线状态响应参数
 *
 * @author liujm
 * @date 2023-06-06
 */
@Data
public class UserOnlineStatusResp {

    private List<UserSession> session;

    private String customText;

    private Integer customStatus;
}
