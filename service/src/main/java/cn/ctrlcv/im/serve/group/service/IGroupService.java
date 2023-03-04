package cn.ctrlcv.im.serve.group.service;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.group.dao.ImGroupEntity;
import cn.ctrlcv.im.serve.group.model.request.CreateGroupReq;
import cn.ctrlcv.im.serve.group.model.request.GetGroupReq;
import cn.ctrlcv.im.serve.group.model.request.ImportGroupReq;
import cn.ctrlcv.im.serve.group.model.request.UpdateGroupReq;
import cn.ctrlcv.im.serve.group.model.resp.GetGroupResp;

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
}
