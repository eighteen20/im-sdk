package cn.ctrlcv.im.codec.pack.friendship;

import lombok.Data;

/**
 * Class Name: ReadAllFriendRequestPack
 * Class Description: 已读好友申请通知报文
 *
 * @author liujm
 * @date 2023-03-19
 */
@Data
public class ReadAllFriendRequestPack {

    private String fromId;

    private Long sequence;

}
