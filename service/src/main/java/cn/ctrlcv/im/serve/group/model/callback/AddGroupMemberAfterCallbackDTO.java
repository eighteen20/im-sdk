package cn.ctrlcv.im.serve.group.model.callback;

import cn.ctrlcv.im.serve.group.model.resp.AddMemberResp;
import lombok.Data;

import java.util.List;

/**
 * Class Name: AddGroupMemberAfterCallbackDTO
 * Class Description: 添加群成员后的回调
 *
 * @author liujm
 * @date 2023-03-17
 */
@Data
public class AddGroupMemberAfterCallbackDTO {

    private String groupId;

    private Integer groupType;

    private String operator;

    private List<AddMemberResp> memberList;
}
