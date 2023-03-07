package cn.ctrlcv.im.serve.group.controller;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.group.model.request.*;
import cn.ctrlcv.im.serve.group.service.IGroupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Class Name: GroupController
 * Class Description: 群组功能控制器
 *
 * @author liujm
 * @date 2023-03-02
 */
@RestController
@RequestMapping("v1/group")
public class GroupController {

    private final IGroupService groupService;

    public GroupController(IGroupService groupService) {
        this.groupService = groupService;
    }

    /**
     * 导入群组
     *
     * @param req {@link ImportGroupReq}
     * @param appId 应用ID
     * @param identifier 操作人
     * @return 无
     */
    @PostMapping("/importGroup")
    public ResponseVO<?> importGroup(@RequestBody @Validated ImportGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return this.groupService.importGroup(req);
    }


    /**
     * 创建群组
     *
     * @param req {@link CreateGroupReq}
     * @return
     */
    @PostMapping("/createGroup")
    public ResponseVO createGroup(@RequestBody @Validated CreateGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return this.groupService.createGroup(req);
    }

    /**
     * 获取群组信息
     *
     * @param req {@link GetGroupReq}
     * @return
     */
    @GetMapping("/getGroupInfo")
    public ResponseVO getGroupInfo(@RequestBody @Validated GetGroupReq req, Integer appId)  {
        req.setAppId(appId);
        return this.groupService.getGroup(req);
    }

    /**
     * 修改群组信息
     *
     * @param req {@link UpdateGroupReq}
     * @return
     */
    @PutMapping("/update")
    public ResponseVO update(@RequestBody @Validated UpdateGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return this.groupService.updateBaseGroupInfo(req);
    }


    /**
     * 获取用户加入的群组列表
     *
     * @param req {@link GetJoinedGroupReq}
     * @param appId 应用ID
     * @param identifier 操作人
     * @return
     */
    @GetMapping("/getJoinedGroup")
    public ResponseVO getJoinedGroup(@RequestBody @Validated GetJoinedGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.getJoinedGroup(req);
    }


    /**
     * 解散群组
     *
     * @param req {@link DestroyGroupReq}
     * @param appId 应用ID
     * @param identifier 操作人
     * @return
     */
    @PutMapping("/destroyGroup")
    public ResponseVO destroyGroup(@RequestBody @Validated DestroyGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.destroyGroup(req);
    }


    /**
     * 转让群主
     *
     * @param req {@link TransferGroupReq}
     * @param appId 应用ID
     * @param identifier 操作人
     * @return
     */
    @PostMapping("/transferGroup")
    public ResponseVO transferGroup(@RequestBody @Validated TransferGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.transferGroup(req);
    }

    /**
     * 禁言群
     *
     * @param req {@link MuteGroupReq}
     * @param appId
     * @param identifier
     * @return
     */
    @PostMapping("/forbidSendMessage")
    public ResponseVO forbidSendMessage(@RequestBody @Validated MuteGroupReq req, Integer appId, String identifier)  {
        req.setAppId(appId);
        req.setOperator(identifier);
        return groupService.muteGroup(req);
    }


}
