package cn.ctrlcv.im.codec.pack.friendship;

import lombok.Data;

import java.util.List;

/**
 * Class Name: AddFriendGroupMemberPack
 * Class Description: 用户添加好友分组成员通知报文
 *
 * @author liujm
 * @date 2023-03-19
 */
@Data
public class AddFriendGroupMemberPack {

    public String fromId;

    private String groupName;

    private List<String> toIds;

}
