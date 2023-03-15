package cn.ctrlcv.im.tcp.redis;

import cn.ctrlcv.im.codec.config.BootstrapConfig;
import cn.ctrlcv.im.tcp.receiver.UserLoginMessageListener;
import org.redisson.api.RedissonClient;

/**
 * Class Name: RedisManager
 * Class Description: Redis初始化配置
 *
 * @author liujm
 * @date 2023-03-14
 */
public class RedisManager {

    private static RedissonClient redissonClient;

    private static Integer loginModel;

    public static void init(BootstrapConfig config){
        loginModel = config.getIm().getLoginModel();

        SingleClientStrategy singleClientStrategy = new SingleClientStrategy();
        redissonClient = singleClientStrategy.getRedissonClient(config.getIm().getRedis());

        new UserLoginMessageListener(loginModel).listenerUserLogin();

    }

    public static RedissonClient getRedissonClient(){
        return redissonClient;
    }


}
