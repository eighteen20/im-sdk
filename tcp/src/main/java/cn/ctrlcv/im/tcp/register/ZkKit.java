package cn.ctrlcv.im.tcp.register;

import cn.ctrlcv.im.common.constant.Constants;
import org.I0Itec.zkclient.ZkClient;

/**
 * Class Name: ZKit
 * Class Description: 对Zookeeper进行配置
 * <p>
 *     <p>节点存放：</p>
 *     <p>im-coreRoot/tcp/ip:port</p>
 * </p>
 *
 * @author liujm
 * @date 2023-03-14
 */
public class ZkKit {

    private ZkClient zkClient;

    public ZkKit(ZkClient zkClient) {
        this.zkClient = zkClient;
    }


    /**
     * 创建父节点
     */
    public void createRootNode(){
        boolean exists = zkClient.exists(Constants.IM_CORE_ZK_ROOT);
        if(!exists){
            zkClient.createPersistent(Constants.IM_CORE_ZK_ROOT);
        }
        boolean tcpExists = zkClient.exists(Constants.IM_CORE_ZK_ROOT +
                Constants.IM_CORE_ZK_ROOT_TCP);
        if(!tcpExists){
            zkClient.createPersistent(Constants.IM_CORE_ZK_ROOT +
                    Constants.IM_CORE_ZK_ROOT_TCP);
        }

        boolean wsExists = zkClient.exists(Constants.IM_CORE_ZK_ROOT +
                Constants.IM_CORE_ZK_ROOT_WS);
        if(!wsExists){
            zkClient.createPersistent(Constants.IM_CORE_ZK_ROOT +
                    Constants.IM_CORE_ZK_ROOT_WS);
        }
    }

    /**
     * 创建子节点
     *
     * @param path 节点路径
     */
    public void createNode(String path){
        if(!zkClient.exists(path)){
            zkClient.createPersistent(path);
        }
    }

}
