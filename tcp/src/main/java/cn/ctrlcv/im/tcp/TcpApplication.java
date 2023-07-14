package cn.ctrlcv.im.tcp;

import cn.ctrlcv.im.codec.config.BootstrapConfig;
import cn.ctrlcv.im.tcp.receiver.MessageReceiver;
import cn.ctrlcv.im.tcp.redis.RedisManager;
import cn.ctrlcv.im.tcp.register.RegistryZk;
import cn.ctrlcv.im.tcp.register.ZkKit;
import cn.ctrlcv.im.tcp.server.ImServer;
import cn.ctrlcv.im.tcp.server.ImWebSocketServer;
import cn.ctrlcv.im.tcp.utils.MqFactory;
import org.I0Itec.zkclient.ZkClient;
import org.yaml.snakeyaml.Yaml;

import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.concurrent.*;

/**
 * @author ljm19
 */
public class TcpApplication {

    public static void main(String[] args) {
        System.out.println("开始启动，参数：" + Arrays.toString(args));
        if (args.length > 0) {
            start(args[0]);
        }
    }

    private static void start(String path) {
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = Files.newInputStream(Paths.get(path));
            BootstrapConfig bootstrapConfig = yaml.loadAs(inputStream, BootstrapConfig.class);

            new ImServer(bootstrapConfig.getIm()).start();
            new ImWebSocketServer(bootstrapConfig.getIm()).start();

            RedisManager.init(bootstrapConfig);

            MqFactory.init(bootstrapConfig.getIm().getRabbitmq());

            registerZk(bootstrapConfig);

            MessageReceiver.init(bootstrapConfig.getIm().getBrokerId());

        } catch (Exception e) {
            e.printStackTrace();
            System.exit(500);
        }
    }


    private static final ExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    public static void registerZk(BootstrapConfig config) throws UnknownHostException {
//        String hostAddress = "127.0.0.1";
        String hostAddress = InetAddress.getLocalHost().getHostAddress();
        ZkClient zkClient = new ZkClient(config.getIm().getZkConfig().getZkAddr(),
                config.getIm().getZkConfig().getZkConnectTimeOut());
        ZkKit zKit = new ZkKit(zkClient);
        RegistryZk registryZk = new RegistryZk(zKit, hostAddress, config.getIm());
        executorService.execute(registryZk);
    }


}
