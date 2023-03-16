package cn.ctrlcv.im.common.route.algorithm.random.consistenthash;

import cn.ctrlcv.im.common.route.RouteHandler;

import java.util.List;

/**
 * Class Name: consistentHashHandle
 * Class Description: 负载均衡 - 一致性hash
 *
 * @author liujm
 * @date 2023-03-16
 */
public class ConsistentHashHandle implements RouteHandler {

    private AbstractConsistentHash hash;

    public ConsistentHashHandle() {
    }

    public ConsistentHashHandle(AbstractConsistentHash hash) {
        this.hash = hash;
    }

    @Override
    public String routerServer(List<String> values, String key) {
        return hash.process(values, key);
    }
}
