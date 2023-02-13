package cn.ctrlcv.im.serve.friendship.service;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.friendship.model.request.*;
import cn.ctrlcv.im.serve.friendship.model.response.ImportFriendShipResp;

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
     * @param fromId 发起人
     * @param dto {@link FriendDTO}
     * @param appId 应用ID
     * @return 无
     */
    ResponseVO<?> doAddFriend(String fromId, FriendDTO dto, Integer appId);


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

}
