package cn.ctrlcv.im.serve.user.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class Name: GetUserSequenceReq
 * Class Description: 获取用户的seq
 *
 * @author liujm
 * @date 2023-05-25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class GetUserSequenceReq extends RequestBase {

    private String userId;
}
