package cn.ctrlcv.im.codec.pack.friendship;

import lombok.Data;

/**
 * Class Name: DeleteFriendBlackPack
 * Class Description: 删除好友黑名单通知报文
 *
 * @author liujm
 * @date 2023-03-19
 */
@Data
public class DeleteFriendBlackPack {

    private String fromId;

    private String toId;

    private Long sequence;

}
