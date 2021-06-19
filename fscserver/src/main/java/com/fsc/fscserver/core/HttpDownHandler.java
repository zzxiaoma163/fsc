package com.fsc.fscserver.core;

import com.fsc.fscserver.enums.Content;
import com.fsc.fscserver.enums.ResultCode;
import com.fsc.fscserver.model.GeneralResponse;
import com.fsc.fscserver.util.PropertiesUtils;
import com.google.gson.Gson;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.CharsetUtil;
import org.apache.log4j.Logger;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.netty.buffer.Unpooled.copiedBuffer;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


public class HttpDownHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    private Logger log = Logger.getLogger(HttpDownHandler.class);
    public HttpDownHandler() {
        super(false);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) {
        String uri = request.uri();
        String filePath = "";
        if (uri.startsWith("/down") && request.method().equals(HttpMethod.GET)) {
            FullHttpResponse fullresponse ;
            Map<String, Object> params = new HashMap<String, Object>();
            QueryStringDecoder decoder = new QueryStringDecoder(uri);
            Map<String, List<String>> paramList = decoder.parameters();
            for (Map.Entry<String, List<String>> entry : paramList.entrySet()) {
                params.put(entry.getKey(), entry.getValue().get(0));
            }
            if(params.get("file")==null){
                log.warn("params not exist");
            }else{
                filePath = PropertiesUtils.getStringValue(Content.FILEPATH)+File.separator+params.get("file").toString();
            }
            GeneralResponse generalResponse = null;
            File file = new File(filePath);
            try {
                final RandomAccessFile raf = new RandomAccessFile(file, "r");
                long fileLength = raf.length();
                HttpResponse response = new DefaultHttpResponse(HTTP_1_1, HttpResponseStatus.OK);
                response.headers().set(HttpHeaderNames.CONTENT_LENGTH, fileLength);
                response.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/octet-stream");
                System.out.println(file.getName());
                response.headers().add(HttpHeaderNames.CONTENT_DISPOSITION,"attachment; filename="+ URLEncoder.encode(file.getName(), "UTF-8"));
                ctx.write(response);
                ChannelFuture sendFileFuture = ctx.write(new DefaultFileRegion(raf.getChannel(), 0, fileLength), ctx.newProgressivePromise());
                sendFileFuture.addListener(new ChannelProgressiveFutureListener() {
                    @Override
                    public void operationComplete(ChannelProgressiveFuture future)
                            throws Exception {
                        log.info(file.getName()+" down complete.");
                        raf.close();
                    }

                    @Override
                    public void operationProgressed(ChannelProgressiveFuture future,
                                                    long progress, long total) throws Exception {

                    }
                });
                ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            } catch (FileNotFoundException e) {
                log.error(file.getPath() + "  not found");
                Gson gson = new Gson();
                generalResponse = new GeneralResponse(ResultCode.NOFILE.getCode(),ResultCode.NOFILE.getName());
                ByteBuf buf = copiedBuffer(gson.toJson(generalResponse), CharsetUtil.UTF_8);
                fullresponse = HttpResponseUtil.responseOK(HttpResponseStatus.OK, buf);
                ctx.writeAndFlush(fullresponse).addListener(ChannelFutureListener.CLOSE);;
            } catch (IOException e) {
                log.error(e.getMessage());
                Gson gson = new Gson();
                generalResponse = new GeneralResponse(ResultCode.ERROR.getCode(),ResultCode.ERROR.getName());
                ByteBuf buf = copiedBuffer(gson.toJson(generalResponse), CharsetUtil.UTF_8);
                fullresponse = HttpResponseUtil.responseOK(HttpResponseStatus.OK, buf);
                ctx.writeAndFlush(fullresponse).addListener(ChannelFutureListener.CLOSE);;
            }
        } else {
            ctx.fireChannelRead(request);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable e) {
        log.warn("{}", e);
        ctx.close();

    }
}
