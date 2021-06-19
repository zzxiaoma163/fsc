package com.fsc.fscclient.core;

import com.fsc.fscclient.model.BackInfo;
import com.google.gson.Gson;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;

import java.util.List;

public class ClientMessageDecoder extends MessageToMessageDecoder<String> {
    @Override
    protected void decode(ChannelHandlerContext ctx, String msg,
                          List<Object> out) throws Exception {
        Gson gson = new Gson();
        Object outobj = gson.fromJson(msg, BackInfo.class);
        out.add(outobj);
    }
}
