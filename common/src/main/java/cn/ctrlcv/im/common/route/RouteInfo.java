package cn.ctrlcv.im.common.route;

import lombok.Data;

/**
 * Class Name: RouteInfo
 * Class Description: 服务节点信息
 *
 * @author liujm
 * @date 2023-03-15
 */
@Data
public class RouteInfo {

    private String ip;
    private Integer port;

    public RouteInfo(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }
}
