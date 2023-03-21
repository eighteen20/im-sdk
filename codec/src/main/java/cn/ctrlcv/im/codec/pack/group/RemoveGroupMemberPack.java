package cn.ctrlcv.im.codec.pack.group;

import lombok.Data;

/**
 * Class Name: RemoveGroupMemberPack
 * Class Description: 踢人出群通知报文
 *
 * @author liujm
 * @date 2023-03-21
 */
@Data
public class RemoveGroupMemberPack {

    private String groupId;

    private String member;
}
