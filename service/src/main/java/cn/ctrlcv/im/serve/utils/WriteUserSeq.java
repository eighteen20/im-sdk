package cn.ctrlcv.im.serve.utils;

import cn.ctrlcv.im.common.constant.Constants;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;

/**
 * Class Name: WriteUserSeq
 * Class Description: TODO
 *
 * @author liujm
 * @date 2023-05-25
 */
@Component
public class WriteUserSeq {

    @Resource
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 保存seq,和用户绑定
     *
     * @param appId 应用id
     * @param userId 用户id
     * @param type seq的类型
     * @param seq 上一个seq
     */
    public void writeUserSeq(Integer appId, String userId, String type, Long seq) {
        String key = appId + Constants.RedisKey.SEQ_PREFIX + userId;
        redisTemplate.opsForHash().put(key, type, seq);
    }
}
