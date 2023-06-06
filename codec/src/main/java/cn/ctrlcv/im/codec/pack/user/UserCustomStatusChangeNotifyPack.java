package cn.ctrlcv.im.codec.pack.user;

import lombok.Data;

/**
 * Class Name: UserCustomStatusChangeNotifyPack
 * Class Description: 用户自定义状态变更通知包
 *
 * @author liujm
 * @date 2023-06-06
 */
@Data
public class UserCustomStatusChangeNotifyPack {

    private String customText;

    private Integer customStatus;

    private String userId;

}
