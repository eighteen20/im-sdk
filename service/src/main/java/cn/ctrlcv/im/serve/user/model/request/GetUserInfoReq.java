package cn.ctrlcv.im.serve.user.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * Class Name: GetUserInfoReq
 * Class Description: 获取用户信息的请求参数
 *
 * @author liujm
 * @date 2023-02-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetUserInfoReq extends RequestBase {

    private List<String> userIds;

}
