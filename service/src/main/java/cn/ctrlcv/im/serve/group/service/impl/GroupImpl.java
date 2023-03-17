package cn.ctrlcv.im.serve.group.service.impl;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.config.ImConfig;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.GroupErrorCodeEnum;
import cn.ctrlcv.im.common.enums.GroupMemberRoleEnum;
import cn.ctrlcv.im.common.enums.GroupStatusEnum;
import cn.ctrlcv.im.common.enums.GroupTypeEnum;
import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.serve.group.dao.ImGroupEntity;
import cn.ctrlcv.im.serve.group.dao.ImGroupMemberEntity;
import cn.ctrlcv.im.serve.group.dao.mapper.ImGroupMapper;
import cn.ctrlcv.im.serve.group.dao.mapper.ImGroupMemberMapper;
import cn.ctrlcv.im.serve.group.model.callback.DestroyGroupAfterCallbackDTO;
import cn.ctrlcv.im.serve.group.model.dto.GroupMemberDTO;
import cn.ctrlcv.im.serve.group.model.request.*;
import cn.ctrlcv.im.serve.group.model.resp.GetGroupResp;
import cn.ctrlcv.im.serve.group.model.resp.GetJoinedGroupResp;
import cn.ctrlcv.im.serve.group.model.resp.GetRoleInGroupResp;
import cn.ctrlcv.im.serve.group.service.IGroupMemberService;
import cn.ctrlcv.im.serve.group.service.IGroupService;
import cn.ctrlcv.im.serve.utils.CallbackService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.toolkit.CollectionUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

import javax.annotation.Resource;
import java.util.*;

/**
 * Class Name: GroupImpl
 * Class Description: 群组操作实现
 *
 * @author liujm
 * @date 2023-03-02
 */
@Service
public class GroupImpl implements IGroupService {

    @Resource
    private ImGroupMapper groupMapper;

    @Resource
    private IGroupMemberService groupMemberService;

    @Resource
    private ImConfig imConfig;

    @Resource
    private CallbackService callbackService;


