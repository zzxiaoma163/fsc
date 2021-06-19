package com.fsc.fscclient.core;

import com.fsc.fscclient.enums.CodeEnum;
import com.fsc.fscclient.model.BackInfo;
import com.fsc.fscclient.model.TransFile;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;


public class ClientHandler extends ChannelInboundHandlerAdapter {
    private int len;
    private volatile long start = 0;
    public RandomAccessFile randomAccessFile;
    private TransFile tf;
    private final int BufferSize = 5120;

    public ClientHandler(TransFile tf) {
        this.tf = tf;
    }

    public void channelActive(ChannelHandlerContext ctx) {
        try {
            if (tf.getCode() == CodeEnum.ADDFILE.getCode()) {
                randomAccessFile = new RandomAccessFile(tf.getFile(), "r");
            }
            transByte(ctx);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException i) {
            i.printStackTrace();
        }
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof BackInfo) {
            BackInfo backInfo = (BackInfo) msg;
            if (backInfo.isEnd()) {
                try {
                    if (randomAccessFile != null) {
                        randomAccessFile.close();
                        randomAccessFile = null;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ctx.close();
            } else {
                try {
                    start = backInfo.getStart();
                    if (start != -1) {
                        transByte(ctx);
                    } else {
                        randomAccessFile.close();
                        ctx.close();
                    }
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException i) {
                    i.printStackTrace();
                }
            }
        }
    }

    private void transByte(ChannelHandlerContext ctx)
            throws IOException {
        if (tf.getCode() == CodeEnum.ADDFILE.getCode()) {
            randomAccessFile.seek(start);
            byte[] bytes = new byte[getSendLength()];
            if ((len = randomAccessFile.read(bytes)) != -1) {
                tf.setEnd(len);
                tf.setBytes(bytes);
                tf.setSize(randomAccessFile.length());
                ctx.writeAndFlush(tf);
            }
        } else {
            ctx.writeAndFlush(tf);
        }
    }

    private int getSendLength() throws IOException {
        int residuelen = (int) (randomAccessFile.length() - start);
        int sendLength = BufferSize;
        if (residuelen < BufferSize) {
            sendLength = residuelen;
        }
        return sendLength;
    }
}
