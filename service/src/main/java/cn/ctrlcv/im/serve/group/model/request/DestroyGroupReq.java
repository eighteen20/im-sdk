package cn.ctrlcv.im.serve.group.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotNull;

/**
 * Class Name: DestroyGroupReq
 * Class Description: 解散群组接口的请求参数
 *
 * @author liujm
 * @date 2023-03-07
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class DestroyGroupReq extends RequestBase {

    @NotNull(message = "群id不能为空")
    private String groupId;

}
