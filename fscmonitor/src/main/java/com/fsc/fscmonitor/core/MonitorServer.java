package com.fsc.fscmonitor.core;

import com.fsc.fscmonitor.controller.RouteConfig;
import com.fsc.fscmonitor.enums.Content;
import com.fsc.fscmonitor.util.PropertiesUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.apache.log4j.Logger;

public class MonitorServer extends Thread {
    private Logger log = Logger.getLogger(MonitorServer.class);
    private int port = PropertiesUtils.getIntValue(Content.PORT);

    @Override
    public void run() {
        RouteConfig.initConfig();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 1024)
                    .childHandler(new ServerInitializer());

            log.info("bind port:" + port);

            ChannelFuture f = b.bind(port).sync();
            f.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error("http error:" + e.getMessage());
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}
