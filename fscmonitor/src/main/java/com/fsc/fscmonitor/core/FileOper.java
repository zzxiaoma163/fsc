package com.fsc.fscmonitor.core;

import com.fsc.fscmonitor.enums.Content;
import com.fsc.fscmonitor.util.PropertiesUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;


public class FileOper {
    private static Logger logger = LoggerFactory.getLogger(FileOper.class);
    private static SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
    private static String address = PropertiesUtils.getStringValue(Content.ADDRESS);
    private static int time = PropertiesUtils.getIntValue(Content.CREATEFILE_TIME);
    private static String mintime = "20000101000000";
    //get log min name
    static{
        File file = new File(address);
        String[] fileNameLists = file.list();
        for (int i=0;i<fileNameLists.length;i++) {
            if(i==0){
                mintime = fileNameLists[i];
            }
            if (fileNameLists[i].compareTo(mintime) < 0) {
                mintime = fileNameLists[i];
            }
        }
    }
    public static void write(String msg) {
        //diff minute
        long diff = 0;
        try {
            diff = (System.currentTimeMillis() - df.parse(mintime).getTime())/1000/60;
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        String day = mintime;
        if(diff >= time){
            day = df.format(new Date());
        }
        write( msg, day);
    }

    public static void write(String msg,String day){
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
        } catch (IOException  e) {
            logger.error(e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                mintime = day;
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
