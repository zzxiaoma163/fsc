package com.fsc.fscserver.core;

import com.fsc.fscserver.enums.Content;
import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;

public class HttpResponseUtil {
    public static FullHttpResponse responseOK(HttpResponseStatus status, ByteBuf content) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, status, content);
        if (content != null) {
            response.headers().set(Content.CONTENT_TYPE, Content.PLAIN_UTF8);
            response.headers().set(Content.CONTENT_LENGTH, response.content().readableBytes());
        }
        return response;
    }
}
