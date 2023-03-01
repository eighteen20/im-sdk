package cn.ctrlcv.im.serve.friendship.service;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.friendship.model.request.AddFriendShipGroupMemberReq;
import cn.ctrlcv.im.serve.friendship.model.request.DeleteFriendShipGroupMemberReq;

/**
 * interface Name: IFriendShipGroupMemberService
 * interface Description: 好友分组成员业务接口
 *
 * @author liujm
 * @date 2023-02-28
 */
public interface IFriendShipGroupMemberService {

    /**
     * 把好友添加进分组
     *
     * @param req {@link AddFriendShipGroupMemberReq}
     * @return 无
     */
    ResponseVO<?> addGroupMember(AddFriendShipGroupMemberReq req);

    /**
     * 把好友从分组中移除
     *
     * @param req {@link DeleteFriendShipGroupMemberReq}
     * @return 无
     */
    ResponseVO<?> delGroupMember(DeleteFriendShipGroupMemberReq req);

    /**
     * 将好用添加进分组（直接添加，写入数据库）
     *
     * @param groupId 分组ID
     * @param toId 好友ID
     * @return
     */
    int doAddGroupMember(Long groupId, String toId);


    /**
     * 清除分组内的好友
     *
     * @param groupId 分组ID
     * @return 清除数量
     */
    int clearGroupMember(Long groupId);
}
