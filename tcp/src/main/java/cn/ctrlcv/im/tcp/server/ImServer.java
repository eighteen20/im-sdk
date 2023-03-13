package cn.ctrlcv.im.tcp.server;

import cn.ctrlcv.im.codec.MessageDecoder;
import cn.ctrlcv.im.codec.config.BootstrapConfig;
import cn.ctrlcv.im.tcp.handler.NettyServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;

/**
 * Class Name: LimServer
 * Class Description: Im-TCP主程序
 *
 * @author liujm
 * @date 2023-03-12
 */
@Slf4j
public class ImServer {

    private BootstrapConfig.TcpConfig tcpConfig;

    EventLoopGroup bossGroup;
    EventLoopGroup workGroup;
    ServerBootstrap bootstrap;

    public ImServer(BootstrapConfig.TcpConfig tcpConfig) {
        this.tcpConfig = tcpConfig;
        bossGroup = new NioEventLoopGroup(this.tcpConfig.getBossThreadSize());
        workGroup = new NioEventLoopGroup(this.tcpConfig.getWorkThreadSize());
        bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workGroup)
                .channel(NioServerSocketChannel.class)
                // 服务端可连接队列大小
                .option(ChannelOption.SO_BACKLOG, 10240)
                // 允许重复使用本地地址和端口
                .option(ChannelOption.SO_REUSEADDR, true)
                // 是否禁用nagle算法
                // 简单点说是否批量发送数据 true关闭 false开启。 开启的话可以减少一定的网络开销，但影响消息实时性
                .childOption(ChannelOption.TCP_NODELAY, true)
                // 保活开关，2h没有数据服务端会发送心跳包
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        socketChannel.pipeline().addLast(new MessageDecoder());
                        socketChannel.pipeline().addLast(new NettyServerHandler());
                    }
                })
        ;
    }

    public void start() {
        this.bootstrap.bind(this.tcpConfig.getTcpPort());
        log.info(" ========== IM-TCP 服务启动 =========");
    }

}
