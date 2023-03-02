package cn.ctrlcv.im.serve.group.service.impl;

import cn.ctrlcv.im.common.ResponseVO;
import cn.ctrlcv.im.common.enums.GroupErrorCodeEnum;
import cn.ctrlcv.im.common.enums.GroupStatusEnum;
import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.serve.group.dao.ImGroupEntity;
import cn.ctrlcv.im.serve.group.dao.mapper.ImGroupMapper;
import cn.ctrlcv.im.serve.group.model.request.ImportGroupReq;
import cn.ctrlcv.im.serve.group.service.IGroupService;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;

/**
 * Class Name: GroupImpl
 * Class Description: 群组操作实现
 *
 * @author liujm
 * @date 2023-03-02
 */
@Service
public class GroupImpl implements IGroupService {
    private final ImGroupMapper groupMapper;

    public GroupImpl(ImGroupMapper groupMapper) {
        this.groupMapper = groupMapper;
    }

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
}
