package cn.ctrlcv.im.serve.sequence;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Class Name: RedisSeq
 * Class Description: redis序列号生成器
 *
 * @author liujm
 * @date 2023-03-23
 */
@Component
public class RedisSeq {

    @Resource
    private StringRedisTemplate stringRedisTemplate;

    /**
     * 生成序列号
     *
     * @param key 序列号key
     * @return 序列号
     */
    public Long nextSeq(String key) {
        return stringRedisTemplate.opsForValue().increment(key);
    }
}