package com.fsc.fscserver.core;

import com.fsc.fscserver.enums.CodeEnum;
import com.fsc.fscserver.enums.Content;
import com.fsc.fscserver.model.BackInfo;
import com.fsc.fscserver.model.TransFile;
import com.fsc.fscserver.util.PropertiesUtils;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.apache.commons.codec.digest.DigestUtils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class TransferServerHandler extends ChannelInboundHandlerAdapter {

	private static final Logger log = LoggerFactory
			.getLogger(TransferServerHandler.class);

	private volatile int len;
	private volatile long start = 0;

	private String filepath = PropertiesUtils.getStringValue(Content.FILEPATH);
	private String backfilepath = PropertiesUtils.getStringValue(Content.BACK_FILEPATH);
	private RandomAccessFile randomAccessFile;
	private File file;
	private long fileSize = -1;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
			throws Exception {
		if (msg instanceof TransFile) {
			TransFile ef = (TransFile) msg;
			InetSocketAddress insocket = (InetSocketAddress) ctx.channel()
					.remoteAddress();
			String clientIP = insocket.getAddress().getHostAddress();
			String dir = filepath + File.separator + clientIP + File.separator + ef.getPath();
			if (ef.getCode() == CodeEnum.ADDFILE.getCode()) {
				byte[] bytes = ef.getBytes();
				len = ef.getEnd();
				String md5 = ef.getMd5();
				createFolder(ef,dir);
				if (start == 0) {
					String path = dir + File.separator
							+ ef.getName();
					file = new File(path);
					fileSize = ef.getSize();
					if (file.exists() && !file.isDirectory()) {
						InputStream is = new FileInputStream(file);
						String ymd5 = DigestUtils.md5Hex(is);
						is.close();
						if (md5.equals(ymd5)) {
							BackInfo backInfo = new BackInfo(start);
							ctx.writeAndFlush(backInfo);
							return;
						}
					}else{
						if(!"".equals(file.getParent())) {
							File fileParent = new File(file.getParent());
							if(!fileParent.exists()) {
								fileParent.mkdirs();
							}
						}
					}

					randomAccessFile = new RandomAccessFile(file, "rw");
				}
				randomAccessFile.seek(start);
				randomAccessFile.write(bytes);
				start = start + len;

				if (len > 0 && (start < fileSize && fileSize != -1)) {
					BackInfo backInfo = new BackInfo(start, md5, (start * 100)
							/ fileSize);
					ctx.writeAndFlush(backInfo);
				} else {
					log.info("create file success:" + ef.getName());

					BackInfo backInfo = new BackInfo(start);
					ctx.writeAndFlush(backInfo);
					randomAccessFile.close();
					file = null;
					fileSize = -1;
					randomAccessFile = null;
				}
			} else if (ef.getCode() == CodeEnum.DELFILE.getCode()) {
				String path = dir
						+ File.separator + ef.getName();
				file = new File(path);
				if(file.exists()) {
					String backpath = backfilepath + File.separator + clientIP + File.separator + ef.getPath();
					File backfiles = new File(backpath);
					if (!backfiles.exists()) {
						backfiles.mkdirs();
					}
					File backfile = new File(backpath + File.separator + ef.getName());
					Files.copy(file.toPath(), backfile.toPath(),StandardCopyOption.COPY_ATTRIBUTES,StandardCopyOption.REPLACE_EXISTING);
					boolean b = file.delete();
					if (!b) {
						log.error("del file error:" + path);
					}
				}
				BackInfo backInfo = new BackInfo(start);
				ctx.writeAndFlush(backInfo);
			}
		}
	}

	private void createFolder(TransFile ef,String dir) {
		if (!ef.getPath().equals("")) {
			File dirfile = new File(dir);
			if (!dirfile.exists()) {
				dirfile.mkdirs();
			}
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
		cause.printStackTrace();
		if (randomAccessFile != null) {
			try {
				randomAccessFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		ctx.close();
	}

}
