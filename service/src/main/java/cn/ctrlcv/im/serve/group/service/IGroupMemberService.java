package cn.ctrlcv.im.serve.group.service;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.serve.group.model.dto.GroupMemberDTO;
import cn.ctrlcv.im.serve.group.model.request.*;
import cn.ctrlcv.im.serve.group.model.resp.GetRoleInGroupResp;

import java.util.Collection;
import java.util.List;

/**
 * interface Name: IGroupMemberService
 * interface Description: 群成员操作接口
 *
 * @author liujm
 * @date 2023-03-02
 */
public interface IGroupMemberService {

    /**
     * 导入群成员
     *
     * @param req {@link ImportGroupMemberReq}
     * @return
     */
    ResponseVO<?> importGroupMember(ImportGroupMemberReq req);

    /**
     * 插入单条群成员记录
     *
     * @param groupId 群组ID
     * @param appId 应用ID
     * @param dto 群成员信息
     * @return
     */
    ResponseVO<?> addGroupMember(String groupId, Integer appId, GroupMemberDTO dto);

    /**
     * 获取群组成员
     *
     * @param groupId 群组ID
     * @param appId 应用ID
     * @return
     */
    ResponseVO<List<GroupMemberDTO>> getGroupMember(String groupId, Integer appId);

    /**
     * 查询当前用户在群内的角色
     *
     * @param groupId
     * @param operator
     * @param appId
     * @return
     */
    ResponseVO<GetRoleInGroupResp> getRoleInGroupOne(String groupId, String operator, Integer appId);

    /**
     * 获取用户加入的群组ID
     *
     * @param req
     * @return 群组ID集合
     */
    ResponseVO<Collection<String>> getMemberJoinedGroup(GetJoinedGroupReq req);

    /**
     * 群组转移群主
     *
     * @param ownerId 新群主ID
     * @param groupId 群组ID
     * @param appId 应用ID
     * @return
     */
    ResponseVO<?> transferGroupMember(String ownerId, String groupId, Integer appId);

    /**
     * 添加群成员，拉人入群的逻辑，直接进入群聊
     * <p>
     *  如果是后台管理员，则直接拉入群，
     *  并且群成员也可以拉人入群.只有私有群可以调用本接口
     * </p>
     *
     * @param req {@link AddGroupMemberReq}
     * @return
     */
    ResponseVO<?> addMember(AddGroupMemberReq req);


    /**
     * 将群成员移出群组
     *
     * @param req {@link RemoveGroupMemberReq}
     * @return
     */
    ResponseVO<?> removeMember(RemoveGroupMemberReq req);

    /**
     * 将群成员移出群聊（直接操作数据库, 内部调用）
     *
     * @param groupId 群组ID
     * @param appId 应用ID
     * @param memberId 成员ID
     * @return
     */
    ResponseVO<?> removeGroupMember(String groupId, Integer appId, String memberId);


    /**
     * 群成员主动退出群聊
     *
     * @param req
     * @return
     */
    ResponseVO exitGroup(ExitGroupReq req);


    /**
     * 修改群成员信息
     *
     * @param req {@link UpdateGroupMemberReq}
     * @return
     */
    ResponseVO updateGroupMember(UpdateGroupMemberReq req);

    /**
     * 禁言群成员
     *
     * @param req {@link SpeakMemberReq}
     * @return
     */
    ResponseVO speak(SpeakMemberReq req);

    /**
     * 获取群组成员Id
     *
     * @param groupId 群组ID
     * @param appId 应用ID
     * @return 群成员ID集合
     */
    List<String> getGroupMemberId(String groupId, Integer appId);


    /**
     * 获取群组管理员
     *
     * @param groupId 群组ID
     * @param appId 应用ID
     * @return 群成员ID集合
     */
    List<GroupMemberDTO> getGroupManager(String groupId, Integer appId);

    /**
     * 获取用户加入的群组ID
     *
     * @param appId 应用ID
     * @param operator 用户ID
     * @return 群组ID集合
     */
    ResponseVO<List<String>> syncMemberJoinedGroup(Integer appId, String operator);
}
