package cn.ctrlcv.im.common.model;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Class Name: SyncReq
 * Class Description: 增量同步数据请求参数
 *
 * @author liujm
 * @date 2023-05-25
 */
@EqualsAndHashCode(callSuper = true)
@Data
public class SyncReq extends RequestBase {

    /**
     * 客户端最大seq
     */
    private Long lastSequence;

    /**
     * 一次拉取多少
     */
    private Integer maxLimit;
}
