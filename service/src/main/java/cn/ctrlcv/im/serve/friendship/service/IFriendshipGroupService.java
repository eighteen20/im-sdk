package cn.ctrlcv.im.serve.friendship.service;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.friendship.dao.ImFriendshipGroupEntity;
import cn.ctrlcv.im.serve.friendship.model.request.AddFriendShipGroupReq;
import cn.ctrlcv.im.serve.friendship.model.request.DeleteFriendShipGroupReq;

/**
 * interface Name: IFriendshipGroup
 * interface Description: 好友分组 业务接口
 *
 * @author liujm
 * @date 2023-02-28
 */
public interface IFriendshipGroupService {

    /**
     * 新增分组
     *
     * @param req {@link AddFriendShipGroupReq}
     * @return 无
     */
    ResponseVO<?> addGroup(AddFriendShipGroupReq req);

    /**
     * 删除分组
     *
     * @param req {@link DeleteFriendShipGroupReq}
     * @return 无
     */
    ResponseVO<?> deleteGroup(DeleteFriendShipGroupReq req);

    /**
     * 获取分组信息
     *
     * @param fromId 用户ID
     * @param groupName 分组名称
     * @param appId 应用ID
     * @return {@link ImFriendshipGroupEntity}
     */
    ResponseVO<ImFriendshipGroupEntity> getGroup(String fromId, String groupName, Integer appId);
}
