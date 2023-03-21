package cn.ctrlcv.im.serve.group.service.impl;

import cn.ctrlcv.im.codec.pack.group.AddGroupMemberPack;
import cn.ctrlcv.im.codec.pack.group.GroupMemberSpeakPack;
import cn.ctrlcv.im.codec.pack.group.RemoveGroupMemberPack;
import cn.ctrlcv.im.codec.pack.group.UpdateGroupMemberPack;
import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.config.ImConfig;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.GroupErrorCodeEnum;
import cn.ctrlcv.im.common.enums.GroupMemberRoleEnum;
import cn.ctrlcv.im.common.enums.GroupStatusEnum;
import cn.ctrlcv.im.common.enums.GroupTypeEnum;
import cn.ctrlcv.im.common.enums.command.GroupEventCommand;
import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.common.model.ClientInfo;
import cn.ctrlcv.im.serve.group.dao.ImGroupEntity;
import cn.ctrlcv.im.serve.group.dao.ImGroupMemberEntity;
import cn.ctrlcv.im.serve.group.dao.mapper.ImGroupMemberMapper;
import cn.ctrlcv.im.serve.group.model.callback.AddGroupMemberAfterCallbackDTO;
import cn.ctrlcv.im.serve.group.model.dto.GroupMemberDTO;
import cn.ctrlcv.im.serve.group.model.request.*;
import cn.ctrlcv.im.serve.group.model.resp.AddMemberResp;
import cn.ctrlcv.im.serve.group.model.resp.GetRoleInGroupResp;
import cn.ctrlcv.im.serve.group.model.resp.ImportGroupMemberResp;
import cn.ctrlcv.im.serve.group.service.IGroupMemberService;
import cn.ctrlcv.im.serve.group.service.IGroupService;
import cn.ctrlcv.im.serve.user.dao.ImUserDataEntity;
import cn.ctrlcv.im.serve.user.service.IUserService;
import cn.ctrlcv.im.serve.utils.CallbackService;
import cn.ctrlcv.im.serve.utils.GroupMessageProducer;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.*;

/**
 * Class Name: GroupMemberImpl
 * Class Description: 群成员操作实现
 *
 * @author liujm
 * @date 2023-03-02
 */
@Slf4j
@Service
public class GroupMemberImpl implements IGroupMemberService {

    @Resource
    private ImGroupMemberMapper groupMemberMapper;

    @Resource
    private IGroupService groupService;

    @Resource
    private IUserService userService;

    @Resource
    private IGroupMemberService groupMemberService;

    @Resource
    private ImConfig imConfig;

    @Resource
    private CallbackService callbackService;

    @Resource
    private GroupMessageProducer groupMessageProducer;


    @Override
    public ResponseVO<?> importGroupMember(ImportGroupMemberReq req) {
        List<ImportGroupMemberResp> resp = new ArrayList<>();

        ResponseVO<ImGroupEntity> groupResp = this.groupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return groupResp;
        }

