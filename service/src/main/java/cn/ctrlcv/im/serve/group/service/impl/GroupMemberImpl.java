package cn.ctrlcv.im.serve.group.service.impl;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.enums.GroupErrorCodeEnum;
import cn.ctrlcv.im.common.enums.GroupMemberRoleEnum;
import cn.ctrlcv.im.serve.group.dao.ImGroupEntity;
import cn.ctrlcv.im.serve.group.dao.ImGroupMemberEntity;
import cn.ctrlcv.im.serve.group.dao.mapper.ImGroupMemberMapper;
import cn.ctrlcv.im.serve.group.model.dto.GroupMemberDTO;
import cn.ctrlcv.im.serve.group.model.request.ImportGroupMemberReq;
import cn.ctrlcv.im.serve.group.model.resp.GetRoleInGroupResp;
import cn.ctrlcv.im.serve.group.model.resp.ImportGroupMemberResp;
import cn.ctrlcv.im.serve.group.service.IGroupMemberService;
import cn.ctrlcv.im.serve.group.service.IGroupService;
import cn.ctrlcv.im.serve.user.dao.ImUserDataEntity;
import cn.ctrlcv.im.serve.user.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Name: GroupMemberImpl
 * Class Description: 群成员操作实现
 *
 * @author liujm
 * @date 2023-03-02
 */
@Service
public class GroupMemberImpl implements IGroupMemberService {

    private final ImGroupMemberMapper groupMemberMapper;
    private final IGroupService groupService;
    private final IUserService userService;
    private final IGroupMemberService groupMemberService;

    public GroupMemberImpl(ImGroupMemberMapper groupMemberMapper, IGroupService groupService, IUserService userService,
                           IGroupMemberService groupMemberService) {
        this.groupMemberMapper = groupMemberMapper;
        this.groupService = groupService;
        this.userService = userService;
        this.groupMemberService = groupMemberService;
    }

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
        if(!singleUserInfo.isOk()){
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
}
