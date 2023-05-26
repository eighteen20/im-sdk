package cn.ctrlcv.im.serve.group.service;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.model.SyncReq;
import cn.ctrlcv.im.serve.group.dao.ImGroupEntity;
import cn.ctrlcv.im.serve.group.model.request.*;
import cn.ctrlcv.im.serve.group.model.resp.GetGroupResp;
import cn.ctrlcv.im.serve.group.model.resp.GetJoinedGroupResp;

import java.util.Collection;

/**
 * interface Name: IGroupService
 * interface Description: 群组功能接口定义
 *
 *
 * @author liujm
 * @date 2023-03-02
 */
public interface IGroupService {

    /**
     * 导入群组
     *
     * @param req {@link ImportGroupReq}
     * @return No
     */
    ResponseVO<?> importGroup(ImportGroupReq req);

    /**
     * 获取群组信息
     *
     * @param groupId 群ID
     * @param appId 应用ID
     * @return {@link ImGroupEntity}
     */
    ResponseVO<ImGroupEntity> getGroup(String groupId, Integer appId);

    /**
     * 创建群组
     *
     * @param req {@link CreateGroupReq}
     * @return No
     */
    ResponseVO<?> createGroup(CreateGroupReq req);

    /**
     * 获取群组信息
     *
     * @param req {@link GetGroupReq}
     * @return {@link GetGroupResp}
     */
    ResponseVO<GetGroupResp> getGroup(GetGroupReq req);

    /**
     * 修改群组信息
     * <p>
     * 修改群基础信息，如果是后台管理员调用，则不检查权限，如果不是则检查权限，如果是私有群（微信群）任何人都可以修改资料，公开群只有管理员可以修改.
     * 如果是群主或者管理员可以修改其他信息。
     * </p>
     *
     * @param req {@link UpdateGroupReq}
     * @return No
     */
    ResponseVO<?> updateBaseGroupInfo(UpdateGroupReq req);


    /**
     * 获取用户加入的群列表
     *
     * @param req {@link GetJoinedGroupReq}
     * @return {@link GetJoinedGroupResp}
     */
    ResponseVO<?> getJoinedGroup(GetJoinedGroupReq req);


    /**
     * 解散群组，只支持后台管理员和群主解散
     *
     * @param req {@link DestroyGroupReq}
     * @return
     */
    ResponseVO destroyGroup(DestroyGroupReq req);


    /**
     * 转移群主
     *
     * @param req {@link TransferGroupReq}
     * @return
     */
    ResponseVO transferGroup(TransferGroupReq req);


    /**
     * 禁言群
     *
     * @param req {@link MuteGroupReq}
     * @return
     */
    ResponseVO muteGroup(MuteGroupReq req);

    /**
     * 同步群组
     *
     * @param req {@link SyncReq}
     * @return
     */
    ResponseVO syncJoinedGroup(SyncReq req);

    /**
     * 获取用户群组最大的sequence
     *
     * @param appId 应用ID
     * @param userId 用户ID
     * @return 最大的sequence
     */
    Long getUserGroupMaxSequence(Integer appId, String userId);
}
