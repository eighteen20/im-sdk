package cn.ctrlcv.im.serve.user.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Class Name: PullUserOnlineStatusReq
 * Class Description: 拉取用户在线状态请求参数
 *
 * @author liujm
 * @date 2023-06-06
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class PullUserOnlineStatusReq extends RequestBase {

    private List<String> userList;
}
