package cn.ctrlcv.im.serve.group.controller;

import cn.ctrlcv.im.common.model.ResponseVO;
import cn.ctrlcv.im.serve.group.model.request.*;
import cn.ctrlcv.im.serve.group.service.IGroupMemberService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

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
        return this.groupMemberService.importGroupMember(req);
    }


    /**
     * 添加群成员，拉人入群
     *
     * @param req {@link AddGroupMemberReq}
     * @param appId 应用ID
     * @param identifier 操作人
     * @return
     */
    @PostMapping("/add")
    public ResponseVO addMember(@RequestBody @Validated AddGroupMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return this.groupMemberService.addMember(req);
    }

    /**
     * 将群成员移出群组
     *
     * @param req {@link RemoveGroupMemberReq}
     * @param appId 应用ID
     * @param identifier 操作人
     * @return
     */
    @DeleteMapping("/remove")
    public ResponseVO removeMember(@RequestBody @Validated RemoveGroupMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return this.groupMemberService.removeMember(req);
    }


    /**
     * 退出群组
     *
     * @param req {@link ExitGroupReq}
     * @param appId 应用ID
     * @param identifier 操作人
     * @return
     */
    @DeleteMapping("/exit")
    public ResponseVO exitGroup(@RequestBody @Validated ExitGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return this.groupMemberService.exitGroup(req);
    }


    /**
     * 修改群成员信息
     *
     * @param req {@link UpdateGroupMemberReq}
     * @param appId 应用ID
     * @param identifier 操作人
     * @return
     */
    @PutMapping("/update")
    public ResponseVO updateGroupMember(@RequestBody @Validated UpdateGroupMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupMemberService.updateGroupMember(req);
    }


    /**
     * 获取群成员信息
     *
     * @param req {@link GetGroupMemberReq}
     * @param appId 应用ID
     * @return
     */
    @GetMapping("/member")
    public ResponseVO getMember(@RequestBody @Validated GetGroupMemberReq req, Integer appId)  {
        req.setAppId(appId);
        return groupMemberService.getGroupMember(req.getGroupId(), appId);
    }


    /**
     * 禁言群成员
     *
     * @param req {@link SpeakMemberReq}
     * @param appId 应用ID
     * @param identifier 操作人
     * @return
     */
    @PostMapping("/speak")
    public ResponseVO speak(@RequestBody @Validated SpeakMemberReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupMemberService.speak(req);
    }

}
