package cn.ctrlcv.im.codec.pack.friendship;

import lombok.Data;

/**
 * Class Name: AddFriendGroupPack
 * Class Description: 添加好友分通知报文
 *
 * @author liujm
 * @date 2023-03-19
 */
@Data
public class AddFriendGroupPack {

    public String fromId;

    private String groupName;

    private Long sequence;
}
