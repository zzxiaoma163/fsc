package com.fsc.fscserver.core;

import com.fsc.fscserver.model.TransFile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

import com.google.gson.Gson;

public class ServerMessageDecoder extends MessageToMessageDecoder<String> {
	@Override
	protected void decode(ChannelHandlerContext ctx, String msg,
			List<Object> out) throws Exception {
		Gson gson = new Gson();
		Object outobj = gson.fromJson(msg, TransFile.class);
		out.add(outobj);
	}
}
