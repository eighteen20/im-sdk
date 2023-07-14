package cn.ctrlcv.im.serve.friendship.service;

import cn.ctrlcv.im.common.model.ResponseVO;
import cn.ctrlcv.im.serve.friendship.dao.ImFriendshipRequestEntity;
import cn.ctrlcv.im.serve.friendship.model.request.ApproveFriendRequestReq;
import cn.ctrlcv.im.serve.friendship.model.request.FriendDTO;
import cn.ctrlcv.im.serve.friendship.model.request.ReadFriendShipRequestReq;

/**
 * interface Name: IFriendshipRequestService
 * interface Description: 好友申请相关的接口
 *
 * @author liujm
 * @date 2023-02-23
 */
public interface IFriendshipRequestService {

    /**
     * 新增一条好友申请记录
     *
     * @param fromId 用户ID
     * @param dto {@link FriendDTO}
     * @param appId 应用ID
     * @return 无
     */
    ResponseVO<?> addFriendshipRequest(String fromId, FriendDTO dto, Integer appId);

    /**
     * 审批好友请求
     *
     * @param req {@link ApproveFriendRequestReq}
     * @return 无
     */
    ResponseVO<?> approveFriendshipRequest(ApproveFriendRequestReq req);

     /**
     * 获取好友申请记录
     *
     * @param fromId 用户ID
     * @param appId 应用ID
     * @return
     */
    ResponseVO<ImFriendshipRequestEntity> getFriendRequest(String fromId, Integer appId);

    /**
     * 查看好友申请记录
     *
     * @param req {@link ReadFriendShipRequestReq}
     * @return
     */
    ResponseVO readFriendShipRequestReq(ReadFriendShipRequestReq req);
}
