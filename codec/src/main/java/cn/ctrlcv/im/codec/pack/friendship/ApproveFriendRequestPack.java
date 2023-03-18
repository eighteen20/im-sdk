package cn.ctrlcv.im.codec.pack.friendship;

import lombok.Data;

/**
 * Class Name: ApproveFriendRequestPack
 * Class Description: 审批好友申请通知报文
 *
 * @author liujm
 * @date 2023-03-19
 */
@Data
public class ApproveFriendRequestPack {

        private Long id;

        /**
         * 1同意 2拒绝
         */
        private Integer status;

}
