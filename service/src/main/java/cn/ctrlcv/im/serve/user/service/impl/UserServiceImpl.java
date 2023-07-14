package cn.ctrlcv.im.serve.user.service.impl;

import cn.ctrlcv.im.codec.pack.user.UserModifyPack;
import cn.ctrlcv.im.common.model.ResponseVO;
import cn.ctrlcv.im.common.config.ImConfig;
import cn.ctrlcv.im.common.constant.Constants;
import cn.ctrlcv.im.common.enums.DelFlagEnum;
import cn.ctrlcv.im.common.enums.UserErrorCodeEnum;
import cn.ctrlcv.im.common.enums.command.UserEventCommand;
import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.serve.group.service.IGroupService;
import cn.ctrlcv.im.serve.user.dao.ImUserDataEntity;
import cn.ctrlcv.im.serve.user.dao.mapper.ImUserDataEntityMapper;
import cn.ctrlcv.im.serve.user.model.request.*;
import cn.ctrlcv.im.serve.user.model.response.GetUserInfoResp;
import cn.ctrlcv.im.serve.user.model.response.ImportUserResp;
import cn.ctrlcv.im.serve.user.service.IUserService;
import cn.ctrlcv.im.serve.utils.CallbackService;
import cn.ctrlcv.im.serve.utils.MessageProducer;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.beans.BeanUtils;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Class Name: UserServiceImpl
 * Class Description: 用户资料业务逻辑实现
 *
 * @author liujm
 * @date 2023-02-03
 */
@Service
public class UserServiceImpl implements IUserService {

    private final ImUserDataEntityMapper userDataEntityMapper;
    private final ImConfig imConfig;
    private final CallbackService callbackService;
    private final MessageProducer messageProducer;

    @Resource
    private RedisTemplate<String, Object> redisTemplate;
    @Resource
    private IGroupService groupService;


    public UserServiceImpl(ImUserDataEntityMapper userDataEntityMapper, ImConfig imConfig, CallbackService callbackService,
                           MessageProducer messageProducer) {
        this.userDataEntityMapper = userDataEntityMapper;
        this.imConfig = imConfig;
        this.callbackService = callbackService;
        this.messageProducer = messageProducer;
    }

    public static final int IMPORT_MAX_NUMBER_OF_USERS = 100;

    @Override
    public ResponseVO<ImportUserResp> importUser(ImportUserReq req) {

        if (req.getUserList().size() > IMPORT_MAX_NUMBER_OF_USERS) {
            throw new ApplicationException(UserErrorCodeEnum.IMPORT_SIZE_BEYOND);
        }

        List<String> successId = new ArrayList<>();
        List<String> errorId = new ArrayList<>();
        req.getUserList().forEach(e -> {
            try {
                e.setAppId(req.getAppId());
                int insert = this.userDataEntityMapper.insert(e);
                if (insert == 1) {
                    successId.add(e.getUserId());
                }
            } catch (Exception ex) {
                ex.printStackTrace();
                errorId.add(e.getUserId());
            }
        });
        ImportUserResp userResp = new ImportUserResp();
        userResp.setSuccessId(successId);
        userResp.setErrorId(errorId);
        return ResponseVO.successResponse(userResp);
    }

    @Override
    public ResponseVO<GetUserInfoResp> getUserInfo(GetUserInfoReq req) {
        QueryWrapper<ImUserDataEntity> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("app_id", req.getAppId());
        queryWrapper.in("user_id", req.getUserIds());
        queryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        List<ImUserDataEntity> userDataEntities = this.userDataEntityMapper.selectList(queryWrapper);
        HashMap<String, ImUserDataEntity> map = new HashMap<>(10);

        for (ImUserDataEntity data : userDataEntities) {
            map.put(data.getUserId(), data);
        }

        List<String> failUser = new ArrayList<>();
        for (String uid : req.getUserIds()) {
            if (!map.containsKey(uid)) {
                failUser.add(uid);
            }
        }

        GetUserInfoResp resp = new GetUserInfoResp();
        resp.setUserDataItem(userDataEntities);
        resp.setFailUser(failUser);
        return ResponseVO.successResponse(resp);
    }

