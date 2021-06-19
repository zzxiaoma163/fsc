package com.fsc.fscserver.util;

import java.io.File;

public class FileUtils {
    public static void getFileList(String strPath,int day) {
        File dir = new File(strPath);
        File[] files = dir.listFiles(); // 该文件目录下文件全部放入数组
        if (files != null) {
            for (int i = 0; i < files.length; i++) {
                int diffday = DateUtils.differentDays(files[i].lastModified(), System.currentTimeMillis());
                if (files[i].isDirectory()) { // 判断是文件还是文件夹
                    getFileList(files[i].getAbsolutePath(),day); // 获取文件绝对路径
                } else {
                    String fileName = files[i].getName();
                }
                if (diffday >= day) {
                    files[i].delete();
                }
            }
        }
    }
}
