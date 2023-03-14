package cn.ctrlcv.im.common.model;

import lombok.Data;

/**
 * Class Name: UserSession
 * Class Description: 登录用户的信息
 *
 * @author liujm
 * @date 2023-03-14
 */
@Data
public class UserSession {

    private String userId;

    private Integer appId;

    /**
     * 客户端类型
     */
    private Integer clientType;

    /**
     * SDK 版本号
     */
    private Integer version;

    /**
     * 连接状态
     * 1-在线
     * 2-离线
     */
    private Integer connectState;


    /**
     * 分布式环境下，记录用户登录在哪个节点上
     */
    private Integer brokerId;

    /**
     * 分布式环境下，记录用户登录在哪个节点的IP
     */
    private String brokerHost;
}
