package com.fsc.fscserver.core;


import com.fsc.fscserver.controller.Action;
import com.fsc.fscserver.controller.HttpRoute;
import com.fsc.fscserver.controller.RequestBody;
import com.fsc.fscserver.enums.Content;
import com.fsc.fscserver.enums.ResultCode;
import com.fsc.fscserver.model.GeneralResponse;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;

import static io.netty.buffer.Unpooled.copiedBuffer;

public class AllServerHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static final Logger log = LoggerFactory
            .getLogger(AllServerHandler.class);

    @Override
    protected void channelRead0(ChannelHandlerContext channelHandlerContext, FullHttpRequest fullHttpRequest) throws UnsupportedEncodingException {
        System.out.println(fullHttpRequest.headers().get("Authorization"));
        FullHttpResponse response = null;
        Gson gson = new Gson();
        if (fullHttpRequest.method() != HttpMethod.POST) {
            GeneralResponse generalResponse = new GeneralResponse(ResultCode.NOPOST.getCode(), ResultCode.NOPOST.getName());
            ByteBuf buf = copiedBuffer(gson.toJson(generalResponse), CharsetUtil.UTF_8);
            response = HttpResponseUtil.responseOK(HttpResponseStatus.OK, buf);
        } else {
            String uri = fullHttpRequest.uri();
            if (uri.contains(Content.URLSPLIT)) {
                uri = uri.substring(0, uri.indexOf(Content.URLSPLIT));
            }
            Action action = HttpRoute.getRoute(uri);
            if (action != null) {
                Class[] classes = action.getMethod().getParameterTypes();
                Object[] objects = new Object[classes.length];
                for (int i = 0; i < classes.length; i++) {
                    Class c = classes[i];
                    //处理@RequestBody注解
                    Annotation[] parameterAnnotation = action.getMethod().getParameterAnnotations()[i];
                    if (parameterAnnotation.length > 0) {
                        for (int j = 0; j < parameterAnnotation.length; j++) {
                            if (parameterAnnotation[j].annotationType() == RequestBody.class &&
                                    fullHttpRequest.headers().get(HttpHeaderNames.CONTENT_TYPE.toString()).equals(HttpHeaderValues.APPLICATION_JSON.toString())) {
                                ByteBuf jsonBuf = fullHttpRequest.content();
                                objects[i] = gson.fromJson(jsonBuf.toString(CharsetUtil.UTF_8), c);
                            }
                        }
                    }
                }
                GeneralResponse generalResponse = action.call(objects);
                ByteBuf buf = copiedBuffer(gson.toJson(generalResponse), CharsetUtil.UTF_8);
                response = HttpResponseUtil.responseOK(HttpResponseStatus.OK, buf);
            } else {
                GeneralResponse generalResponse = new GeneralResponse(ResultCode.NOVALIDURL.getCode(), ResultCode.NOVALIDURL.getName());
                ByteBuf buf = copiedBuffer(gson.toJson(generalResponse), CharsetUtil.UTF_8);
                response = HttpResponseUtil.responseOK(HttpResponseStatus.OK, buf);
            }

        }
        // 发送响应
        channelHandlerContext.writeAndFlush(response).addListener(ChannelFutureListener.CLOSE);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }
}
