package cn.ctrlcv.im.serve.friendship.controller;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.friendship.model.request.ApproveFriendRequestReq;
import cn.ctrlcv.im.serve.friendship.model.request.GetFriendShipRequestReq;
import cn.ctrlcv.im.serve.friendship.model.request.ReadFriendShipRequestReq;
import cn.ctrlcv.im.serve.friendship.service.IFriendshipRequestService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * Class Name: ImFriendshipRequestController
 * Class Description: 好友请求控制器
 *
 * @author liujm
 * @date 2023-02-23
 */
@RestController
@RequestMapping("v1/friendshipRequest")
public class ImFriendshipRequestController {

    private final IFriendshipRequestService friendshipRequestService;

    public ImFriendshipRequestController(IFriendshipRequestService friendshipRequestService) {
        this.friendshipRequestService = friendshipRequestService;
    }

    /**
     * 审批好友申请
     *
     * @param req {@link ApproveFriendRequestReq}
     * @param appId 应用ID
     * @return 无
     */
    @PostMapping("/approve")
    public ResponseVO<?> approveFriendshipRequest(@RequestBody @Validated ApproveFriendRequestReq req, Integer appId, String identifier) {
        req.setAppId(appId);
        req.setOperator(identifier);
        return this.friendshipRequestService.approveFriendshipRequest(req);
    }

    /**
     * 获取好友申请记录
     *
     * @param req {@link GetFriendShipRequestReq}
     * @param appId 应用ID
     * @return
     */
    @GetMapping("/getFriendRequest")
    public ResponseVO getFriendRequest(@RequestBody @Validated GetFriendShipRequestReq req, Integer appId){
        req.setAppId(appId);
        return this.friendshipRequestService.getFriendRequest(req.getFromId(),req.getAppId());
    }

    /**
     * 查看好友申请记录
     *
     * @param req {@link ReadFriendShipRequestReq}
     * @param appId 应用Id
     * @return
     */
    @PostMapping("/readFriendShipRequestReq")
    public ResponseVO readFriendShipRequestReq(@RequestBody @Validated ReadFriendShipRequestReq req, Integer appId){
        req.setAppId(appId);
        return this.friendshipRequestService.readFriendShipRequestReq(req);
    }

}
