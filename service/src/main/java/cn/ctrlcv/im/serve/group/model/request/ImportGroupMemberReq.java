package cn.ctrlcv.im.serve.group.model.request;

import cn.ctrlcv.im.common.model.RequestBase;
import cn.ctrlcv.im.serve.group.model.dto.GroupMemberDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import java.util.List;

/**
 * Class Name: ImportGroupMemberReq
 * Class Description: 导入群成员接口请求参数
 *
 * @author liujm
 * @date 2023-03-02
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class ImportGroupMemberReq extends RequestBase {

    @NotBlank(message = "群id不能为空")
    private String groupId;

    private List<GroupMemberDTO> members;

}
