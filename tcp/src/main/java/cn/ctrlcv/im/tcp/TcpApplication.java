package cn.ctrlcv.im.tcp;

import cn.ctrlcv.im.codec.config.BootstrapConfig;
import cn.ctrlcv.im.tcp.redis.RedisManager;
import cn.ctrlcv.im.tcp.server.ImServer;
import cn.ctrlcv.im.tcp.server.ImWebSocketServer;
import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

/**
 * @author ljm19
 */
public class TcpApplication {

    public static void main(String[] args) {
        System.out.println("开始启动，参数：" + Arrays.toString(args));
        if(args.length > 0){
            start(args[0]);
        }
    }

    private static void start(String path){
        try {
            Yaml yaml = new Yaml();
            InputStream inputStream = Files.newInputStream(Paths.get(path));
            BootstrapConfig bootstrapConfig = yaml.loadAs(inputStream, BootstrapConfig.class);

            new ImServer(bootstrapConfig.getIm()).start();
            new ImWebSocketServer(bootstrapConfig.getIm()).start();

            RedisManager.init(bootstrapConfig);
        }catch (Exception e){
            e.printStackTrace();
            System.exit(500);
        }
    }

}
