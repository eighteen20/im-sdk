package cn.ctrlcv.im.serve.friendship.model.callback;

import lombok.Data;

/**
 * Class Name: DeleteFriendAfterCallbackDTO
 * Class Description: 删除好友后的回调参数
 *
 * @author liujm
 * @date 2023-03-17
 */
@Data
public class DeleteFriendAfterCallbackDTO {

    /**
     * 用户ID
     */
    private String fromId;

    /**
     * 用户ID好友Id
     */
    private String toId;
}
