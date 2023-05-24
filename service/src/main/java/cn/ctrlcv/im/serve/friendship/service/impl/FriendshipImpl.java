package cn.ctrlcv.im.serve.friendship.service.impl;

import cn.ctrlcv.im.codec.pack.friendship.*;
import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.config.ImConfig;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.AllowFriendTypeEnum;
import cn.ctrlcv.im.common.enums.CheckFriendShipTypeEnum;
import cn.ctrlcv.im.common.enums.FriendShipErrorCodeEnum;
import cn.ctrlcv.im.common.enums.FriendShipStatusEnum;
import cn.ctrlcv.im.common.enums.command.FriendshipEventCommand;
import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.common.model.RequestBase;
import cn.ctrlcv.im.serve.friendship.dao.ImFriendshipEntity;
import cn.ctrlcv.im.serve.friendship.dao.mapper.ImFriendshipMapper;
import cn.ctrlcv.im.serve.friendship.model.callback.AddFriendAfterCallbackDTO;
import cn.ctrlcv.im.serve.friendship.model.callback.AddFriendBlackAfterCallbackDTO;
import cn.ctrlcv.im.serve.friendship.model.callback.DeleteFriendAfterCallbackDTO;
import cn.ctrlcv.im.serve.friendship.model.request.*;
import cn.ctrlcv.im.serve.friendship.model.response.CheckFriendShipResp;
import cn.ctrlcv.im.serve.friendship.model.response.ImportFriendShipResp;
import cn.ctrlcv.im.serve.friendship.service.IFriendshipRequestService;
import cn.ctrlcv.im.serve.friendship.service.IFriendshipService;
import cn.ctrlcv.im.serve.sequence.RedisSeq;
import cn.ctrlcv.im.serve.user.dao.ImUserDataEntity;
import cn.ctrlcv.im.serve.user.service.IUserService;
import cn.ctrlcv.im.serve.utils.CallbackService;
import cn.ctrlcv.im.serve.utils.MessageProducer;
import cn.ctrlcv.im.serve.utils.WriteUserSeq;
import cn.hutool.core.util.ObjectUtil;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Class Name: FriendshipImpl
 * Class Description: 好友关系业务关系实现
 *
 * @author liujm
 * @date 2023-02-08
 */
@Service
public class FriendshipImpl implements IFriendshipService {

    @Resource
    private ImFriendshipMapper friendshipMapper;

    @Resource
    private IUserService userService;

    @Resource
    private IFriendshipRequestService friendshipRequestService;

    @Resource
    private ImConfig imConfig;

    @Resource
    private CallbackService callbackService;

    @Resource
    private MessageProducer messageProducer;

    @Resource
    private RedisSeq redisSeq;

    @Resource
    private WriteUserSeq writeUserSeq;


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

        // 前置回调
        if (imConfig.isAddFriendBeforeCallback()) {
            ResponseVO responseVO = callbackService.beforeCallback(req.getAppId(), Constants.CallbackCommand.ADD_FRIEND_BEFORE, JSONObject.toJSONString(req));
            if (!responseVO.isOk()) {
                return responseVO;
            }
        }


