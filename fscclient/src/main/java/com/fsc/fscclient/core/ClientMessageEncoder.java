package com.fsc.fscclient.core;

import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageEncoder;

import java.util.List;

public class ClientMessageEncoder extends MessageToMessageEncoder<Object> {
    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg,
                          List<Object> out) throws Exception {
        Gson gson = new Gson();
        out.add(gson.toJson(msg));
    }
}
