package cn.ctrlcv.im.serve.friendship.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class Name: ApproveFriendRequestReq
 * Class Description: 审批好友请求
 *
 * @author liujm
 * @date 2023-02-23
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ApproveFriendRequestReq extends RequestBase {

    private Long id;

    /**
     * 1同意； 2拒绝
     */
    private Integer status;

}
