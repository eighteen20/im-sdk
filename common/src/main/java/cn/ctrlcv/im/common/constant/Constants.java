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
     * 设备IMEI号
     */
    public static final String IMEI = "imei";

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

        /**
         * 用户上线通知channel
         */
        public static final String USER_LOGIN_CHANNEL = "signal/channel/LOGIN_USER_INNER_QUEUE";


        /**
         * userSign，格式：appId:userSign:
         */
        public static final String USER_SIGN = "userSign";
    }


    /**
     * RabbitMQ ChannelName de 常量
     */
    public static class RabbitConstants {

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


    /**
     * 回调指令
     */
    public static class CallbackCommand {
        /**
         * 修改用户资料后
         */
        public static final String MODIFY_USER_AFTER = "modify.user.after";

        /**
         * 创建群组后
         */
        public static final String CREATE_GROUP_AFTER = "group.create.after";

        /**
         * 修改群组后
         */
        public static final String UPDATE_GROUP_AFTER = "group.update.after";

        /**
         * 解散群组后
         */
        public static final String DESTROY_GROUP_AFTER = "group.destroy.after";

        /**
         * 群主转移后
         */
        public static final String TRANSFER_GROUP_AFTER = "group.transfer.after";

        /**
         * 添加群成员前
         */
        public static final String GROUP_MEMBER_ADD_BEFORE = "group.member.add.before";

        /**
         * 添加群成员后
         */
        public static final String GROUP_MEMBER_ADD_AFTER = "group.member.add.after";

        /**
         * 删除群成员后
         */
        public static final String GROUP_MEMBER_DELETE_AFTER = "group.member.delete.after";

        /**
         * 添加好友前
         */
        public static final String ADD_FRIEND_BEFORE = "friend.add.before";

        /**
         * 添加好友后
         */
        public static final String ADD_FRIEND_AFTER = "friend.add.after";

        /**
         * 更新好友前
         */
        public static final String UPDATE_FRIEND_BEFORE = "friend.update.before";

        /**
         * 更新好友后
         */
        public static final String UPDATE_FRIEND_AFTER = "friend.update.after";

        /**
         * 删除好友后
         */
        public static final String DELETE_FRIEND_AFTER = "friend.delete.after";

        /**
         * 加入黑名单后
         */
        public static final String ADD_BLACK_AFTER = "black.add.after";

        /**
         * 移出黑名单
         */
        public static final String DELETE_BLACK = "black.delete";

        /**
         * 消息发送后
         */
        public static final String SEND_MESSAGE_AFTER = "message.send.after";

        /**
         * 消息发送前
         */
        public static final String SEND_MESSAGE_BEFORE = "message.send.before";
    }
}