        for (GroupMemberDTO memberId : req.getMembers()) {
            ResponseVO responseVO = null;
            try {
                responseVO = this.groupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), memberId);
            } catch (Exception e) {
                e.printStackTrace();
                responseVO = ResponseVO.errorResponse();
            }
            ImportGroupMemberResp addMemberResp = new ImportGroupMemberResp();
            addMemberResp.setMemberId(memberId.getMemberId());
            if (responseVO.isOk()) {
                addMemberResp.setResult(0);
            } else if (responseVO.getCode() == GroupErrorCodeEnum.USER_IS_JOINED_GROUP.getCode()) {
                addMemberResp.setResult(2);
            } else {
                addMemberResp.setResult(1);
            }
            resp.add(addMemberResp);
        }

        return ResponseVO.successResponse(resp);
    }

    @Override
    @Transactional
    public ResponseVO<?> addGroupMember(String groupId, Integer appId, GroupMemberDTO dto) {
        ResponseVO<ImUserDataEntity> singleUserInfo = this.userService.getSingleUserInfo(dto.getMemberId(), appId);
        if (!singleUserInfo.isOk()) {
            return singleUserInfo;
        }

        if (dto.getRole() != null && GroupMemberRoleEnum.OWNER.getCode() == dto.getRole()) {
            QueryWrapper<ImGroupMemberEntity> queryOwner = new QueryWrapper<>();
            queryOwner.eq(ImGroupMemberEntity.COL_GROUP_ID, groupId);
            queryOwner.eq(ImGroupMemberEntity.COL_APP_ID, appId);
            queryOwner.eq(ImGroupMemberEntity.COL_ROLE, GroupMemberRoleEnum.OWNER.getCode());
            Integer ownerNum = this.groupMemberMapper.selectCount(queryOwner);
            if (ownerNum > 0) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.GROUP_IS_HAVE_OWNER);
            }
        }

        QueryWrapper<ImGroupMemberEntity> query = new QueryWrapper<>();
        query.eq(ImGroupMemberEntity.COL_GROUP_ID, groupId);
        query.eq(ImGroupMemberEntity.COL_APP_ID, appId);
        query.eq(ImGroupMemberEntity.COL_MEMBER_ID, dto.getMemberId());
        ImGroupMemberEntity memberDto = this.groupMemberMapper.selectOne(query);

        long now = System.currentTimeMillis();
        if (memberDto == null) {
            //初次加群
            memberDto = new ImGroupMemberEntity();
            BeanUtils.copyProperties(dto, memberDto);
            memberDto.setGroupId(groupId);
            memberDto.setAppId(appId);
            memberDto.setJoinTime(now);
            int insert = this.groupMemberMapper.insert(memberDto);
            if (insert == 1) {
                return ResponseVO.successResponse();
            }
            return ResponseVO.errorResponse(GroupErrorCodeEnum.USER_JOIN_GROUP_ERROR);
        } else if (GroupMemberRoleEnum.LEAVE.getCode() == memberDto.getRole()) {
            //重新进群
            memberDto = new ImGroupMemberEntity();
            BeanUtils.copyProperties(dto, memberDto);
            memberDto.setJoinTime(now);
            int update = this.groupMemberMapper.update(memberDto, query);
            if (update == 1) {
                return ResponseVO.successResponse();
            }
            return ResponseVO.errorResponse(GroupErrorCodeEnum.USER_JOIN_GROUP_ERROR);
        }

        return ResponseVO.errorResponse(GroupErrorCodeEnum.USER_IS_JOINED_GROUP);
    }

    @Override
    public ResponseVO<List<GroupMemberDTO>> getGroupMember(String groupId, Integer appId) {
        List<GroupMemberDTO> groupMember = this.groupMemberMapper.getGroupMember(appId, groupId);
        return ResponseVO.successResponse(groupMember);
    }

    @Override
    public ResponseVO<GetRoleInGroupResp> getRoleInGroupOne(String groupId, String operator, Integer appId) {
        GetRoleInGroupResp resp = new GetRoleInGroupResp();

        QueryWrapper<ImGroupMemberEntity> queryOwner = new QueryWrapper<>();
        queryOwner.eq(ImGroupMemberEntity.COL_GROUP_ID, groupId);
        queryOwner.eq(ImGroupMemberEntity.COL_APP_ID, appId);
        queryOwner.eq(ImGroupMemberEntity.COL_MEMBER_ID, operator);

        ImGroupMemberEntity imGroupMemberEntity = this.groupMemberMapper.selectOne(queryOwner);
        if (imGroupMemberEntity == null || imGroupMemberEntity.getRole() == GroupMemberRoleEnum.LEAVE.getCode()) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.MEMBER_IS_NOT_JOINED_GROUP);
        }

        resp.setSpeakDate(imGroupMemberEntity.getSpeakDate());
        resp.setGroupMemberId(imGroupMemberEntity.getGroupMemberId());
        resp.setMemberId(imGroupMemberEntity.getMemberId());
        resp.setRole(imGroupMemberEntity.getRole());
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<Collection<String>> getMemberJoinedGroup(GetJoinedGroupReq req) {
        if (req.getLimit() != null) {
            Page<ImGroupMemberEntity> objectPage = new Page<>(req.getOffset(), req.getLimit());
            QueryWrapper<ImGroupMemberEntity> query = new QueryWrapper<>();
            query.eq("app_id", req.getAppId());
            query.eq("member_id", req.getMemberId());
            IPage<ImGroupMemberEntity> imGroupMemberEntityPage = this.groupMemberMapper.selectPage(objectPage, query);

            Set<String> groupId = new HashSet<>();
            List<ImGroupMemberEntity> records = imGroupMemberEntityPage.getRecords();
            records.forEach(e -> {
                groupId.add(e.getGroupId());
            });

            return ResponseVO.successResponse(groupId);
        } else {
            return ResponseVO.successResponse(this.groupMemberMapper.getJoinedGroupId(req.getAppId(), req.getMemberId()));
        }
    }

    @Override
    public ResponseVO<?> transferGroupMember(String ownerId, String groupId, Integer appId) {
        //更新旧群主
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        imGroupMemberEntity.setRole(GroupMemberRoleEnum.ORDINARY.getCode());
        UpdateWrapper<ImGroupMemberEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.eq(ImGroupMemberEntity.COL_APP_ID, appId);
        updateWrapper.eq(ImGroupMemberEntity.COL_GROUP_ID, groupId);
        updateWrapper.eq(ImGroupMemberEntity.COL_ROLE, GroupMemberRoleEnum.OWNER.getCode());
        this.groupMemberMapper.update(imGroupMemberEntity, updateWrapper);

        //更新新群主
        ImGroupMemberEntity newOwner = new ImGroupMemberEntity();
        newOwner.setRole(GroupMemberRoleEnum.OWNER.getCode());
        UpdateWrapper<ImGroupMemberEntity> ownerWrapper = new UpdateWrapper<>();
        ownerWrapper.eq(ImGroupMemberEntity.COL_APP_ID, appId);
        ownerWrapper.eq(ImGroupMemberEntity.COL_GROUP_ID, groupId);
        ownerWrapper.eq(ImGroupMemberEntity.COL_MEMBER_ID, ownerId);
        this.groupMemberMapper.update(newOwner, ownerWrapper);

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> addMember(AddGroupMemberReq req) {
        List<AddMemberResp> resp = new ArrayList<>();

        boolean isAdmin = false;
        ResponseVO<ImGroupEntity> groupResp = groupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return groupResp;
        }

        List<GroupMemberDTO> groupMemberDTOS = req.getMembers();
        if (imConfig.isAddGroupMemberBeforeCallback()) {
            ResponseVO responseVO = callbackService.beforeCallback(req.getAppId(), Constants.CallbackCommand.GROUP_MEMBER_ADD_BEFORE,
                    JSONObject.toJSONString(req));
            if (!responseVO.isOk()) {
                return responseVO;
            }

            //  让用户指定哪些人可以添加
            try {
                groupMemberDTOS = JSONArray.parseArray(JSONObject.toJSONString(responseVO.getData()), GroupMemberDTO.class);
            } catch (Exception e) {
                log.error(" command： GROUP_MEMBER_ADD_BEFORE， 回调失败， APPID: {}", req.getAppId());
                e.printStackTrace();
            }
        }

        List<GroupMemberDTO> memberDTOs = req.getMembers();

        ImGroupEntity group = groupResp.getData();


        /*
         * 私有群（private）	类似普通微信群，创建后仅支持已在群内的好友邀请加群，且无需被邀请方同意或群主审批
         * 公开群（Public）	类似 QQ 群，创建后群主可以指定群管理员，需要群主或管理员审批通过才能入群
         * 群类型 1私有群（类似微信） 2公开群(类似qq）
         *
         */

        if (!isAdmin && GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {
            throw new ApplicationException(GroupErrorCodeEnum.THIS_OPERATE_NEED_APPMANAGER_ROLE);
        }

        List<String> successId = new ArrayList<>();
        for (GroupMemberDTO memberId : memberDTOs) {
            ResponseVO responseVO;
            try {
                responseVO = groupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), memberId);
            } catch (Exception e) {
                e.printStackTrace();
                responseVO = ResponseVO.errorResponse();
            }
            AddMemberResp addMemberResp = new AddMemberResp();
            addMemberResp.setMemberId(memberId.getMemberId());
            if (responseVO.isOk()) {
                successId.add(memberId.getMemberId());
                addMemberResp.setResult(0);
            } else if (responseVO.getCode() == GroupErrorCodeEnum.USER_IS_JOINED_GROUP.getCode()) {
                addMemberResp.setResult(2);
                addMemberResp.setResultMessage(responseVO.getMsg());
            } else {
                addMemberResp.setResult(1);
                addMemberResp.setResultMessage(responseVO.getMsg());
            }
            resp.add(addMemberResp);
        }

        AddGroupMemberPack addGroupMemberPack = new AddGroupMemberPack();
        addGroupMemberPack.setGroupId(req.getGroupId());
        addGroupMemberPack.setMembers(successId);
        groupMessageProducer.producer(req.getOperator(), GroupEventCommand.ADDED_MEMBER, addGroupMemberPack,
                new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

        if (imConfig.isAddGroupMemberAfterCallback()) {
            AddGroupMemberAfterCallbackDTO callbackDTO = new AddGroupMemberAfterCallbackDTO();
            callbackDTO.setGroupId(req.getGroupId());
            callbackDTO.setOperator(req.getOperator());
            callbackDTO.setGroupType(group.getGroupType());
            callbackDTO.setMemberList(resp);
            callbackService.beforeCallback(req.getAppId(), Constants.CallbackCommand.GROUP_MEMBER_ADD_AFTER, JSONObject.toJSONString(callbackDTO));
        }

        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<?> removeMember(RemoveGroupMemberReq req) {
        boolean isAdmin = false;
        ResponseVO<ImGroupEntity> groupResp = this.groupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return groupResp;
        }

        ImGroupEntity group = groupResp.getData();

        if (!isAdmin) {
            if (GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {

                //获取操作人的权限 是管理员or群主or群成员
                ResponseVO<GetRoleInGroupResp> role = getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
                if (!role.isOk()) {
                    return role;
                }

                GetRoleInGroupResp data = role.getData();
                Integer roleInfo = data.getRole();

                boolean isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
                boolean isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode();

                if (!isOwner && !isManager) {
                    throw new ApplicationException(GroupErrorCodeEnum.THIS_OPERATE_NEED_MANAGER_ROLE);
                }

                //私有群必须是群主才能踢人
                if (!isOwner && GroupTypeEnum.PRIVATE.getCode() == group.getGroupType()) {
                    throw new ApplicationException(GroupErrorCodeEnum.THIS_OPERATE_NEED_OWNER_ROLE);
                }

                //公开群管理员和群主可踢人，但管理员只能踢普通群成员
                if (GroupTypeEnum.PUBLIC.getCode() == group.getGroupType()) {
                    //获取被踢人的权限
                    ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
                    if (!roleInGroupOne.isOk()) {
                        return roleInGroupOne;
                    }
                    GetRoleInGroupResp memberRole = roleInGroupOne.getData();
                    if (memberRole.getRole() == GroupMemberRoleEnum.OWNER.getCode()) {
                        throw new ApplicationException(GroupErrorCodeEnum.GROUP_OWNER_IS_NOT_REMOVE);
                    }
                    //是管理员并且被踢人不是群成员，无法操作
                    if (isManager && memberRole.getRole() != GroupMemberRoleEnum.ORDINARY.getCode()) {
                        throw new ApplicationException(GroupErrorCodeEnum.THIS_OPERATE_NEED_OWNER_ROLE);
                    }
                }
            }
        }

        ResponseVO<?> responseVO = this.groupMemberService.removeGroupMember(req.getGroupId(), req.getAppId(), req.getMemberId());

        if (responseVO.isOk()) {
            RemoveGroupMemberPack removeGroupMemberPack = new RemoveGroupMemberPack();
            removeGroupMemberPack.setGroupId(req.getGroupId());
            removeGroupMemberPack.setMember(req.getMemberId());
            groupMessageProducer.producer(req.getMemberId(), GroupEventCommand.DELETED_MEMBER, removeGroupMemberPack,
                    new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

            if (imConfig.isDeleteGroupMemberAfterCallback()) {
                callbackService.beforeCallback(req.getAppId(), Constants.CallbackCommand.GROUP_MEMBER_DELETE_AFTER, JSONObject.toJSONString(req));
            }
        }

        return responseVO;
    }


    @Transactional
    @Override
    public ResponseVO<?> removeGroupMember(String groupId, Integer appId, String memberId) {
        ResponseVO<ImUserDataEntity> singleUserInfo = this.userService.getSingleUserInfo(memberId, appId);
        if (!singleUserInfo.isOk()) {
            return singleUserInfo;
        }

        ResponseVO<GetRoleInGroupResp> roleInGroupOne = getRoleInGroupOne(groupId, memberId, appId);
        if (!roleInGroupOne.isOk()) {
            return roleInGroupOne;
        }

        GetRoleInGroupResp data = roleInGroupOne.getData();
        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        imGroupMemberEntity.setRole(GroupMemberRoleEnum.LEAVE.getCode());
        imGroupMemberEntity.setLeaveTime(System.currentTimeMillis());
        imGroupMemberEntity.setGroupMemberId(data.getGroupMemberId());
        this.groupMemberMapper.updateById(imGroupMemberEntity);
        return ResponseVO.successResponse();
    }


    @Override
    public ResponseVO exitGroup(ExitGroupReq req) {
        return this.groupMemberService.removeGroupMember(req.getGroupId(), req.getAppId(), req.getOperator());
    }


    @Override
    public ResponseVO updateGroupMember(UpdateGroupMemberReq req) {
        boolean isadmin = false;

        ResponseVO<ImGroupEntity> group = groupService.getGroup(req.getGroupId(), req.getAppId());
        if (!group.isOk()) {
            return group;
        }

        ImGroupEntity groupData = group.getData();
        if (groupData.getStatus() == GroupStatusEnum.DESTROY.getCode()) {
            throw new ApplicationException(GroupErrorCodeEnum.GROUP_IS_DESTROY);
        }

        //是否是自己修改自己的资料
        boolean isMeOperate = req.getOperator().equals(req.getMemberId());

        if (!isadmin) {
            //昵称只能自己修改 权限只能群主或管理员修改
            if (StrUtil.isBlank(req.getAlias()) && !isMeOperate) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATE_NEED_ONESELF);
            }
            //私有群不能设置管理员
            if (groupData.getGroupType() == GroupTypeEnum.PRIVATE.getCode() &&
                    req.getRole() != null && (req.getRole() == GroupMemberRoleEnum.MANAGER.getCode() ||
                    req.getRole() == GroupMemberRoleEnum.OWNER.getCode())) {
                return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATE_NEED_MANAGER_ROLE);
            }

            //如果要修改权限相关的则走下面的逻辑
            if (req.getRole() != null) {
                //获取被操作人的是否在群内
                ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
                if (!roleInGroupOne.isOk()) {
                    return roleInGroupOne;
                }

                //获取操作人权限
                ResponseVO<GetRoleInGroupResp> operateRoleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
                if (!operateRoleInGroupOne.isOk()) {
                    return operateRoleInGroupOne;
                }

                GetRoleInGroupResp data = operateRoleInGroupOne.getData();
                Integer roleInfo = data.getRole();
                boolean isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
                boolean isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode();

                //不是管理员不能修改权限
                if (req.getRole() != null && !isOwner && !isManager) {
                    return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATE_NEED_MANAGER_ROLE);
                }

                //管理员只有群主能够设置
                if (req.getRole() != null && req.getRole() == GroupMemberRoleEnum.MANAGER.getCode() && !isOwner) {
                    return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATE_NEED_OWNER_ROLE);
                }

            }
        }

        ImGroupMemberEntity update = new ImGroupMemberEntity();

        if (StrUtil.isNotBlank(req.getAlias())) {
            update.setAlias(req.getAlias());
        }

        //不能直接修改为群主
        if (req.getRole() != null && req.getRole() != GroupMemberRoleEnum.OWNER.getCode()) {
            update.setRole(req.getRole());
        }

        UpdateWrapper<ImGroupMemberEntity> objectUpdateWrapper = new UpdateWrapper<>();
        objectUpdateWrapper.eq(ImGroupMemberEntity.COL_APP_ID, req.getAppId());
        objectUpdateWrapper.eq(ImGroupMemberEntity.COL_MEMBER_ID, req.getMemberId());
        objectUpdateWrapper.eq(ImGroupMemberEntity.COL_GROUP_ID, req.getGroupId());
        this.groupMemberMapper.update(update, objectUpdateWrapper);

        UpdateGroupMemberPack pack = new UpdateGroupMemberPack();
        BeanUtils.copyProperties(req, pack);
        groupMessageProducer.producer(req.getOperator(), GroupEventCommand.UPDATED_MEMBER, pack,
                new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));

        return ResponseVO.successResponse();
    }


    @Override
    public ResponseVO speak(SpeakMemberReq req) {
        ResponseVO<ImGroupEntity> groupResp = groupService.getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return groupResp;
        }

        boolean isadmin = false;
        boolean isOwner = false;
        boolean isManager = false;
        GetRoleInGroupResp memberRole = null;

        if (!isadmin) {

            //获取操作人的权限 是管理员or群主or群成员
            ResponseVO<GetRoleInGroupResp> role = getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
            if (!role.isOk()) {
                return role;
            }

            GetRoleInGroupResp data = role.getData();
            Integer roleInfo = data.getRole();

            isOwner = roleInfo == GroupMemberRoleEnum.OWNER.getCode();
            isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode();

            if (!isOwner && !isManager) {
                throw new ApplicationException(GroupErrorCodeEnum.THIS_OPERATE_NEED_MANAGER_ROLE);
            }

            //获取被操作的权限
            ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
            if (!roleInGroupOne.isOk()) {
                return roleInGroupOne;
            }
            memberRole = roleInGroupOne.getData();
            //被操作人是群主只能app管理员操作
            if (memberRole.getRole() == GroupMemberRoleEnum.OWNER.getCode()) {
                throw new ApplicationException(GroupErrorCodeEnum.THIS_OPERATE_NEED_APPMANAGER_ROLE);
            }

            //是管理员并且被操作人不是群成员，无法操作
            if (isManager && memberRole.getRole() != GroupMemberRoleEnum.ORDINARY.getCode()) {
                throw new ApplicationException(GroupErrorCodeEnum.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }

        ImGroupMemberEntity imGroupMemberEntity = new ImGroupMemberEntity();
        if (memberRole == null) {
            //获取被操作的权限
            ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.getRoleInGroupOne(req.getGroupId(), req.getMemberId(), req.getAppId());
            if (!roleInGroupOne.isOk()) {
                return roleInGroupOne;
            }
            memberRole = roleInGroupOne.getData();
        }

        imGroupMemberEntity.setGroupMemberId(memberRole.getGroupMemberId());
        if (req.getSpeakDate() > 0) {
            imGroupMemberEntity.setSpeakDate(System.currentTimeMillis() + req.getSpeakDate());
        } else {
            imGroupMemberEntity.setSpeakDate(req.getSpeakDate());
        }

        int i = this.groupMemberMapper.updateById(imGroupMemberEntity);
        if (i == 1) {
            GroupMemberSpeakPack pack = new GroupMemberSpeakPack();
            BeanUtils.copyProperties(req, pack);
            groupMessageProducer.producer(req.getOperator(), GroupEventCommand.SPEAK_GROUP_MEMBER, pack,
                    new ClientInfo(req.getAppId(), req.getClientType(), req.getImei()));
        }

        return ResponseVO.successResponse();
    }

    @Override
    public List<String> getGroupMemberId(String groupId, Integer appId) {
        return this.groupMemberMapper.getGroupMemberId(appId, groupId);
    }

    @Override
    public List<GroupMemberDTO> getGroupManager(String groupId, Integer appId) {
        return this.groupMemberMapper.getGroupManager(appId, groupId);
    }
}
