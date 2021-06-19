package com.fsc.fscserver.core;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;


public class FileInitializer extends ChannelInitializer<Channel> {
	@Override
	protected void initChannel(Channel ch) throws Exception {
		ch.pipeline().addLast(new NettyServerInitializer());
	}
}
