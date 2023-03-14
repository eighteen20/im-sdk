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
     * Redis key 常量
     */
    public static class RedisConstants {
        /**
         * 用户Session。
         * 格式：APPID + USER_SESSION_CONSTANTS + userId
         */
        public static final String USER_SESSION_CONSTANTS = ":userSession:";
    }
}
