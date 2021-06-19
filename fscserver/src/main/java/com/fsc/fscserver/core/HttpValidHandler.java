package com.fsc.fscserver.core;

import com.fsc.fscserver.enums.Content;
import com.fsc.fscserver.enums.ResultCode;
import com.fsc.fscserver.model.GeneralResponse;
import com.fsc.fscserver.util.PropertiesUtils;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class HttpValidHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private Logger log = Logger.getLogger(HttpValidHandler.class);
    public HttpValidHandler() {
        super(false);
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        String author = msg.headers().get("Authorization");
        if(author!=null&&author.equals(Content.BASIC+PropertiesUtils.getStringValue(Content.AUTHOR))){
            ctx.fireChannelRead(msg);
        }else{
            FullHttpResponse response = null;
            Gson gson = new Gson();
            GeneralResponse generalResponse = new GeneralResponse(ResultCode.NOVALID.getCode(),ResultCode.NOVALID.getName());
            ByteBuf buf = copiedBuffer(gson.toJson(generalResponse), CharsetUtil.UTF_8);
            response = HttpResponseUtil.responseOK(HttpResponseStatus.OK, buf);
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        log.warn("{}", e);
        ctx.close();

    }
}
