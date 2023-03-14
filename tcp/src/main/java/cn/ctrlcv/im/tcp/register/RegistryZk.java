package cn.ctrlcv.im.tcp.register;

import cn.ctrlcv.im.codec.config.BootstrapConfig;
import cn.ctrlcv.im.common.constant.Constants;
import lombok.extern.slf4j.Slf4j;

/**
 * Class Name: RegistryZk
 * Class Description: 注册服务到zookeeper
 *
 * @author liujm
 * @date 2023-03-14
 */
@Slf4j
public class RegistryZk implements Runnable {

    private ZkKit zkKit;
    private String ip;
    private BootstrapConfig.TcpConfig tcpConfig;

    public RegistryZk(ZkKit zkKit, String ip, BootstrapConfig.TcpConfig tcpConfig) {
        this.zkKit = zkKit;
        this.ip = ip;
        this.tcpConfig = tcpConfig;
    }

    @Override
    public void run() {
        this.zkKit.createRootNode();
        String tcpPath = Constants.IM_CORE_ZK_ROOT + Constants.IM_CORE_ZK_ROOT_TCP + "/" + ip + ":" + tcpConfig.getTcpPort();
        this.zkKit.createNode(tcpPath);

        log.info(" ========== Tcp 服务成功注册到zookeeper， 节点=[{}] ==========", tcpPath);

        String wsPath = Constants.IM_CORE_ZK_ROOT + Constants.IM_CORE_ZK_ROOT_WS + "/" + ip + ":" + tcpConfig.getWebSocketPort();
        this.zkKit.createNode(wsPath);
        log.info(" ========== webSocket 服务成功注册到zookeeper， 节点=[{}] ==========", wsPath);
    }
}
