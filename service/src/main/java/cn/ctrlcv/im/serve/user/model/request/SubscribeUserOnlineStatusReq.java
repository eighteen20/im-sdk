package cn.ctrlcv.im.serve.user.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Class Name: SubscribeUserOnlineStatusReq
 * Class Description: 订阅用户上线状态变更请求
 *
 * @author liujm
 * @date 2023-06-06
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SubscribeUserOnlineStatusReq extends RequestBase {

    /**
     * 订阅用户ID
     */
    private List<String> subUserId;

    /**
     * 订阅时间
     */
    private Long subTime;

}
