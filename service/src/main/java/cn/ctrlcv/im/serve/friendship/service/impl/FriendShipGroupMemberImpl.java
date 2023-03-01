package cn.ctrlcv.im.serve.friendship.service.impl;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.serve.friendship.dao.ImFriendshipGroupEntity;
import cn.ctrlcv.im.serve.friendship.dao.ImFriendshipGroupMemberEntity;
import cn.ctrlcv.im.serve.friendship.dao.mapper.ImFriendshipGroupMemberMapper;
import cn.ctrlcv.im.serve.friendship.model.request.AddFriendShipGroupMemberReq;
import cn.ctrlcv.im.serve.friendship.model.request.DeleteFriendShipGroupMemberReq;
import cn.ctrlcv.im.serve.friendship.service.IFriendShipGroupMemberService;
import cn.ctrlcv.im.serve.friendship.service.IFriendshipGroupService;
import cn.ctrlcv.im.serve.user.dao.ImUserDataEntity;
import cn.ctrlcv.im.serve.user.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Name: FriendShipGroupMemberImpl
 * Class Description: 好友分组成员业务实现
 *
 * @author liujm
 * @date 2023-02-28
 */
@Service
public class FriendShipGroupMemberImpl implements IFriendShipGroupMemberService {

    private final ImFriendshipGroupMemberMapper groupMemberMapper;

    private final IFriendshipGroupService groupService;

    private final IUserService userService;

    public FriendShipGroupMemberImpl(ImFriendshipGroupMemberMapper groupMemberMapper, IFriendshipGroupService groupService, IUserService userService) {
        this.groupMemberMapper = groupMemberMapper;
        this.groupService = groupService;
        this.userService = userService;
    }

    @Override
    @Transactional(rollbackFor = ApplicationException.class)
    public ResponseVO<?> addGroupMember(AddFriendShipGroupMemberReq req) {
        ResponseVO<ImFriendshipGroupEntity> group = this.groupService.getGroup(req.getFromId(),req.getGroupName(),req.getAppId());
        if(!group.isOk()){
            return group;
        }

        List<String> successId = new ArrayList<>();
        for (String toId : req.getToIds()) {
            ResponseVO<ImUserDataEntity> singleUserInfo = this.userService.getSingleUserInfo(toId, req.getAppId());
            if(singleUserInfo.isOk()){
                int i = this.doAddGroupMember(group.getData().getGroupId(), toId);
                if(i == 1){
                    successId.add(toId);
                }
            }
        }


        return ResponseVO.successResponse(successId);
    }

    @Override
    @Transactional(rollbackFor = ApplicationException.class)
    public ResponseVO<?> delGroupMember(DeleteFriendShipGroupMemberReq req) {
        ResponseVO<ImFriendshipGroupEntity> group = this.groupService.getGroup(req.getFromId(),req.getGroupName(),req.getAppId());
        if(!group.isOk()){
            return group;
        }

        List<String> successId = new ArrayList<>();
        for (String toId : req.getToIds()) {
            ResponseVO<ImUserDataEntity> singleUserInfo = this.userService.getSingleUserInfo(toId, req.getAppId());
            if(singleUserInfo.isOk()){
                int i = deleteGroupMember(group.getData().getGroupId(), toId);
                if(i == 1){
                    successId.add(toId);
                }
            }
        }

        return ResponseVO.successResponse(successId);
    }

    private int deleteGroupMember(Long groupId, String toId) {
        QueryWrapper<ImFriendshipGroupMemberEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("group_id",groupId);
        queryWrapper.eq("to_id",toId);

        try {
            return this.groupMemberMapper.delete(queryWrapper);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }

    @Override
    @Transactional(rollbackFor = ApplicationException.class)
    public int doAddGroupMember(Long groupId, String toId) {
        ImFriendshipGroupMemberEntity imFriendShipGroupMemberEntity = new ImFriendshipGroupMemberEntity();
        imFriendShipGroupMemberEntity.setGroupId(groupId);
        imFriendShipGroupMemberEntity.setToId(toId);

        try {
            return this.groupMemberMapper.insert(imFriendShipGroupMemberEntity);
        }catch (Exception e){
            e.printStackTrace();
            return 0;
        }
    }


    @Override
    @Transactional(rollbackFor = ApplicationException.class)
    public int clearGroupMember(Long groupId) {
        QueryWrapper<ImFriendshipGroupMemberEntity> query = new QueryWrapper<>();
        query.eq("group_id",groupId);
        int delete = this.groupMemberMapper.delete(query);
        return delete;
    }
}
