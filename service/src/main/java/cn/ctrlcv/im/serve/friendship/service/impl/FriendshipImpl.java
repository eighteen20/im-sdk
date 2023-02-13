package cn.ctrlcv.im.serve.friendship.service.impl;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.enums.FriendShipErrorCodeEnum;
import cn.ctrlcv.im.common.enums.FriendShipStatusEnum;
import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.serve.friendship.dao.ImFriendshipEntity;
import cn.ctrlcv.im.serve.friendship.dao.mapper.ImFriendshipMapper;
import cn.ctrlcv.im.serve.friendship.model.request.*;
import cn.ctrlcv.im.serve.friendship.model.response.ImportFriendShipResp;
import cn.ctrlcv.im.serve.friendship.service.IFriendshipService;
import cn.ctrlcv.im.serve.user.dao.ImUserDataEntity;
import cn.ctrlcv.im.serve.user.service.IUserService;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

/**
 * Class Name: FriendshipImpl
 * Class Description: 好友关系业务关系实现
 *
 * @author liujm
 * @date 2023-02-08
 */
@Service
public class FriendshipImpl implements IFriendshipService {

    private final ImFriendshipMapper friendshipMapper;
    private final IUserService userService;

    public FriendshipImpl(ImFriendshipMapper friendshipMapper, IUserService userService) {
        this.friendshipMapper = friendshipMapper;
        this.userService = userService;
    }

    public static final int IMPORT_MAX_NUMBER_OF_FRIENDSHIP = 100;

    @Override
    public ResponseVO<ImportFriendShipResp> importFriendship(ImportFriendshipReq req) {
        if (req.getFriendItem().size() > IMPORT_MAX_NUMBER_OF_FRIENDSHIP) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.IMPORT_SIZE_BEYOND);
        }
        ImportFriendShipResp resp = new ImportFriendShipResp();
        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();

        for (ImportFriendshipReq.ImportFriendDTO dto : req.getFriendItem()) {
            ImFriendshipEntity entity = new ImFriendshipEntity();
            BeanUtils.copyProperties(dto, entity);
            entity.setAppId(req.getAppId());
            entity.setFromId(req.getFromId());
            try {
                int insert = this.friendshipMapper.insert(entity);
                if (insert == 1) {
                    successId.add(dto.getToId());
                } else {
                    errorId.add(dto.getToId());
                }
            } catch (Exception e) {
                e.printStackTrace();
                errorId.add(dto.getToId());
            }

        }

        resp.setErrorId(errorId);
        resp.setSuccessId(successId);

        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO addFriend(AddFriendshipReq req) {
        ResponseVO<ImUserDataEntity> fromInfo = this.userService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }

        ResponseVO<ImUserDataEntity> toInfo = this.userService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return fromInfo;
        }

        ImUserDataEntity data = toInfo.getData();

        return this.doAddFriend(req.getFromId(), req.getToItem(), req.getAppId());

    }

    @Transactional(rollbackFor = ApplicationException.class)
    @Override
    public ResponseVO doAddFriend(String fromId, FriendDTO dto, Integer appId) {
        //A 添加 B
        //Friend表插入A 和 B 两条记录
        //查询是否有记录存在，如果存在则判断状态，如果是已添加，则提示已添加，如果是未添加，则修改状态

        QueryWrapper<ImFriendshipEntity> query = new QueryWrapper<>();
        query.eq("app_id", appId);
        query.eq("from_id", fromId);
        query.eq("to_id", dto.getToId());
        ImFriendshipEntity fromItem = this.friendshipMapper.selectOne(query);

        if (fromItem == null) {
            //走添加逻辑。
            fromItem = new ImFriendshipEntity();
            fromItem.setAppId(appId);
            fromItem.setFromId(fromId);
            BeanUtils.copyProperties(dto, fromItem);
            fromItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            fromItem.setCreateTime(System.currentTimeMillis());
            int insert = this.friendshipMapper.insert(fromItem);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.ADD_FRIEND_ERROR);
            }
        } else {
            //如果存在则判断状态，如果是已添加，则提示已添加，如果是未添加，则修改状态

            if (fromItem.getStatus() == FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.TO_IS_YOUR_FRIEND);
            } else {
                ImFriendshipEntity update = new ImFriendshipEntity();

                if (StringUtils.isNotBlank(dto.getAddSource())) {
                    update.setAddSource(dto.getAddSource());
                }

                if (StringUtils.isNotBlank(dto.getRemark())) {
                    update.setRemake(dto.getRemark());
                }

                if (StringUtils.isNotBlank(dto.getExtra())) {
                    update.setExtra(dto.getExtra());
                }
                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

                int result = this.friendshipMapper.update(update, query);
                if (result != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCodeEnum.ADD_FRIEND_ERROR);
                }
            }

        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> updateFriend(UpdateFriendshipReq req) {
        ResponseVO<ImUserDataEntity> fromInfo = this.userService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }

        ResponseVO<ImUserDataEntity> toInfo = this.userService.getSingleUserInfo(req.getToItem().getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return fromInfo;
        }

        return this.doUpdateFriend(req.getFromId(), req.getToItem(), req.getAppId());
    }

    /**
     * 更新好友操作（数据库操作）
     *
     * @param fromId 发起人
     * @param toItem {@link FriendDTO}
     * @param appId  应用ID
     * @return 无
     */
    private ResponseVO<?> doUpdateFriend(String fromId, FriendDTO toItem, Integer appId) {
        UpdateWrapper<ImFriendshipEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(ImFriendshipEntity::getAddSource, toItem.getAddSource())
                .set(ImFriendshipEntity::getExtra, toItem.getExtra())
                .set(ImFriendshipEntity::getRemake, toItem.getRemark())
                .eq(ImFriendshipEntity::getAppId, appId)
                .eq(ImFriendshipEntity::getToId, toItem.getToId())
                .eq(ImFriendshipEntity::getFromId, fromId);

        int update = this.friendshipMapper.update(null, updateWrapper);
        if(update == 1){
            return ResponseVO.successResponse();
        }
        return ResponseVO.errorResponse();
    }

    @Override
    public ResponseVO<?> deleteFriend(DeleteFriendshipReq req) {

        QueryWrapper<ImFriendshipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("to_id", req.getToId());
        ImFriendshipEntity fromItem = this.friendshipMapper.selectOne(query);

        if (fromItem == null) {
            // 返回不是好友
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.TO_IS_NOT_YOUR_FRIEND);
        } else {
            if (fromItem.getStatus() == FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
                // 是好友关系，执行删除
                ImFriendshipEntity friendship = new ImFriendshipEntity();
                friendship.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
                this.friendshipMapper.update(friendship, query);
            } else {
                // 返回已被删除
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_IS_DELETED);
            }
        }
        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<String> deleteAllFriend(DeleteAllFriendshipReq req) {
        QueryWrapper<ImFriendshipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("status", FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

        ImFriendshipEntity friendship = new ImFriendshipEntity();
        friendship.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
        int update = this.friendshipMapper.update(friendship, query);

        return ResponseVO.successResponse("成功删除" + update + "条好友");
    }
}
