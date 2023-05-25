package cn.ctrlcv.im.serve.group.dao.mapper;

import cn.ctrlcv.im.serve.group.dao.ImGroupEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.List;

public interface ImGroupMapper extends BaseMapper<ImGroupEntity> {

    /**
     * 获取群组最大的sequence
     *
     * @param appId 应用ID
     * @param joinedGroupIds 群组ID集合
     * @return 最大的sequence
     */
    Long getMaxSequence(Integer appId, List<String> joinedGroupIds);
}