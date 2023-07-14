package cn.ctrlcv.im.codec.pack.friendship;

import lombok.Data;

import java.util.List;

/**
 * Class Name: DeleteFriendGroupMemberPack
 * Class Description: 用户删除好友分组成员通知报文
 *
 * @author liujm
 * @date 2023-03-19
 */
@Data
public class DeleteFriendGroupMemberPack {

        public String fromId;

        private String groupName;

        private List<String> toIds;
}
