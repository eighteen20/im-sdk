package cn.ctrlcv.im.codec.pack.friendship;

import lombok.Data;

/**
 * Class Name: AddFriendBlackPack
 * Class Description: 添加黑名单通知报文
 *
 * @author liujm
 * @date 2023-03-19
 */
@Data
public class AddFriendBlackPack {

    private String fromId;

    private String toId;

    private Long sequence;
}
