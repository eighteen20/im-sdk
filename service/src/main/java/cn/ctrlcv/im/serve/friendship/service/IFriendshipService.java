package cn.ctrlcv.im.serve.friendship.service;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.model.RequestBase;
import cn.ctrlcv.im.serve.friendship.dao.ImFriendshipEntity;
import cn.ctrlcv.im.serve.friendship.model.request.*;
import cn.ctrlcv.im.serve.friendship.model.response.CheckFriendShipResp;
import cn.ctrlcv.im.serve.friendship.model.response.ImportFriendShipResp;

import java.util.List;

/**
 * Class Name: IFriendshipService
 * Class Description: 好友关系业务接口
 *
 * @author liujm
 * @date 2023-02-08
 */
public interface IFriendshipService {

    /**
     * 导入用户好友关系链
     *
     * @param req {@link ImportFriendshipReq}
     * @return {@link ImportFriendShipResp}
     */
    ResponseVO<ImportFriendShipResp> importFriendship(ImportFriendshipReq req);

    /**
     * 添加好友（）
     *
     * @param req {@link AddFriendshipReq}
     * @return 无
     */
    ResponseVO<?> addFriend(AddFriendshipReq req);


    /**
     * 添加好友（无需验证）
     *
     * @param requestBase {@link RequestBase}
     * @param fromId 发起人
     * @param dto {@link FriendDTO}
     * @param appId 应用ID
     * @return 无
     */
    ResponseVO<?> doAddFriend(RequestBase requestBase, String fromId, FriendDTO dto, Integer appId);


    /**
     * 添加好友（无需验证）
     *
     * @param req {@link UpdateFriendshipReq}
     * @return 无
     */
    ResponseVO<?> updateFriend(UpdateFriendshipReq req);

    /**
     * 删除好友
     *
     * @param req {@link DeleteFriendshipReq}
     * @return 无
     */
    ResponseVO<?> deleteFriend(DeleteFriendshipReq req);

    /**
     * 删除所有好友
     *
     * @param req {@link DeleteFriendshipReq}
     * @return 无
     */
    ResponseVO<String> deleteAllFriend(DeleteAllFriendshipReq req);

    /**
     * 拉取所有好友信息
     *
     * @param req {@link GetAllFriendShipReq}
     * @return {@link List}<{@link ImFriendshipEntity}>
     */
    ResponseVO<List<ImFriendshipEntity>> getAllFriendShip(GetAllFriendShipReq req);

    /**
     * 拉取好友信息
     *
     * @param req {@link GetFriendShipReq}
     * @return {@link ImFriendshipEntity}
     */
    ResponseVO<ImFriendshipEntity> getFriendShip(GetFriendShipReq req);

    /**
     * 校验好友关系
     *
     * @param req {@link CheckFriendShipReq}
     * @return {@link CheckFriendShipResp}
     */
    ResponseVO<CheckFriendShipResp> checkFriendship(CheckFriendShipReq req);

    /**
     * 添加黑名单
     *
     * @param req {@link AddBlackReq}
     * @return 无
     */
    ResponseVO<?> addBlack(AddBlackReq req);

    /**
     * 将好友移除黑名单
     *
     * @param req {@link DeleteBlackReq}
     * @return 无
     */
    ResponseVO<?> deleteBlack(DeleteBlackReq req);

     /**
     * 检验好友黑名单关系
     *
     * @param req {@link CheckFriendShipReq}
     * @return {@link CheckFriendShipResp}
     */
    ResponseVO<CheckFriendShipResp> checkBlack(CheckFriendShipReq req);
}
