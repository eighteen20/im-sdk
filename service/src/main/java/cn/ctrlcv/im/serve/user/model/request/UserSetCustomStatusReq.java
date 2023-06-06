package cn.ctrlcv.im.serve.user.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class Name: UserSetCustomStatusReq
 * Class Description: 设置用户自定义状态请求
 *
 * @author liujm
 * @date 2023-06-06
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserSetCustomStatusReq extends RequestBase {

    /**
     * 用户ID
     */
    private String userId;

    /**
     * 自定义状态文本
     */
    private String customText;

    /**
     * 自定义状态
     */
    private Integer customStatus;

}
