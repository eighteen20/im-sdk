package cn.ctrlcv.im.serve.group.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class Name: GetGroupReq
 * Class Description: 获取群组信息接口的请求参数
 *
 * @author liujm
 * @date 2023-03-04
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetGroupReq extends RequestBase {

    private String groupId;

}
