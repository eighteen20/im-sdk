package cn.ctrlcv.im.common.model;

import lombok.Data;

import java.util.List;

/**
 * Class Name: SyncResp
 * Class Description: 增量同步数据响应参数
 *
 * @author liujm
 * @date 2023-05-25
 */
@Data
public class SyncResp<T> {

        /**
        * 最大seq
        */
        private Long maxSequence;

        /**
        * 是否完成
        */
        private boolean isCompleted;

        /**
        * 数据列表
        */
        private List<T> dataList;
}
