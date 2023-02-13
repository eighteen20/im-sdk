package cn.ctrlcv.im.serve.friendship.service;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.friendship.model.request.AddFriendReq;
import cn.ctrlcv.im.serve.friendship.model.request.FriendDTO;
import cn.ctrlcv.im.serve.friendship.model.request.ImportFriendshipReq;
import cn.ctrlcv.im.serve.friendship.model.request.UpdateFriendReq;
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
     * @param req {@link AddFriendReq}
     * @return 无
     */
    ResponseVO<?> addFriend(AddFriendReq req);


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
     * @param req {@link UpdateFriendReq}
     * @return 无
     */
    ResponseVO<?> updateFriend(UpdateFriendReq req);

}