    @Override
    public ResponseVO<?> importGroup(ImportGroupReq req) {

        if (StringUtils.isEmpty(req.getGroupId())) {
            req.setGroupId(IdUtil.fastSimpleUUID().toUpperCase());
        }
        QueryWrapper<ImGroupEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImGroupEntity.COL_GROUP_ID, req.getGroupId());
        queryWrapper.eq(ImGroupEntity.COL_APP_ID, req.getAppId());
        if (this.groupMapper.selectCount(queryWrapper) > 0) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.GROUP_IS_EXIST);
        }

        ImGroupEntity entity = new ImGroupEntity();
        BeanUtil.copyProperties(req, entity);
        if (ObjectUtils.isEmpty(req.getCreateTime())) {
            entity.setCreateTime(System.currentTimeMillis());
        }
        if (ObjectUtils.isEmpty(req.getStatus())) {
            entity.setStatus(GroupStatusEnum.NORMAL.getCode());
        }
        int insert = this.groupMapper.insert(entity);
        if (insert != 1) {
            throw new ApplicationException(GroupErrorCodeEnum.IMPORT_GROUP_ERROR);
        }

        return ResponseVO.errorResponse();
    }

    @Override
    public ResponseVO<ImGroupEntity> getGroup(String groupId, Integer appId) {
        QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
        query.eq(ImGroupEntity.COL_APP_ID, appId);
        query.eq(ImGroupEntity.COL_GROUP_ID, groupId);
        ImGroupEntity imGroupEntity = this.groupMapper.selectOne(query);

        if (imGroupEntity == null) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.GROUP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(imGroupEntity);
    }

    @Transactional
    @Override
    public ResponseVO createGroup(CreateGroupReq req) {
        boolean isAdmin = false;

        if (!isAdmin) {
            req.setOwnerId(req.getOperator());
        }

        //1.判断群id是否存在
        QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();

        if (StringUtils.isEmpty(req.getGroupId())) {
            req.setGroupId(IdUtil.fastSimpleUUID().toUpperCase());
        } else {
            query.eq(ImGroupEntity.COL_GROUP_ID, req.getGroupId());
            query.eq(ImGroupEntity.COL_APP_ID, req.getAppId());
            Integer integer = this.groupMapper.selectCount(query);
            if (integer > 0) {
                throw new ApplicationException(GroupErrorCodeEnum.GROUP_IS_EXIST);
            }
        }

        if (req.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && StrUtil.isBlank(req.getOwnerId())) {
            throw new ApplicationException(GroupErrorCodeEnum.PUBLIC_GROUP_MUST_HAVE_OWNER);
        }

        ImGroupEntity imGroupEntity = new ImGroupEntity();
        imGroupEntity.setCreateTime(System.currentTimeMillis());
        imGroupEntity.setStatus(GroupStatusEnum.NORMAL.getCode());
        BeanUtils.copyProperties(req, imGroupEntity);
        int insert = this.groupMapper.insert(imGroupEntity);

        GroupMemberDTO groupMemberDto = new GroupMemberDTO();
        groupMemberDto.setMemberId(req.getOwnerId());
        groupMemberDto.setRole(GroupMemberRoleEnum.OWNER.getCode());
        groupMemberDto.setJoinTime(System.currentTimeMillis());
        this.groupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), groupMemberDto);

        //插入群成员
        for (GroupMemberDTO dto : req.getMember()) {
            this.groupMemberService.addGroupMember(req.getGroupId(), req.getAppId(), dto);
        }

        if (imConfig.isCreateGroupAfterCallback()) {
            callbackService.beforeCallback(req.getAppId(), Constants.CallbackCommand.CREATE_GROUP_AFTER, JSONObject.toJSONString(imGroupEntity));
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<GetGroupResp> getGroup(GetGroupReq req) {
        ResponseVO group = this.getGroup(req.getGroupId(), req.getAppId());

        if(!group.isOk()){
            return group;
        }

        GetGroupResp getGroupResp = new GetGroupResp();
        BeanUtils.copyProperties(group.getData(), getGroupResp);
        try {
            ResponseVO<List<GroupMemberDTO>> groupMember = this.groupMemberService.getGroupMember(req.getGroupId(), req.getAppId());
            if (groupMember.isOk()) {
                getGroupResp.setMemberList(groupMember.getData());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseVO.successResponse(getGroupResp);
    }

    @Transactional
    @Override
    public ResponseVO updateBaseGroupInfo(UpdateGroupReq req) {
        //1.判断群id是否存在
        QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
        query.eq(ImGroupEntity.COL_GROUP_ID, req.getGroupId());
        query.eq(ImGroupEntity.COL_APP_ID, req.getAppId());
        ImGroupEntity imGroupEntity = this.groupMapper.selectOne(query);
        if (imGroupEntity == null) {
            throw new ApplicationException(GroupErrorCodeEnum.GROUP_IS_EXIST);
        }

        if(imGroupEntity.getStatus() == GroupStatusEnum.DESTROY.getCode()){
            throw new ApplicationException(GroupErrorCodeEnum.GROUP_IS_DESTROY);
        }

        // TODO 是否是后台管理员
        boolean isAdmin = false;

        if (!isAdmin) {
            //不是后台调用需要检查权限
            ResponseVO<GetRoleInGroupResp> role = this.groupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
            if (!role.isOk()) {
                return role;
            }

            GetRoleInGroupResp data = role.getData();
            Integer roleInfo = data.getRole();

            boolean isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode() || roleInfo == GroupMemberRoleEnum.OWNER.getCode();

            //公开群只能群主修改资料
            if (!isManager && GroupTypeEnum.PUBLIC.getCode() == imGroupEntity.getGroupType()) {
                throw new ApplicationException(GroupErrorCodeEnum.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
        }

        ImGroupEntity update = new ImGroupEntity();
        BeanUtils.copyProperties(req, update);
        update.setUpdateTime(System.currentTimeMillis());
        int row = this.groupMapper.update(update, query);
        if (row != 1) {
            throw new ApplicationException(GroupErrorCodeEnum.UPDATE_GROUP_BASE_INFO_ERROR);
        }

        if (imConfig.isModifyGroupAfterCallback()) {
            ImGroupEntity groupEntity = this.groupMapper.selectOne(query);
            callbackService.beforeCallback(req.getAppId(), Constants.CallbackCommand.UPDATE_GROUP_AFTER, JSONObject.toJSONString(groupEntity));
        }


        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> getJoinedGroup(GetJoinedGroupReq req) {
        ResponseVO<Collection<String>> memberJoinedGroup = this.groupMemberService.getMemberJoinedGroup(req);
        if (memberJoinedGroup.isOk()) {

            GetJoinedGroupResp resp = new GetJoinedGroupResp();

            if (CollectionUtils.isEmpty(memberJoinedGroup.getData())) {
                resp.setTotalCount(0);
                resp.setGroupList(new ArrayList<>());
                return ResponseVO.successResponse(resp);
            }

            QueryWrapper<ImGroupEntity> query = new QueryWrapper<>();
            query.eq(ImGroupEntity.COL_APP_ID, req.getAppId());
            query.in(ImGroupEntity.COL_GROUP_ID, memberJoinedGroup.getData());

            if (CollectionUtils.isNotEmpty(req.getGroupType())) {
                query.in(ImGroupEntity.COL_GROUP_TYPE, req.getGroupType());
            }

            List<ImGroupEntity> groupList = this.groupMapper.selectList(query);
            resp.setGroupList(groupList);
            if (req.getLimit() == null) {
                resp.setTotalCount(groupList.size());
            } else {
                resp.setTotalCount(this.groupMapper.selectCount(query));
            }
            return ResponseVO.successResponse(resp);
        } else {
            return memberJoinedGroup;
        }
    }

    @Transactional
    @Override
    public ResponseVO destroyGroup(DestroyGroupReq req) {
        boolean isAdmin = false;

        QueryWrapper<ImGroupEntity> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq(ImGroupEntity.COL_GROUP_ID, req.getGroupId());
        objectQueryWrapper.eq(ImGroupEntity.COL_APP_ID, req.getAppId());
        ImGroupEntity imGroupEntity = this.groupMapper.selectOne(objectQueryWrapper);
        if (imGroupEntity == null) {
            throw new ApplicationException(GroupErrorCodeEnum.PRIVATE_GROUP_CAN_NOT_DESTORY);
        }

        if(imGroupEntity.getStatus() == GroupStatusEnum.DESTROY.getCode()){
            throw new ApplicationException(GroupErrorCodeEnum.GROUP_IS_DESTROY);
        }

        if (!isAdmin) {
            if (imGroupEntity.getGroupType() == GroupTypeEnum.PUBLIC.getCode()) {
                throw new ApplicationException(GroupErrorCodeEnum.THIS_OPERATE_NEED_OWNER_ROLE);
            }

            if (imGroupEntity.getGroupType() == GroupTypeEnum.PUBLIC.getCode() && !imGroupEntity.getOwnerId().equals(req.getOperator())) {
                throw new ApplicationException(GroupErrorCodeEnum.THIS_OPERATE_NEED_OWNER_ROLE);
            }
        }

        ImGroupEntity update = new ImGroupEntity();

        update.setStatus(GroupStatusEnum.DESTROY.getCode());
        int update1 = this.groupMapper.update(update, objectQueryWrapper);
        if (update1 != 1) {
            throw new ApplicationException(GroupErrorCodeEnum.UPDATE_GROUP_BASE_INFO_ERROR);
        }

        if (imConfig.isDestroyGroupAfterCallback()) {
            DestroyGroupAfterCallbackDTO callbackDTO = new DestroyGroupAfterCallbackDTO();
            callbackDTO.setGroupId(req.getGroupId());
            callbackService.beforeCallback(req.getAppId(), Constants.CallbackCommand.DESTROY_GROUP_AFTER, JSONObject.toJSONString(callbackDTO));
        }


        return ResponseVO.successResponse();
    }

    @Transactional
    @Override
    public ResponseVO transferGroup(TransferGroupReq req) {
        ResponseVO<GetRoleInGroupResp> roleInGroupOne = this.groupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());
        if (!roleInGroupOne.isOk()) {
            return roleInGroupOne;
        }

        if (roleInGroupOne.getData().getRole() != GroupMemberRoleEnum.OWNER.getCode()) {
            return ResponseVO.errorResponse(GroupErrorCodeEnum.THIS_OPERATE_NEED_OWNER_ROLE);
        }

        ResponseVO<GetRoleInGroupResp> newOwnerRole = groupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOwnerId(), req.getAppId());
        if (!newOwnerRole.isOk()) {
            return newOwnerRole;
        }

        QueryWrapper<ImGroupEntity> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq(ImGroupEntity.COL_GROUP_ID, req.getGroupId());
        objectQueryWrapper.eq(ImGroupEntity.COL_APP_ID, req.getAppId());
        ImGroupEntity imGroupEntity = this.groupMapper.selectOne(objectQueryWrapper);
        if(imGroupEntity.getStatus() == GroupStatusEnum.DESTROY.getCode()){
            throw new ApplicationException(GroupErrorCodeEnum.GROUP_IS_DESTROY);
        }

        ImGroupEntity updateGroup = new ImGroupEntity();
        updateGroup.setOwnerId(req.getOwnerId());
        UpdateWrapper<ImGroupEntity> updateGroupWrapper = new UpdateWrapper<>();
        updateGroupWrapper.eq(ImGroupEntity.COL_APP_ID, req.getAppId());
        updateGroupWrapper.eq(ImGroupEntity.COL_GROUP_ID, req.getGroupId());
        this.groupMapper.update(updateGroup, updateGroupWrapper);
        this.groupMemberService.transferGroupMember(req.getOwnerId(), req.getGroupId(), req.getAppId());

        return ResponseVO.successResponse();
    }


    @Override
    public ResponseVO muteGroup(MuteGroupReq req) {
        ResponseVO<ImGroupEntity> groupResp = getGroup(req.getGroupId(), req.getAppId());
        if (!groupResp.isOk()) {
            return groupResp;
        }

        if(groupResp.getData().getStatus() == GroupStatusEnum.DESTROY.getCode()){
            throw new ApplicationException(GroupErrorCodeEnum.GROUP_IS_DESTROY);
        }

        boolean isAdmin = false;

        if (!isAdmin) {
            //不是后台调用需要检查权限
            ResponseVO<GetRoleInGroupResp> role = groupMemberService.getRoleInGroupOne(req.getGroupId(), req.getOperator(), req.getAppId());

            if (!role.isOk()) {
                return role;
            }

            GetRoleInGroupResp data = role.getData();
            Integer roleInfo = data.getRole();

            boolean isManager = roleInfo == GroupMemberRoleEnum.MANAGER.getCode() || roleInfo == GroupMemberRoleEnum.OWNER.getCode();

            //公开群只能群主修改资料
            if (!isManager) {
                throw new ApplicationException(GroupErrorCodeEnum.THIS_OPERATE_NEED_MANAGER_ROLE);
            }
        }

        ImGroupEntity update = new ImGroupEntity();
        update.setMute(req.getMute());

        UpdateWrapper<ImGroupEntity> wrapper = new UpdateWrapper<>();
        wrapper.eq(ImGroupEntity.COL_GROUP_ID,req.getGroupId());
        wrapper.eq(ImGroupEntity.COL_APP_ID,req.getAppId());
        this.groupMapper.update(update,wrapper);

        return ResponseVO.successResponse();
    }
}
