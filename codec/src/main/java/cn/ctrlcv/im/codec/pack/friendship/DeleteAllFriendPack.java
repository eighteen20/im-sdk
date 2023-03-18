package cn.ctrlcv.im.codec.pack.friendship;

import lombok.Data;

/**
 * Class Name: DeleteAllFriendPack
 * Class Description: 删除所有好友通知报文
 *
 * @author liujm
 * @date 2023-03-19
 */
@Data
public class DeleteAllFriendPack {

    private String fromId;

    private String toId;
}
