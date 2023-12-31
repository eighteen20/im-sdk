package cn.ctrlcv.im.common.route.algorithm.random.random;

import cn.ctrlcv.im.common.enums.UserErrorCodeEnum;
import cn.ctrlcv.im.common.exception.ApplicationException;
import cn.ctrlcv.im.common.route.RouteHandler;

import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

/**
 * Class Name: RandomHandler
 * Class Description: 负载均衡-随机模式
 *
 * @author liujm
 * @date 2023-03-15
 */
public class RandomHandle implements RouteHandler {
    @Override
    public String routerServer(List<String> values, String key) {
        int size = values.size();
        if (size == 0) {
            throw new ApplicationException(UserErrorCodeEnum.SERVER_NOT_AVAILABLE);
        }

        int i = ThreadLocalRandom.current().nextInt(size);

        return values.get(i);
    }
}
