package cn.ctrlcv.im.serve.friendship.controller;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.friendship.dao.ImFriendshipEntity;
import cn.ctrlcv.im.serve.friendship.model.request.*;
import cn.ctrlcv.im.serve.friendship.model.response.ImportFriendShipResp;
import cn.ctrlcv.im.serve.friendship.service.IFriendshipService;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Class Name: ImFriendshipController
 * Class Description: 好友关系控制层
 *
 * @author liujm
 * @date 2023-02-08
 */
@RestController
@RequestMapping("v1/friendship")
public class ImFriendshipController {

    private final IFriendshipService friendshipService;

    public ImFriendshipController(IFriendshipService friendshipService) {
        this.friendshipService = friendshipService;
    }


    /**
     * 导入好友关系链数据
     *
     * @param req {@link ImportFriendshipReq}
     * @param appId 应用ID
     * @return {@link ImportFriendShipResp}
     */
    @PostMapping("/importFriendship")
    public ResponseVO<ImportFriendShipResp> importFriendShip(@RequestBody @Validated ImportFriendshipReq req, Integer appId) {
        req.setAppId(appId);
        return this.friendshipService.importFriendship(req);
    }


    /**
     * 添加好友
     *
     * @param req {@link AddFriendshipReq}
     * @param appId 应用ID
     * @return {@link ResponseVO}
     */
    @PostMapping("/addFriend")
    public ResponseVO<?> addFriend(@RequestBody @Validated AddFriendshipReq req, Integer appId){
        req.setAppId(appId);
        return this.friendshipService.addFriend(req);
    }

    /**
     * 修改好友
     *
     * @param req {@link AddFriendshipReq}
     * @param appId 应用ID
     * @return {@link ResponseVO}
     */
    @PutMapping("/updateFriend")
    public ResponseVO<?> updateFriend(@RequestBody @Validated UpdateFriendshipReq req, Integer appId){
        req.setAppId(appId);
        return this.friendshipService.updateFriend(req);
    }


    /**
     * 删除好友
     *
     * @param req {@link AddFriendshipReq}
     * @param appId 应用ID
     * @return {@link ResponseVO}
     */
    @DeleteMapping("/deleteFriend")
    public ResponseVO<?> deleteFriend(@RequestBody @Validated DeleteFriendshipReq req, Integer appId){
        req.setAppId(appId);
        return this.friendshipService.deleteFriend(req);
    }

    /**
     * 删除所有好友
     *
     * @param req {@link AddFriendshipReq}
     * @param appId 应用ID
     * @return {@link ResponseVO}
     */
    @DeleteMapping("/deleteAllFriend")
    public ResponseVO<?> deleteAllFriend(@RequestBody @Validated DeleteAllFriendshipReq req, Integer appId){
        req.setAppId(appId);
        return this.friendshipService.deleteAllFriend(req);
    }

    /**
     *拉取好友信息
     *
     * @param req {@link GetFriendShipReq}
     * @param appId 应用ID
     * @return {@link ImFriendshipEntity}
     */
    @GetMapping("/getFriend")
    public ResponseVO<ImFriendshipEntity> getFriend(@RequestBody @Validated GetFriendShipReq req, Integer appId){
        req.setAppId(appId);
        return this.friendshipService.getFriendShip(req);
    }


    /**
     *拉取所有好友信息
     *
     * @param req {@link GetAllFriendShipReq}
     * @param appId 应用ID
     * @return {@link List}<{@link ImFriendshipEntity}>
     */
    @GetMapping("/getAllFriend")
    public ResponseVO<List<ImFriendshipEntity>> getAllFriend(@RequestBody @Validated GetAllFriendShipReq req, Integer appId){
        req.setAppId(appId);
        return this.friendshipService.getAllFriendShip(req);
    }

}
