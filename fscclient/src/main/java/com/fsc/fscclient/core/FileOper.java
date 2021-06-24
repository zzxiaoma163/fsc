package com.fsc.fscclient.core;

import com.fsc.fscclient.enums.CodeEnum;
import com.fsc.fscclient.enums.Content;
import com.fsc.fscclient.model.FileText;
import com.fsc.fscclient.model.TransFile;
import com.fsc.fscclient.util.PropertiesUtils;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;


public class FileOper {
    private static Logger logger = LoggerFactory.getLogger(FileOper.class);
    private static SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
    private static String address = PropertiesUtils.getStringValue(Content.ADDRESS);
    private static String server_ip = PropertiesUtils.getStringValue(Content.SERVER_IP);
    private static String montior_address = PropertiesUtils.getStringValue(Content.MONITOR_ADDRESS);
    private static int port = PropertiesUtils.getIntValue(Content.PORT);
    private static int time = PropertiesUtils.getIntValue(Content.MINUTE);
    public static void read() throws IOException {
        BufferedReader br = null;
        List<String> filelist = new ArrayList<>();

        File file = new File(address);
        String[] fileNameLists = file.list();
        for (String s : fileNameLists) {
            if ( diff(s) >= time) {
                filelist.add(s);
            }
        }
        Collections.sort(filelist);
        Map<String, FileText> ftmap = new HashMap<>();
        if (!filelist.isEmpty()) {
            for (String name : filelist) {
                try {
                    br = new BufferedReader(new FileReader(address + name));
                    String str;
                    while ((str = br.readLine()) != null) {
                        try {
                            FileText fileText = analyFile(str);
                            if (fileText.getType().equals("ENTRY_MODIFY")) {
                                if (ftmap.get(fileText.getFilepath()) == null) {
                                    ftmap.put(fileText.getFilepath(), fileText);
                                }
                            }
                            if (fileText.getType().equals("ENTRY_DELETE")) {
                                if (ftmap.get(fileText.getFilepath()) == null) {
                                    ftmap.put(fileText.getFilepath(), fileText);
                                } else {
                                    ftmap.remove(fileText.getFilepath());
                                }
                            }
                            /*if (fileText.getType().equals("ENTRY_MODIFY")
                                    || fileText.getType().equals("ENTRY_DELETE")) {
                                sendFile(fileText);
                            }*/

                        } catch (Exception e) {
                            e.printStackTrace();
                            throw new RuntimeException(e);
                        }
                    }
                    /*File del = new File(address + name);
                    boolean b = del.delete();
                    if (!b) {
                        logger.error("del file error:" + address + name);
                    }*/
                } catch (IOException ex) {
                    logger.error(ex.getMessage());
                    throw new RuntimeException(ex);
                } finally {
                    if (br != null)
                        br.close();
                }
            }
        }
        try {
            for (String key : ftmap.keySet()) {
                FileText ft = ftmap.get(key);
                sendFile(ft);
            }
        } catch (Exception e) {
            logger.error(e.getMessage());
            throw new RuntimeException(e);
        }
        for(String name:filelist) {
            File del = new File(address + name);
            boolean b = del.delete();
            if (!b) {
                logger.error("del file error:" + address + name);
            }
        }
    }
    private static FileText analyFile(String str) {
        FileText ft = new FileText();
        String[] files = new String[3];
        ft.setTime(str.substring(0, str.indexOf("---")));
        ft.setType(str
                .substring(str.indexOf("---") + 3, str.lastIndexOf("---")));
        ft.setFilepath(str.substring(str.lastIndexOf("---") + 3, str.length()));
        return ft;
    }
    public static long diff(String fileday) {
        //diff minute
        long diff = 0;
        try {
            diff = (System.currentTimeMillis() - format.parse(fileday).getTime())/1000/60;
        } catch (ParseException e) {
            logger.error(e.getMessage());
        }
        return diff;
    }
    private static void sendFile(FileText files){

        TransFile tf = new TransFile();
        File tfile = new File(files.getFilepath());
        String fileName = tfile.getName();
        tf.setName(fileName);
        String path = tfile.getPath().replace(montior_address, "");
        path = getDirName(path);
        if (path.indexOf(File.separator) > -1) {
            tf.setPath(path.substring(0, path.lastIndexOf(File.separator)));
        } else {
            tf.setPath(path);
        }
        if (files.getType().equals("ENTRY_MODIFY")) {
            tf.setCode(CodeEnum.ADDFILE.getCode());
            tf.setFile(tfile);
            try (InputStream is = new FileInputStream(tfile)) {
                tf.setMd5(DigestUtils.md5Hex(is));
            } catch (FileNotFoundException e) {
                logger.error("file not find:" + e.getMessage());
                throw new RuntimeException(e.getMessage());
            } catch (Exception e){
                logger.error("md5 io error "+e.getMessage());
                throw new RuntimeException(e.getMessage());
            }
        } else {
            tf.setCode(CodeEnum.DELFILE.getCode());
        }
        tf.setType(getSuffix(fileName));
        new FileClient().connect(port,server_ip, tf);
    }

    public static String getDirName(String filePath) {
        if (StringUtils.isBlank(filePath))
            return filePath;
        int lastSep = filePath.lastIndexOf(File.separator);
        return lastSep == -1 ? "" : filePath.substring(0, lastSep + 1);
    }

    private static String getSuffix(String fileName) {
        String fileType = "";
        if (fileName.lastIndexOf(".") != -1) {
            fileType = fileName.substring(fileName.lastIndexOf("."),
                    fileName.length());
        }
        return fileType;
    }

}
