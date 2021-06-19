package com.fsc.fscserver.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.List;


public class FileOper {
    private static Logger logger = LoggerFactory.getLogger(FileOper.class);

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
