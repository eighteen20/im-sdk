package cn.ctrlcv.im.serve.user.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class Name: UserId
 * Class Description: TODO
 *
 * @author liujm
 * @date 2023-02-08
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class UserId extends RequestBase {

    private String userId;

}
