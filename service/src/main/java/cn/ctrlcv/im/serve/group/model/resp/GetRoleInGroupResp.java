package cn.ctrlcv.im.serve.group.model.resp;

import lombok.Data;

/**
 * Class Name: GetRoleInGroupResp
 * Class Description: 群组成员权限信息
 *
 * @author liujm
 * @date 2023-03-04
 */
@Data
public class GetRoleInGroupResp {

    private Long groupMemberId;

    private String memberId;

    private Integer role;

    private Long speakDate;
}
