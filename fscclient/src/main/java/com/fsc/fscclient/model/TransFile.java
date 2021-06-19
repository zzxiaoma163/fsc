package com.fsc.fscclient.model;

import java.io.File;
import java.io.Serializable;

public class TransFile implements Serializable {

    private static final long serialVersionUID = 1L;
    private int code; // 传输代码
    private File file;// 文件
    private String path; // 文件全路径
    private String name;// 文件名
    private long start;// 开始位置
    private byte[] bytes;// 文件字节数组
    private int end;// 结尾位置
    private String md5; // 文件的MD5值
    private String type; // 文件类型
    private long size; // 文件总长度

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getStart() {
        return start;
    }

    public void setStart(long start) {
        this.start = start;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public byte[] getBytes() {
        return bytes;
    }

    public void setBytes(byte[] bytes) {
        this.bytes = bytes;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

}
