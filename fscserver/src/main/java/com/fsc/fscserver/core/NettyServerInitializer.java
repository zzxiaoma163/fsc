package com.fsc.fscserver.core;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.util.List;

public class NettyServerInitializer extends ByteToMessageDecoder {

    private static final String HTTP_PREFIX = "HTTP/";

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        String protocol = getBufStart(in);
        if (protocol.indexOf(HTTP_PREFIX)>-1) {
            ctx.pipeline().addLast(new HttpServerCodec());
            ctx.pipeline().addLast(new HttpObjectAggregator(512 * 1024));
            ctx.pipeline().addLast(new ChunkedWriteHandler());
            ctx.pipeline().addLast(new HttpValidHandler());
            ctx.pipeline().addLast(new HttpDownHandler());
            ctx.pipeline().addLast(new AllServerHandler());

        } else {
            ctx.pipeline().addLast(new ObjectEncoder());
            ctx.pipeline().addLast(
                    new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers
                            .weakCachingConcurrentResolver(this.getClass()
                                    .getClassLoader())));
            ctx.pipeline().addLast(new ServerMessageDecoder());
            ctx.pipeline().addLast(new ServerMessageEncoder());
            ctx.pipeline().addLast(new TransferServerHandler());
        }
        in.resetReaderIndex();
        ctx.pipeline().remove(this.getClass());
    }

    private String getBufStart(ByteBuf in) {
        int length = in.readableBytes();
        // 标记读位置
        in.markReaderIndex();
        byte[] content = new byte[length];
        in.readBytes(content);
        return new String(content);
    }

}
