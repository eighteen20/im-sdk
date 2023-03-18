package cn.ctrlcv.im.codec.pack.friendship;

import lombok.Data;

/**
 * Class Name: DeleteFriendGroupPack
 * Class Description: 删除好友分组通知报文
 *
 * @author liujm
 * @date 2023-03-19
 */
@Data
public class DeleteFriendGroupPack {

        public String fromId;

        private String groupName;
}
