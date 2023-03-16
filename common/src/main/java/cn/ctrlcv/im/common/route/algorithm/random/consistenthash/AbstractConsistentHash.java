package cn.ctrlcv.im.common.route.algorithm.random.consistenthash;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

/**
 * Class Name: AbstractConsistentHash
 * Class Description: 一致性 hash 抽象类
 *
 * @author liujm
 * @date 2023-03-16
 */
public abstract class AbstractConsistentHash {

    /**
     * Add element to MAP
     *
     * @param key
     * @param value
     */
    protected abstract void add(long key, String value);

    protected void sort() {};

    /**
     * 处理之前事件
     */
    protected abstract void processBefore();

    /**
     * get First Node Value
     *
     * @param key
     * @return
     */
    protected abstract String getFirstNodeValue(String key);


    /**
     * 传入节点列表以及客户端信息获取一个服务节点
     * @param values
     * @param key
     * @return
     */
    public synchronized String process(List<String> values, String key){
        processBefore();
        for (String value : values) {
            this.add(this.hash(value), value);
        }
        sort();
        return getFirstNodeValue(key) ;
    }

    /**
     * hash 运算
     * @param value
     * @return
     */
    public Long hash(String value){
        MessageDigest md5;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("MD5 not supported", e);
        }
        md5.reset();
        byte[] keyBytes = null;
        keyBytes = value.getBytes(StandardCharsets.UTF_8);

        md5.update(keyBytes);
        byte[] digest = md5.digest();

        // hash code, Truncate to 32-bits
        long hashCode = ((long) (digest[3] & 0xFF) << 24)
                | ((long) (digest[2] & 0xFF) << 16)
                | ((long) (digest[1] & 0xFF) << 8)
                | (digest[0] & 0xFF);

        long truncateHashCode = hashCode & 0xffffffffL;
        return truncateHashCode;
    }

}
