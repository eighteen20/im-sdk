package cn.ctrlcv.im.codec.pack.friendship;

import lombok.Data;

/**
 * Class Name: DeleteFriendPack
 * Class Description: 删除好友通知报文
 *
 * @author liujm
 * @date 2023-03-19
 */
@Data
public class DeleteFriendPack {

    private String fromId;

    private String toId;

    private Long sequence;

}
