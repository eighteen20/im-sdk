package cn.ctrlcv.im.common.constant;

/**
 * Class Name: Constants
 * Class Description: 系统常量
 *
 * @author liujm
 * @date 2023-03-14
 */
public class Constants {

    /**
     * channel绑定的userId Key
     */
    public static final String USER_ID = "userId";

    /**
     * channel绑定的appId Key
     */
    public static final String APP_ID = "appId";

    /**
     * 用户登录的设备类型
     */
    public static final String CLIENT_TYPE = "clientType";

    /**
     * 上一次心跳时间
     */
    public static final String READ_TIME = "readTime";

    /**
     * zookeeper 父节点, 前置节点
     */
    public static final String IM_CORE_ZK_ROOT = "/im-coreRoot";

    /**
     * zookeeper 父节点, 后置节点，TCP服务
     */
    public static final String IM_CORE_ZK_ROOT_TCP = "/tcp";

    /**
     * zookeeper 父节点, 后置节点，websocket服务
     */
    public static final String IM_CORE_ZK_ROOT_WS = "/ws";

    /**
     * Redis key 常量
     */
    public static class RedisConstants {
        /**
         * 用户Session。
         * 格式：APPID + USER_SESSION_CONSTANTS + userId
         */
        public static final String USER_SESSION_CONSTANTS = ":userSession:";
    }


    /**
     * RabbitMQ ChannelName de 常量
     */
    public static class RabbitConstants{

        /**
         * IM服务投递给用户服务的消息
         */
        public static final String IM_2_USER_SERVICE = "pipeline2UserService";

        /**
         * IM服务投递给消息服务的消息
         */
        public static final String IM_2_MESSAGE_SERVICE = "pipeline2MessageService";


        /**
         * IM服务投递消息给群组服务
         */
        public static final String IM_2_GROUP_SERVICE = "pipeline2GroupService";


        /**
         * IM服务投递消息给好友服务
         */
        public static final String IM_2_FRIENDSHIP_SERVICE = "pipeline2FriendshipService";


        /**
         * 消息服务给IM服务的消息
         */
        public static final String MESSAGE_SERVICE_2_IM = "messageService2Pipeline";

        /**
         * 群组服务投递给IM服务的消息
         */
        public static final String GROUP_SERVICE_2_IM = "GroupService2Pipeline";

        /**
         * 好友服务投递给IM服务的消息
         */
        public static final String FRIENDSHIP_2_IM = "friendShip2Pipeline";


    }
}
