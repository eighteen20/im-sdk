package cn.ctrlcv.im.serve.friendship.model.callback;

import cn.ctrlcv.im.serve.friendship.model.request.FriendDTO;
import lombok.Data;

/**
 * Class Name: AddFriendAfterCallbackDTO
 * Class Description: 添加好友后的回调参数
 *
 * @author liujm
 * @date 2023-03-17
 */
@Data
public class AddFriendAfterCallbackDTO {

    /**
     * 用户ID
     */
    private String fromId;

    /**
     * 好友信息
     */
    private FriendDTO toItem;
}
