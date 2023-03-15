package cn.ctrlcv.im.common.model;

import lombok.Data;

/**
 * Class Name: UserClientDTO
 * Class Description: 标识channel的key
 *
 * @author liujm
 * @date 2023-03-14
 */
@Data
public class UserClientDTO {

    private Integer appId;

    private Integer clientType;

    private String userId;

    private String imei;
}
