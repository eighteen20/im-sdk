package cn.ctrlcv.im.messagestore.dao.mapper;

import cn.ctrlcv.im.messagestore.dao.ImMessageHistoryEntity;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;

import java.util.Collection;

/**
 * @author ljm19
 */
public interface ImMessageHistoryMapper extends BaseMapper<ImMessageHistoryEntity> {

    /**
     * 批量插入【mysql】
     *
     * @param list Collection<ImMessageHistoryEntity>
     * @return int 影响行数
     */
    Integer insertBatchSomeColumn(Collection<ImMessageHistoryEntity> list);

}