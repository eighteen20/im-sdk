package cn.ctrlcv.im.codec.pack.group;

import lombok.Data;

/**
 * Class Name: UpdateGroupMemberPack
 * Class Description: 修改群成员通知报文
 *
 * @author liujm
 * @date 2023-03-21
 */
@Data
public class UpdateGroupMemberPack {

    private String groupId;

    private String memberId;

    private String alias;

    private String extra;

}