        ImUserDataEntity data = toInfo.getData();
        if (ObjectUtil.isNotNull(data.getFriendAllowType()) && data.getFriendAllowType() == AllowFriendTypeEnum.NOT_NEED.getCode()) {
            return this.doAddFriend(req, req.getFromId(), req.getToItem(), req.getAppId());
        } else {
            QueryWrapper<ImFriendshipEntity> query = new QueryWrapper<>();
            query.eq("app_id", req.getAppId());
            query.eq("from_id", req.getFromId());
            query.eq("to_id", req.getToItem().getToId());
            ImFriendshipEntity fromItem = friendshipMapper.selectOne(query);
            if (fromItem == null || fromItem.getStatus()
                    != FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode()) {
                // 好友申请流程
                ResponseVO<?> vo = this.friendshipRequestService.addFriendshipRequest(req.getFromId(), req.getToItem(), req.getAppId());
                if (!vo.isOk()) {
                    return vo;
                }
            } else {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.TO_IS_YOUR_FRIEND);
            }

        }

        return ResponseVO.successResponse();

    }

    @Transactional(rollbackFor = ApplicationException.class)
    @Override
    public ResponseVO doAddFriend(RequestBase requestBase, String fromId, FriendDTO dto, Integer appId) {
        //A 添加 B
        //Friend表插入A 和 B 两条记录
        //查询是否有记录存在，如果存在则判断状态，如果是已添加，则提示已添加，如果是未添加，则修改状态

        QueryWrapper<ImFriendshipEntity> query = new QueryWrapper<>();
        query.eq("app_id", appId);
        query.eq("from_id", fromId);
        query.eq("to_id", dto.getToId());
        ImFriendshipEntity fromItem = this.friendshipMapper.selectOne(query);

        long seq = 0L;
        if (fromItem == null) {
            //走添加逻辑。
            fromItem = new ImFriendshipEntity();
            fromItem.setAppId(appId);
            fromItem.setFromId(fromId);
            seq = redisSeq.nextSeq(appId + Constants.SeqConstants.FRIENDSHIP);
            fromItem.setFriendSequence(seq);
            BeanUtils.copyProperties(dto, fromItem);
            fromItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            fromItem.setCreateTime(System.currentTimeMillis());
            int insert = this.friendshipMapper.insert(fromItem);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.ADD_FRIEND_ERROR);
            }
            writeUserSeq.writeUserSeq(appId, fromId, Constants.SeqConstants.FRIENDSHIP, seq);
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
                seq = redisSeq.nextSeq(appId + Constants.SeqConstants.FRIENDSHIP);
                update.setFriendSequence(seq);
                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());

                int result = this.friendshipMapper.update(update, query);
                if (result != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCodeEnum.ADD_FRIEND_ERROR);
                }
                writeUserSeq.writeUserSeq(appId, fromId, Constants.SeqConstants.FRIENDSHIP, seq);
            }

        }

        QueryWrapper<ImFriendshipEntity> toQuery = new QueryWrapper<>();
        toQuery.eq("app_id", appId);
        toQuery.eq("from_id", dto.getToId());
        toQuery.eq("to_id", fromId);
        ImFriendshipEntity toItem = this.friendshipMapper.selectOne(toQuery);
        // B 添加 A
        if (ObjectUtil.isNull(toItem)) {
            toItem = new ImFriendshipEntity();
            toItem.setAppId(appId);
            BeanUtils.copyProperties(dto, toItem);
            toItem.setFromId(dto.getToId());
            toItem.setToId(fromId);
            toItem.setFriendSequence(seq);
            toItem.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
            toItem.setCreateTime(System.currentTimeMillis());
            int insert = this.friendshipMapper.insert(toItem);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.ADD_FRIEND_ERROR);
            }
            writeUserSeq.writeUserSeq(appId, dto.getToId(), Constants.SeqConstants.FRIENDSHIP, seq);
        } else {
            if (FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode() !=
                    toItem.getStatus()) {
                ImFriendshipEntity update = new ImFriendshipEntity();
                update.setFriendSequence(seq);
                update.setStatus(FriendShipStatusEnum.FRIEND_STATUS_NORMAL.getCode());
                friendshipMapper.update(update, toQuery);
                writeUserSeq.writeUserSeq(appId, dto.getToId(), Constants.SeqConstants.FRIENDSHIP, seq);
            }
        }

        // 添加好友后，需要更新好友列表
        AddFriendPack pack = new AddFriendPack();
        BeanUtils.copyProperties(fromItem, pack);
        pack.setSequence(fromItem.getFriendSequence());
        // 发送给A，发起方
        if (requestBase != null) {
            messageProducer.sendToUser(fromId, requestBase.getClientType(), requestBase.getImei(),
                    FriendshipEventCommand.FRIENDSHIP_ADD, pack, appId);
        } else {
            messageProducer.sendToUser(fromId, FriendshipEventCommand.FRIENDSHIP_ADD, pack, appId);
        }
        // 发送给B，接收方
        AddFriendPack packToUser = new AddFriendPack();
        BeanUtils.copyProperties(toItem, packToUser);
        messageProducer.sendToUser(toItem.getFromId(), FriendshipEventCommand.FRIENDSHIP_ADD, pack, appId);


        // 后置回调
        if (imConfig.isAddFriendAfterCallback()) {
            AddFriendAfterCallbackDTO callbackDTO = new AddFriendAfterCallbackDTO();
            callbackDTO.setFromId(fromId);
            callbackDTO.setToItem(dto);
            callbackService.callback(appId, Constants.CallbackCommand.ADD_FRIEND_AFTER, JSONObject.toJSONString(callbackDTO));

        }

        return ResponseVO.successResponse();
    }

    @Transactional(rollbackFor = ApplicationException.class)
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

        ResponseVO<?> responseVO = this.doUpdateFriend(req.getFromId(), req.getToItem(), req.getAppId());
        if (!responseVO.isOk()) {
            return responseVO;
        }

        // 通知
        UpdateFriendPack pack = new UpdateFriendPack();
        pack.setToId(req.getToItem().getToId());
        pack.setRemark(req.getToItem().getRemark());
        messageProducer.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                FriendshipEventCommand.FRIENDSHIP_UPDATE, pack, req.getAppId());


        // 回调
        if (imConfig.isModifyFriendAfterCallback()) {
            AddFriendAfterCallbackDTO callbackDTO = new AddFriendAfterCallbackDTO();
            callbackDTO.setFromId(req.getFromId());
            callbackDTO.setToItem(req.getToItem());
            callbackService.callback(req.getAppId(), Constants.CallbackCommand.UPDATE_FRIEND_AFTER, JSONObject.toJSONString(callbackDTO));
        }

        return ResponseVO.successResponse();
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
        Long seq = redisSeq.nextSeq(appId + Constants.SeqConstants.FRIENDSHIP);
        UpdateWrapper<ImFriendshipEntity> updateWrapper = new UpdateWrapper<>();
        updateWrapper.lambda().set(ImFriendshipEntity::getAddSource, toItem.getAddSource())
                .set(ImFriendshipEntity::getExtra, toItem.getExtra())
                .set(ImFriendshipEntity::getRemake, toItem.getRemark())
                .set(ImFriendshipEntity::getFriendSequence, seq)
                .eq(ImFriendshipEntity::getAppId, appId)
                .eq(ImFriendshipEntity::getToId, toItem.getToId())
                .eq(ImFriendshipEntity::getFromId, fromId);

        int update = this.friendshipMapper.update(null, updateWrapper);

        if (update == 1) {
            writeUserSeq.writeUserSeq(appId, fromId, Constants.SeqConstants.FRIENDSHIP, seq);
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
                Long seq = redisSeq.nextSeq(req.getAppId() + Constants.SeqConstants.FRIENDSHIP);
                friendship.setFriendSequence(seq);
                friendship.setStatus(FriendShipStatusEnum.FRIEND_STATUS_DELETE.getCode());
                this.friendshipMapper.update(friendship, query);

                writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.FRIENDSHIP, seq);

                // 通知
                DeleteFriendPack pack = new DeleteFriendPack();
                pack.setToId(req.getToId());
                pack.setFromId(req.getFromId());
                pack.setSequence(seq);
                messageProducer.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                        FriendshipEventCommand.FRIENDSHIP_DELETE, pack, req.getAppId());

                if (imConfig.isDeleteFriendAfterCallback()) {
                    DeleteFriendAfterCallbackDTO callbackDTO = new DeleteFriendAfterCallbackDTO();
                    callbackDTO.setFromId(req.getFromId());
                    callbackDTO.setToId(req.getToId());
                    callbackService.callback(req.getAppId(), Constants.CallbackCommand.DELETE_FRIEND_AFTER, JSONObject.toJSONString(callbackDTO));
                }
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

        // 通知
        DeleteAllFriendPack pack = new DeleteAllFriendPack();
        pack.setFromId(req.getFromId());
        messageProducer.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                FriendshipEventCommand.FRIENDSHIP_ALL_DELETE, pack, req.getAppId());

        return ResponseVO.successResponse("成功删除" + update + "条好友");
    }

    @Override
    public ResponseVO<List<ImFriendshipEntity>> getAllFriendShip(GetAllFriendShipReq req) {
        QueryWrapper<ImFriendshipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        return ResponseVO.successResponse(this.friendshipMapper.selectList(query));
    }

    @Override
    public ResponseVO<ImFriendshipEntity> getFriendShip(GetFriendShipReq req) {
        QueryWrapper<ImFriendshipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("to_id", req.getToId());

        ImFriendshipEntity entity = this.friendshipMapper.selectOne(query);
        if (entity == null) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.RELATIONSHIP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(entity);
    }

    @Override
    public ResponseVO<CheckFriendShipResp> checkFriendship(CheckFriendShipReq req) {

        Map<String, Integer> map = req.getToIds().stream().collect(Collectors.toMap(Function.identity(), s -> 0));

        List<CheckFriendShipResp> respList = new ArrayList<>(10);

        if (CheckFriendShipTypeEnum.SINGLE.getType() == req.getCheckType()) {
            // 检验对方是不是在好友列表中
            respList = this.friendshipMapper.getSingleFriendship(req);
        } else {
            respList = this.friendshipMapper.getBothFriendship(req);
        }

        Map<String, Integer> collect = respList.stream().collect(Collectors.toMap(CheckFriendShipResp::getToId, CheckFriendShipResp::getStatus));
        for (String toId : map.keySet()) {
            if (!collect.containsKey(toId)) {
                CheckFriendShipResp resp = new CheckFriendShipResp();
                resp.setFromId(req.getFromId());
                resp.setToId(toId);
                resp.setStatus(map.get(toId));
                respList.add(resp);
            }

        }
        return ResponseVO.successResponse(respList);
    }

    @Override
    public ResponseVO<?> addBlack(AddBlackReq req) {
        ResponseVO<ImUserDataEntity> fromInfo = this.userService.getSingleUserInfo(req.getFromId(), req.getAppId());
        if (!fromInfo.isOk()) {
            return fromInfo;
        }

        ResponseVO<ImUserDataEntity> toInfo = this.userService.getSingleUserInfo(req.getToId(), req.getAppId());
        if (!toInfo.isOk()) {
            return fromInfo;
        }

        QueryWrapper<ImFriendshipEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("from_id", req.getFromId());
        query.eq("to_id", req.getToId());

        ImFriendshipEntity fromItem = this.friendshipMapper.selectOne(query);

        long seq = 0L;
        if (fromItem == null) {
            // 走添加逻辑。
            fromItem = new ImFriendshipEntity();
            fromItem.setFromId(req.getFromId());
            fromItem.setToId(req.getToId());
            seq = redisSeq.nextSeq(req.getAppId() + Constants.SeqConstants.FRIENDSHIP);
            fromItem.setFriendSequence(seq);
            fromItem.setAppId(req.getAppId());
            fromItem.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
            fromItem.setCreateTime(System.currentTimeMillis());
            int insert = this.friendshipMapper.insert(fromItem);
            if (insert != 1) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.ADD_FRIEND_ERROR);
            }
            writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.FRIENDSHIP, seq);
        } else {
            //如果存在则判断状态，如果是拉黑，则提示已拉黑，如果是未拉黑，则修改状态
            if (fromItem.getBlack() != null && fromItem.getBlack() == FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode()) {
                return ResponseVO.errorResponse(FriendShipErrorCodeEnum.FRIEND_IS_BLACK);
            } else {
                seq = redisSeq.nextSeq(req.getAppId() + Constants.SeqConstants.FRIENDSHIP);
                ImFriendshipEntity update = new ImFriendshipEntity();
                update.setFriendSequence(seq);
                update.setBlack(FriendShipStatusEnum.BLACK_STATUS_BLACKED.getCode());
                int result = this.friendshipMapper.update(update, query);
                if (result != 1) {
                    return ResponseVO.errorResponse(FriendShipErrorCodeEnum.ADD_BLACK_ERROR);
                }
                writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.FRIENDSHIP, seq);
            }
        }

        // 通知
        AddFriendBlackPack pack = new AddFriendBlackPack();
        pack.setFromId(req.getFromId());
        pack.setToId(req.getToId());
        pack.setSequence(seq);
        messageProducer.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                FriendshipEventCommand.FRIENDSHIP_BLACK_ADD, pack, req.getAppId());

        if (imConfig.isAddFriendShipBlackAfterCallback()) {
            AddFriendBlackAfterCallbackDTO callbackDTO = new AddFriendBlackAfterCallbackDTO();
            callbackDTO.setFromId(req.getFromId());
            callbackDTO.setToId(req.getToId());
            callbackService.callback(req.getAppId(), Constants.CallbackCommand.ADD_BLACK_AFTER, JSONObject.toJSONString(callbackDTO));
        }

        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<?> deleteBlack(DeleteBlackReq req) {
        QueryWrapper<ImFriendshipEntity> queryFrom = new QueryWrapper<>();
        queryFrom.eq("from_id", req.getFromId());
        queryFrom.eq("app_id", req.getAppId());
        queryFrom.eq("to_id", req.getToId());
        ImFriendshipEntity fromItem = this.friendshipMapper.selectOne(queryFrom);
        if (fromItem.getBlack() != null && fromItem.getBlack() == FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode()) {
            throw new ApplicationException(FriendShipErrorCodeEnum.FRIEND_IS_NOT_YOUR_BLACK);
        }
        Long seq = redisSeq.nextSeq(req.getAppId() + Constants.SeqConstants.FRIENDSHIP);
        ImFriendshipEntity entity = new ImFriendshipEntity();
        entity.setFriendSequence(seq);
        entity.setBlack(FriendShipStatusEnum.BLACK_STATUS_NORMAL.getCode());
        int update = this.friendshipMapper.update(entity, queryFrom);

        if (update == 1) {
            writeUserSeq.writeUserSeq(req.getAppId(), req.getFromId(), Constants.SeqConstants.FRIENDSHIP, seq);
            // 通知
            DeleteFriendBlackPack pack = new DeleteFriendBlackPack();
            pack.setFromId(req.getFromId());
            pack.setToId(req.getToId());
            pack.setSequence(seq);
            messageProducer.sendToUser(req.getFromId(), req.getClientType(), req.getImei(),
                    FriendshipEventCommand.FRIENDSHIP_BLACK_DELETE, pack, req.getAppId());

            if (imConfig.isDeleteFriendShipBlackAfterCallback()) {
                AddFriendBlackAfterCallbackDTO callbackDTO = new AddFriendBlackAfterCallbackDTO();
                callbackDTO.setFromId(req.getFromId());
                callbackDTO.setToId(req.getToId());
                callbackService.callback(req.getAppId(), Constants.CallbackCommand.DELETE_BLACK, JSONObject.toJSONString(callbackDTO));
            }
        }


        return ResponseVO.successResponse();
    }

    @Override
    public ResponseVO<CheckFriendShipResp> checkBlack(CheckFriendShipReq req) {
        Map<String, Integer> toIdMap
                = req.getToIds().stream().collect(Collectors
                .toMap(Function.identity(), s -> 0));
        List<CheckFriendShipResp> result;

        if (req.getCheckType() == CheckFriendShipTypeEnum.SINGLE.getType()) {
            result = this.friendshipMapper.getSingleFriendShipBlack(req);
        } else {
            result = this.friendshipMapper.getBothFriendShipBlack(req);
        }

        Map<String, Integer> collect = result.stream()
                .collect(Collectors
                        .toMap(CheckFriendShipResp::getToId,
                                CheckFriendShipResp::getStatus));
        for (String toId :
                toIdMap.keySet()) {
            if (!collect.containsKey(toId)) {
                CheckFriendShipResp checkFriendShipResp = new CheckFriendShipResp();
                checkFriendShipResp.setToId(toId);
                checkFriendShipResp.setFromId(req.getFromId());
                checkFriendShipResp.setStatus(toIdMap.get(toId));
                result.add(checkFriendShipResp);
            }
        }

        return ResponseVO.successResponse(result);
    }

    @Override
    public ResponseVO<ImFriendshipEntity> getRelation(GetRelationReq req) {
        return getImFriendshipEntityResponseVO(req.getAppId(), req.getFromId(), req.getToId(), req);
    }

    private ResponseVO<ImFriendshipEntity> getImFriendshipEntityResponseVO(Integer appId, String fromId, String toId, GetRelationReq req) {
        QueryWrapper<ImFriendshipEntity> query = new QueryWrapper<>();
        query.eq("app_id", appId);
        query.eq("from_id", fromId);
        query.eq("to_id", toId);

        ImFriendshipEntity entity = this.friendshipMapper.selectOne(query);
        if (entity == null) {
            return ResponseVO.errorResponse(FriendShipErrorCodeEnum.RELATIONSHIP_IS_NOT_EXIST);
        }
        return ResponseVO.successResponse(entity);
    }
}
