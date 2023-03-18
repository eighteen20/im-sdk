package cn.ctrlcv.im.common.model;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Class Name: ClientInfo
 * Class Description: 客户端信息
 *
 * @author liujm
 * @date 2023-03-17
 */
@Data
@NoArgsConstructor
public class ClientInfo {

    private Integer appId;

    private Integer clientType;

    private String imei;

    public ClientInfo(Integer appId, Integer clientType, String imei) {
        this.appId = appId;
        this.clientType = clientType;
        this.imei = imei;
    }

}
