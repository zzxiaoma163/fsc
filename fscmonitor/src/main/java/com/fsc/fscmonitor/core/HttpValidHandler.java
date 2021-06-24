package com.fsc.fscmonitor.core;

import com.fsc.fscmonitor.enums.Content;
import com.fsc.fscmonitor.enums.ResultCode;
import com.fsc.fscmonitor.model.GeneralResponse;
import com.fsc.fscmonitor.util.PropertiesUtils;
import com.google.gson.Gson;
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
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) {
        String author = msg.headers().get("Authorization");
        if(author!=null&&author.equals(Content.BASIC+ PropertiesUtils.getStringValue(Content.AUTHOR))){
            ctx.fireChannelRead(msg);
        }else{
            Gson gson = new Gson();
            GeneralResponse generalResponse = new GeneralResponse(ResultCode.NOVALID.getCode(),ResultCode.NOVALID.getName());
            FullHttpResponse response = HttpResponseUtil.responseOK(HttpResponseStatus.OK, copiedBuffer(gson.toJson(generalResponse), CharsetUtil.UTF_8));
            ctx.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        log.warn("{}", e);
        ctx.close();

    }
}
