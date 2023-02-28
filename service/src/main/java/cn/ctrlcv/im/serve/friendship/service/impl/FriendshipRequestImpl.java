package cn.ctrlcv.im.serve.friendship.service.impl;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.enums.ApproveFriendRequestStatusEnum;
import cn.ctrlcv.im.common.enums.FriendShipErrorCodeEnum;
import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.serve.friendship.dao.ImFriendshipRequestEntity;
import cn.ctrlcv.im.serve.friendship.dao.mapper.ImFriendshipRequestMapper;
import cn.ctrlcv.im.serve.friendship.model.request.ApproveFriendRequestReq;
import cn.ctrlcv.im.serve.friendship.model.request.FriendDTO;
import cn.ctrlcv.im.serve.friendship.model.request.ReadFriendShipRequestReq;
import cn.ctrlcv.im.serve.friendship.service.IFriendshipRequestService;
import cn.ctrlcv.im.serve.friendship.service.IFriendshipService;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Class Name: FriendshipRequestImpl
 * Class Description: 好友请求相关接口实现
 *
 * @author liujm
 * @date 2023-02-10
 */
@Service
public class FriendshipRequestImpl implements IFriendshipRequestService {

    private final ImFriendshipRequestMapper friendshipRequestMapper;

    private final IFriendshipService friendshipService;

    public FriendshipRequestImpl(ImFriendshipRequestMapper friendshipRequestMapper, IFriendshipService friendshipService) {
        this.friendshipRequestMapper = friendshipRequestMapper;
        this.friendshipService = friendshipService;
    }

    @Override
    public ResponseVO<?> addFriendshipRequest(String fromId, FriendDTO dto, Integer appId) {
        QueryWrapper<ImFriendshipRequestEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq(ImFriendshipRequestEntity.COL_APP_ID, appId);
        queryWrapper.eq(ImFriendshipRequestEntity.COL_FROM_ID, fromId);
        queryWrapper.eq(ImFriendshipRequestEntity.COL_TO_ID, dto.getToId());

        ImFriendshipRequestEntity friendshipRequest = this.friendshipRequestMapper.selectOne(queryWrapper);
        if (ObjectUtil.isNull(friendshipRequest)) {
            friendshipRequest.setAddSource(dto.getAddSource());
            friendshipRequest.setAddWording(dto.getAddWording());
            friendshipRequest.setAppId(appId);
            friendshipRequest.setFromId(fromId);
            friendshipRequest.setToId(dto.getToId());
            friendshipRequest.setReadStatus(0);
            friendshipRequest.setApproveStatus(0);
            friendshipRequest.setRemark(dto.getRemark());
            friendshipRequest.setCreateTime(DateUtil.current());
            friendshipRequest.setUpdateTime(friendshipRequest.getUpdateTime());
            this.friendshipRequestMapper.insert(friendshipRequest);
        } else {
            if (StrUtil.isNotBlank(dto.getAddSource())) {
                friendshipRequest.setAddSource(dto.getAddSource());
            }
            if (StrUtil.isNotBlank(dto.getAddWording())) {
                friendshipRequest.setAddWording(dto.getAddWording());
            }
            if (StrUtil.isNotBlank(dto.getRemark())) {
                friendshipRequest.setRemark(dto.getRemark());
            }
            friendshipRequest.setReadStatus(0);
            friendshipRequest.setApproveStatus(0);
            friendshipRequest.setUpdateTime(DateUtil.current());
            this.friendshipRequestMapper.updateById(friendshipRequest);
        }
        return null;
    }

    @Override
    public ResponseVO<?> approveFriendshipRequest(ApproveFriendRequestReq req) {
        ImFriendshipRequestEntity imFriendShipRequestEntity = this.friendshipRequestMapper.selectById(req.getId());
        if (ObjectUtil.isNull(imFriendShipRequestEntity)) {
            throw new ApplicationException(FriendShipErrorCodeEnum.FRIEND_REQUEST_IS_NOT_EXIST);
        }

        if (!req.getOperator().equals(imFriendShipRequestEntity.getToId())) {
            //只能审批发给自己的好友请求
            throw new ApplicationException(FriendShipErrorCodeEnum.NOT_APPROVAL_OTHER_MAN_REQUEST);
        }

        ImFriendshipRequestEntity update = new ImFriendshipRequestEntity();
        update.setApproveStatus(req.getStatus());
        update.setUpdateTime(System.currentTimeMillis());
        update.setId(req.getId());
        this.friendshipRequestMapper.updateById(update);

        if (ApproveFriendRequestStatusEnum.AGREE.getCode() == req.getStatus()) {
            //同意 ===> 去执行添加好友逻辑
            FriendDTO dto = new FriendDTO();
            dto.setAddSource(imFriendShipRequestEntity.getAddSource());
            dto.setAddWording(imFriendShipRequestEntity.getAddWording());
            dto.setRemark(imFriendShipRequestEntity.getRemark());
            dto.setToId(imFriendShipRequestEntity.getToId());
            ResponseVO responseVO = this.friendshipService.doAddFriend(imFriendShipRequestEntity.getFromId(), dto, req.getAppId());

            if (!responseVO.isOk() && responseVO.getCode() != FriendShipErrorCodeEnum.TO_IS_YOUR_FRIEND.getCode()) {
                return responseVO;
            }
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<ImFriendshipRequestEntity> getFriendRequest(String fromId, Integer appId) {
        QueryWrapper<ImFriendshipRequestEntity> query = new QueryWrapper();
        query.eq("app_id", appId);
        query.eq("to_id", fromId);

        List<ImFriendshipRequestEntity> requestList = this.friendshipRequestMapper.selectList(query);

        return ResponseVO.successResponse(requestList);
    }

    @Override
    public ResponseVO<?> readFriendShipRequestReq(ReadFriendShipRequestReq req) {
        QueryWrapper<ImFriendshipRequestEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("to_id", req.getFromId());


        ImFriendshipRequestEntity update = new ImFriendshipRequestEntity();
        update.setReadStatus(1);
        this.friendshipRequestMapper.update(update, query);


        return ResponseVO.successResponse();
    }
}
