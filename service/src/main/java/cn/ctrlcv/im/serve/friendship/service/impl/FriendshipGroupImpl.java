package cn.ctrlcv.im.serve.friendship.service.impl;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.enums.DelFlagEnum;
import cn.ctrlcv.im.common.enums.FriendShipErrorCodeEnum;
import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.serve.friendship.dao.ImFriendshipGroupEntity;
import cn.ctrlcv.im.serve.friendship.dao.mapper.ImFriendshipGroupMapper;
import cn.ctrlcv.im.serve.friendship.model.request.AddFriendShipGroupMemberReq;
import cn.ctrlcv.im.serve.friendship.model.request.AddFriendShipGroupReq;
import cn.ctrlcv.im.serve.friendship.model.request.DeleteFriendShipGroupReq;
import cn.ctrlcv.im.serve.friendship.service.IFriendShipGroupMemberService;
import cn.ctrlcv.im.serve.friendship.service.IFriendshipGroupService;
import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Class Name: FriendshipGroupImpl
 * Class Description: 好友分组业务实现
 *
 * @author liujm
 * @date 2023-02-28
 */
@Service
public class FriendshipGroupImpl implements IFriendshipGroupService {

    private final ImFriendshipGroupMapper groupMapper;
    private final IFriendShipGroupMemberService groupMemberService;

    public FriendshipGroupImpl(ImFriendshipGroupMapper groupMapper, IFriendShipGroupMemberService groupMemberService) {
        this.groupMapper = groupMapper;
        this.groupMemberService = groupMemberService;
    }

    @Override
    @Transactional(rollbackFor = ApplicationException.class)
    public ResponseVO<?> addGroup(AddFriendShipGroupReq req) {
        QueryWrapper<ImFriendshipGroupEntity> query = new QueryWrapper<>();
        query.eq("group_name", req.getGroupName());
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImFriendshipGroupEntity entity = this.groupMapper.selectOne(query);

        if (entity != null) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_SHIP_GROUP_IS_EXIST);
        }

        //写入db
        ImFriendshipGroupEntity insert = new ImFriendshipGroupEntity();
        insert.setAppId(req.getAppId());
        insert.setCreateTime(System.currentTimeMillis());
        insert.setDelFlag(DelFlagEnum.NORMAL.getCode());
        insert.setGroupName(req.getGroupName());
        insert.setFromId(req.getFromId());
        try {
            int insert1 = this.groupMapper.insert(insert);

            if (insert1 != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_SHIP_GROUP_CREATE_ERROR);
            }
            if (CollectionUtil.isNotEmpty(req.getToIds())) {
                AddFriendShipGroupMemberReq addFriendShipGroupMemberReq = new AddFriendShipGroupMemberReq();
                addFriendShipGroupMemberReq.setFromId(req.getFromId());
                addFriendShipGroupMemberReq.setGroupName(req.getGroupName());
                addFriendShipGroupMemberReq.setToIds(req.getToIds());
                addFriendShipGroupMemberReq.setAppId(req.getAppId());
                this.groupMemberService.addGroupMember(addFriendShipGroupMemberReq);
                return ResponseVO.successResponse();
            }
        } catch (DuplicateKeyException e) {
            e.getStackTrace();
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_SHIP_GROUP_IS_EXIST);
        }

        return ResponseVO.successResponse();
    }

    @Override
    @Transactional(rollbackFor = ApplicationException.class)
    public ResponseVO<?> deleteGroup(DeleteFriendShipGroupReq req) {
        for (String groupName : req.getGroupName()) {
            QueryWrapper<ImFriendshipGroupEntity> query = new QueryWrapper<>();
            query.eq("group_name", groupName);
            query.eq("app_id", req.getAppId());
            query.eq("from_id", req.getFromId());
            query.eq("del_flag", DelFlagEnum.NORMAL.getCode());

            ImFriendshipGroupEntity entity = this.groupMapper.selectOne(query);

            if (entity != null) {
                ImFriendshipGroupEntity update = new ImFriendshipGroupEntity();
                update.setGroupId(entity.getGroupId());
                update.setDelFlag(DelFlagEnum.DELETE.getCode());
                this.groupMapper.updateById(update);
                this.groupMemberService.clearGroupMember(entity.getGroupId());

            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<ImFriendshipGroupEntity> getGroup(String fromId, String groupName, Integer appId) {
        QueryWrapper<ImFriendshipGroupEntity> query = new QueryWrapper<>();
        query.eq("group_name", groupName);
        query.eq("app_id", appId);
        query.eq("from_id", fromId);
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImFriendshipGroupEntity entity = this.groupMapper.selectOne(query);
        if (entity == null) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_SHIP_GROUP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(entity);
    }
}
