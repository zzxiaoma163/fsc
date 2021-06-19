package com.fsc.fscmonitor.core;

import com.fsc.fscmonitor.enums.Content;
import com.fsc.fscmonitor.util.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class FileOper {
    private static Logger logger = LoggerFactory.getLogger(FileOper.class);
    private static String address = PropertiesUtils.getStringValue(Content.ADDRESS);

    public static void write(String msg) {
        BufferedWriter bw = null;
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");
        String day = df.format(new Date());
        write(msg, day);
    }

    public static void write(String msg, String day) {
        BufferedWriter bw = null;
        try {
            File dir = new File(address);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            File file = new File(address + day);
            if (!file.exists()) {
                boolean newFile = file.createNewFile();
                if (!newFile) {
                    logger.error("create filelog error:");
                }
            }
            bw = new BufferedWriter(new FileWriter(address + day, true));
            bw.write(msg);
        } catch (IOException e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static List<File> getFileList(String strPath, List<File> filelist) {
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                String fileName = files[i].getName();
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(files[i].getAbsolutePath(), filelist); // 获取文件绝对路径
                } else {
                    filelist.add(files[i]);
                }
            }
        }
        return filelist;
    }
}
