package cn.ctrlcv.im.serve.utils;

import cn.ctrlcv.im.common.constant.Constants;
import com.alibaba.fastjson.JSON;
import lombok.extern.slf4j.Slf4j;
import org.I0Itec.zkclient.ZkClient;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * Class Name: zkUtil
 * Class Description: zookeeper 工具类
 *
 * @author liujm
 * @date 2023-03-15
 */
@Slf4j
@Component
public class ZkUtil {

    @Resource
    private ZkClient zkClient;
    /**
     * 从 zookeeper 获取所有 TCP 服务器节点
     *
     * @return
     */
    public List<String> getAllTcpNode() {
        List<String> children = zkClient.getChildren(Constants.IM_CORE_ZK_ROOT + Constants.IM_CORE_ZK_ROOT_TCP);
        log.info("查询所有zookeeper结点成功， node =[{}].", JSON.toJSONString(children));
        return children;
    }

    /**
     * 从 zookeeper 获取所有 ws 服务器节点
     *
     * @return
     */
    public List<String> getAllWsNode() {
        List<String> children = zkClient.getChildren(Constants.IM_CORE_ZK_ROOT + Constants.IM_CORE_ZK_ROOT_WS);
        log.info("Query all node =[{}] success.", JSON.toJSONString(children));
        return children;
    }

}
