package cn.ctrlcv.im.tcp.redis;

import cn.ctrlcv.im.codec.config.BootstrapConfig;
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


    public static void init(BootstrapConfig config){
        SingleClientStrategy singleClientStrategy = new SingleClientStrategy();
        redissonClient = singleClientStrategy.getRedissonClient(config.getIm().getRedis());

    }

    public static RedissonClient getRedissonClient(){
        return redissonClient;
    }


}
