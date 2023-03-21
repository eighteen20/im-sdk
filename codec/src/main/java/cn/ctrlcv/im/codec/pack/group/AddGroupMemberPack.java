package cn.ctrlcv.im.codec.pack.group;

import lombok.Data;

import java.util.List;

/**
 * Class Name: AddGroupMemberPack
 * Class Description: 群内添加群成员通知报文
 *
 * @author liujm
 * @date 2023-03-21
 */
@Data
public class AddGroupMemberPack {

    private String groupId;

    private List<String> members;

}