    @Override
    public ResponseVO<ImUserDataEntity> getSingleUserInfo(String userId, Integer appId) {
        QueryWrapper<ImUserDataEntity> objectQueryWrapper = new QueryWrapper<>();
        objectQueryWrapper.eq("app_id", appId);
        objectQueryWrapper.eq("user_id", userId);
        objectQueryWrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());

        ImUserDataEntity imUserDataEntity = this.userDataEntityMapper.selectOne(objectQueryWrapper);
        if (imUserDataEntity == null) {
            return ResponseVO.errorResponse(UserErrorCodeEnum.USER_IS_NOT_EXIST);
        }

        return ResponseVO.successResponse(imUserDataEntity);
    }

    @Override
    public ResponseVO<?> deleteUser(DeleteUserReq req) {
        ImUserDataEntity entity = new ImUserDataEntity();
        entity.setDelFlag(DelFlagEnum.DELETE.getCode());

        List<String> errorId = new ArrayList<>();
        List<String> successId = new ArrayList<>();

        for (String userId :
                req.getUserId()) {
            QueryWrapper<ImUserDataEntity> wrapper = new QueryWrapper<>();
            wrapper.eq("app_id", req.getAppId());
            wrapper.eq("user_id", userId);
            wrapper.eq("del_flag", DelFlagEnum.NORMAL.getCode());
            int update;

            try {
                update = this.userDataEntityMapper.update(entity, wrapper);
                if (update > 0) {
                    successId.add(userId);
                } else {
                    errorId.add(userId);
                }
            } catch (Exception e) {
                errorId.add(userId);
            }
        }

        ImportUserResp resp = new ImportUserResp();
        resp.setSuccessId(successId);
        resp.setErrorId(errorId);
        return ResponseVO.successResponse(resp);
    }

    @Transactional(rollbackFor = ApplicationException.class)
    @Override
    public ResponseVO<?> modifyUserInfo(ModifyUserInfoReq req) {
        QueryWrapper<ImUserDataEntity> query = new QueryWrapper<>();
        query.eq("app_id", req.getAppId());
        query.eq("user_id", req.getUserId());
        query.eq("del_flag", DelFlagEnum.NORMAL.getCode());
        ImUserDataEntity user = this.userDataEntityMapper.selectOne(query);
        if (user == null) {
            throw new ApplicationException(UserErrorCodeEnum.USER_IS_NOT_EXIST);
        }

        ImUserDataEntity update = new ImUserDataEntity();
        BeanUtils.copyProperties(req, update);

        update.setAppId(null);
        update.setUserId(null);
        int update1 = this.userDataEntityMapper.update(update, query);
        if (update1 == 1) {
            // TCP 通知
            UserModifyPack pack = new UserModifyPack();
            BeanUtils.copyProperties(req, pack);
            messageProducer.sendToUser(req.getUserId(), req.getClientType(), req.getImei(),
                    UserEventCommand.USER_MODIFY, pack, req.getAppId());
            // 回调
            if (imConfig.isModifyUserAfterCallback()) {
                callbackService.callback(req.getAppId(), Constants.CallbackCommand.MODIFY_USER_AFTER, JSONObject.toJSONString(req));
            }
            return ResponseVO.successResponse();
        }
        throw new ApplicationException(UserErrorCodeEnum.MODIFY_USER_ERROR);
    }

    @Override
    public ResponseVO login(LoginReq req) {
        // TODO 可拓展一些具体的业务逻辑
        return ResponseVO.successResponse();
    }


    @Override
    public ResponseVO<Map<Object, Object>> getSequence(GetUserSequenceReq req) {
        Map<Object, Object> map = redisTemplate.opsForHash()
                .entries(req.getAppId() + Constants.RedisKey.SEQ_PREFIX + req.getUserId());
        Long groupSeq = groupService.getUserGroupMaxSequence(req.getAppId(), req.getUserId());
        if (groupSeq != -1L) {
            map.put(Constants.SeqConstants.GROUP, groupSeq);
        }

        return ResponseVO.successResponse(map);
    }
}
