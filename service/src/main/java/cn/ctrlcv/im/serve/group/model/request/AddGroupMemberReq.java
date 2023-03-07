package cn.ctrlcv.im.serve.group.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import cn.ctrlcv.im.serve.group.model.dto.GroupMemberDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import java.util.List;

/**
 * Class Name: AddGroupMemberReq
 * Class Description: 邀请入群接口的请求参数
 *
 * @author liujm
 * @date 2023-03-07
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class AddGroupMemberReq extends RequestBase {

    @NotBlank(message = "群id不能为空")
    private String groupId;

    @NotEmpty(message = "群成员不能为空")
    private List<GroupMemberDTO> members;


}
