package cn.ctrlcv.im.common.route.algorithm.random.consistenthash;

import cn.ctrlcv.im.common.enums.UserErrorCodeEnum;
import cn.ctrlcv.im.common.exception.ApplicationException;

import java.util.SortedMap;
import java.util.TreeMap;

/**
 * Class Name: TreeMapConsistentHash
 * Class Description: 使用 TreeMap 实现一致性 hash算法
 *
 * @author liujm
 * @date 2023-03-16
 */
public class TreeMapConsistentHash extends AbstractConsistentHash {

    private final TreeMap<Long, String> treeMap = new TreeMap<>();

    /**
     * 虚拟节点数量（真实节点的拷贝），防止treeMap中节点过少，导致分配不均
     */
    private static final int NODE_SIZE = 2;

    @Override
    protected void add(long key, String value) {
        for (int i = 0; i < NODE_SIZE; i++) {
            this.treeMap.put(super.hash("node-" + key + i), value);
        }

        this.treeMap.put(key, value);
    }

    @Override
    protected void processBefore() {
        // 节点数量是动态数量，每次都要清空
        this.treeMap.clear();
    }

    @Override
    protected String getFirstNodeValue(String key) {
        // 这里key 就是userId, hash后找出最近的结点
        Long hash = super.hash(key);

        SortedMap<Long, String> sortedMap = this.treeMap.tailMap(hash);
        if (!sortedMap.isEmpty()) {
            return sortedMap.get(sortedMap.firstKey());
        }
        if (treeMap.size() == 0) {
            throw new ApplicationException(UserErrorCodeEnum.SERVER_NOT_AVAILABLE);
        }

        return this.treeMap.firstEntry().getValue();
    }
}
