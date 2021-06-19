package com.fsc.fscserver;

import com.fsc.fscserver.controller.RouteConfig;
import com.fsc.fscserver.core.FileInitializer;
import com.fsc.fscserver.enums.Content;
import com.fsc.fscserver.util.FileUtils;
import com.fsc.fscserver.util.PropertiesUtils;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import org.apache.log4j.Logger;

import java.util.Timer;
import java.util.TimerTask;


public class FscServerMain extends Thread {
	private Logger log = Logger.getLogger(FscServerMain.class);
	private final static String backfilepath = PropertiesUtils.getStringValue(Content.BACK_FILEPATH);
	private final static int backtime = PropertiesUtils.getIntValue(Content.BACK_TIME);
	private final static int port = PropertiesUtils.getIntValue(Content.PORT);

	public void start() {
		RouteConfig.initConfig();
		EventLoopGroup bossGroup = new NioEventLoopGroup();
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup)
					.channel(NioServerSocketChannel.class)
					.option(ChannelOption.SO_BACKLOG, 1024)
					.childHandler(new FileInitializer());

			log.info("bind port:" + port);

			ChannelFuture f = b.bind(port).sync();
			f.channel().closeFuture().sync();
		} catch (InterruptedException e) {
			log.error(e.getMessage());
		} finally {
			bossGroup.shutdownGracefully();
			workerGroup.shutdownGracefully();
		}
	}

	public static void main(String[] args) {
		int port = PropertiesUtils.getIntValue(Content.PORT);
		if (args != null && args.length > 0) {
			try {
				port = Integer.valueOf(args[0]);
			} catch (NumberFormatException e) {
				e.printStackTrace();
			}
		}
		new Thread(() -> {
			try {
				Timer timer = new Timer();
				timer.scheduleAtFixedRate(new TimerTask() {
					public void run() {
						FileUtils.getFileList(backfilepath, backtime);
					}
				}, 0, 1000 * 60 * 60 * 24);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		).start();
		new FscServerMain().start();
	}
}