package cn.ctrlcv.im.serve.group.controller;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.group.model.request.ImportGroupMemberReq;
import cn.ctrlcv.im.serve.group.service.IGroupMemberService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Class Name: GroupMemberController
 * Class Description: 群组成员功能控制器
 *
 * @author liujm
 * @date 2023-03-02
 */
@RestController
@RequestMapping("v1/groupMember")
public class GroupMemberController {

    private final IGroupMemberService groupMemberService;

    public GroupMemberController(IGroupMemberService groupMemberService) {
        this.groupMemberService = groupMemberService;
    }

    /**
     * 导入群成员
     *
     * @param req {@link ImportGroupMemberReq}
     * @param appId 应用ID
     * @param identifier 操作人
     * @return
     */
    @PostMapping("/importGroupMember")
    public ResponseVO importGroupMember(@RequestBody @Validated ImportGroupMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupMemberService.importGroupMember(req);
    }

}
