package com.fsc.fscclient.core;

import com.fsc.fscclient.model.TransFile;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



public class FileClient {
    private static Logger logger = LoggerFactory.getLogger(FileClient.class);
    public void connect(int port, String host, final TransFile tf) {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class).option(ChannelOption.TCP_NODELAY, true).handler(new ChannelInitializer<Channel>() {

                @Override
                protected void initChannel(Channel ch) throws Exception {
                    ch.pipeline().addLast(new ObjectEncoder());
                    ch.pipeline().addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.weakCachingConcurrentResolver(null)));

                    ch.pipeline().addLast(new ClientMessageDecoder());
                    ch.pipeline().addLast(new ClientMessageEncoder());
                    ch.pipeline().addLast(new ClientHandler(tf));
                }
            });
            ChannelFuture f = b.connect(host, port).sync();
            f.channel().closeFuture().sync();
        } catch(InterruptedException e){
            e.printStackTrace();
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        } finally {
            group.shutdownGracefully();
        }

    }

    private static String getSuffix(String fileName) {
        String fileType = fileName.substring(fileName.lastIndexOf("."), fileName.length());
        return fileType;
    }
}
