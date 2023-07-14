package cn.ctrlcv.im.serve.friendship.controller;

import cn.ctrlcv.im.common.model.ResponseVO;
import cn.ctrlcv.im.serve.friendship.model.request.AddFriendShipGroupMemberReq;
import cn.ctrlcv.im.serve.friendship.model.request.AddFriendShipGroupReq;
import cn.ctrlcv.im.serve.friendship.model.request.DeleteFriendShipGroupMemberReq;
import cn.ctrlcv.im.serve.friendship.model.request.DeleteFriendShipGroupReq;
import cn.ctrlcv.im.serve.friendship.service.IFriendShipGroupMemberService;
import cn.ctrlcv.im.serve.friendship.service.IFriendshipGroupService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Class Name: ImFriendshipGroupController
 * Class Description: 好友分组功能控制层
 *
 * @author liujm
 * @date 2023-02-28
 */
@RestController
@RequestMapping("v1/friendship/group")
public class ImFriendshipGroupController {

    private final IFriendshipGroupService groupService;

    private final IFriendShipGroupMemberService groupMemberService;

    public ImFriendshipGroupController(IFriendshipGroupService groupService, IFriendShipGroupMemberService groupMemberService) {
        this.groupService = groupService;
        this.groupMemberService = groupMemberService;
    }


    /**
     * 新增分组
     *
     * @param req {@link AddFriendShipGroupReq}
     * @return 无
     */
    @PostMapping("/add")
    public ResponseVO<?> add(@RequestBody @Validated AddFriendShipGroupReq req, Integer appId)  {
        req.setAppId(appId);
        return this.groupService.addGroup(req);
    }

    /**
     * 删除分组
     *
     * @param req {@link DeleteFriendShipGroupReq}
     * @return 无
     */
    @DeleteMapping("/del")
    public ResponseVO<?> del(@RequestBody @Validated DeleteFriendShipGroupReq req, Integer appId)  {
        req.setAppId(appId);
        return this.groupService.deleteGroup(req);
    }

    /**
     * 把好友添加进分组
     *
     * @param req {@link AddFriendShipGroupMemberReq}
     * @return 无
     */
    @PostMapping("/member/add")
    public ResponseVO<?> memberAdd(@RequestBody @Validated AddFriendShipGroupMemberReq req, Integer appId)  {
        req.setAppId(appId);
        return this.groupMemberService.addGroupMember(req);
    }


    /**
     * 把好友从分组中移除
     *
     * @param req {@link DeleteFriendShipGroupMemberReq}
     * @return 无
     */
    @DeleteMapping("/member/del")
    public ResponseVO<?> memberDel(@RequestBody @Validated DeleteFriendShipGroupMemberReq req, Integer appId)  {
        req.setAppId(appId);
        return this.groupMemberService.delGroupMember(req);
    }


}
