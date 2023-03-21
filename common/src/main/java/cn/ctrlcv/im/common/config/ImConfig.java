package cn.ctrlcv.im.common.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Class Name: ImConfig
 * Class Description: 自定义配置
 *
 * @author liujm
 * @date 2023-03-15
 */
@Data
@Component
@ConfigurationProperties(prefix = "imconfig")
public class ImConfig {

    /**
     * 私钥
     */
    private String privateKey;

    /**
     * zk连接地址
     */
    private String zkAddr;

    /**
     * zk连接超时时间
     */
    private Integer zkConnectTimeOut;

    /**
     * 路由的负载均衡算法, 1-随机，2-轮训，3-hash
     */
    private Integer imRouteWay = 3;

    /**
     * 如果选用一致性hash的话({@link ImConfig#getImRouteWay()} = 3).
     * 配置具体hash算法。1-TreeMap, 2-自定义Map
     */
    private Integer consistentHashWay = 1;

    /**
     * 业务端回调地址
     */
    private String callbackUrl;

    /**
     * 用户资料变更之后回调开关
     */
    private boolean modifyUserAfterCallback;


    /**
     * 添加好友之后回调开关
     */
    private boolean addFriendAfterCallback;

    /**
     * 添加好友之前回调开关
     */
    private boolean addFriendBeforeCallback;

    /**
     * 修改好友之后回调开关
     */
    private boolean modifyFriendAfterCallback;

    /**
     * 删除好友之后回调开关
     */
    private boolean deleteFriendAfterCallback;

    /**
     * 添加黑名单之后回调开关
     */
    private boolean addFriendShipBlackAfterCallback;

    /**
     * 删除黑名单之后回调开关
     */
    private boolean deleteFriendShipBlackAfterCallback;

    /**
     * 创建群聊之后回调开关
     */
    private boolean createGroupAfterCallback;

    /**
     * 修改群聊之后回调开关
     */
    private boolean modifyGroupAfterCallback;

    /**
     * 解散群聊之后回调开关
     */
    private boolean destroyGroupAfterCallback;

    /**
     * 删除群成员之后回调
     */
    private boolean deleteGroupMemberAfterCallback;

    /**
     * 拉人入群之后回调
     */
    private boolean addGroupMemberAfterCallback;

    /**
     * 拉人入群之前回调
     */
    private boolean addGroupMemberBeforeCallback;

    /**
     * 发送单聊消息之后
     */
    private boolean sendMessageAfterCallback;

    /**
     * 发送单聊消息之前
     */
    private boolean sendMessageBeforeCallback;

    /**
     * 发送群聊消息之后
     */
    private boolean sendGroupMessageAfterCallback;

    /**
     * 发送群聊消息之前
     */
    private boolean sendGroupMessageBeforeCallback;


}
