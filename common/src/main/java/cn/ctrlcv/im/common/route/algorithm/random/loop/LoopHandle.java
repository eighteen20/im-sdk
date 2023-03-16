package cn.ctrlcv.im.common.route.algorithm.random.loop;

import cn.ctrlcv.im.common.enums.UserErrorCodeEnum;
import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.common.route.RouteHandler;

import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Class Name: LoopHandle
 * Class Description: 负载均衡-轮询模式
 *
 * @author liujm
 * @date 2023-03-16
 */
public class LoopHandle implements RouteHandler {

    private final AtomicLong index = new AtomicLong();

    @Override
    public String routerServer(List<String> values, String key) {
        int size = values.size();
        if (size == 0) {
            throw new ApplicationException(UserErrorCodeEnum.SERVER_NOT_AVAILABLE);
        }
        Long result = index.incrementAndGet() % size;
        if (result < 0) {
            result = 0L;
        }
        return values.get(result.intValue());
    }
}
