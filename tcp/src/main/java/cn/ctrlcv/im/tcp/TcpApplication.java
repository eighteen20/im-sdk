package cn.ctrlcv.im.tcp;

import cn.ctrlcv.im.tcp.server.LimServer;
import cn.ctrlcv.im.tcp.server.LimWebSocketServer;

/**
 * @author ljm19
 */
public class TcpApplication {

    public static void main(String[] args) {
        new LimServer(9000);
        new LimWebSocketServer(9100);
    }

}
