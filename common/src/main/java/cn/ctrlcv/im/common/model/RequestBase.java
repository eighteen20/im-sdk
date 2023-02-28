package cn.ctrlcv.im.common.model;

import lombok.Data;

/**
 * Class Name: RequestBase
 * Class Description: TODO
 *
 * @author liujm
 * @date 2023-02-06
 */
@Data
public class RequestBase {

    private Integer appId;

    /**
     * 操作人
     */
    private String operator;
}
