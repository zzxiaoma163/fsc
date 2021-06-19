package com.fsc.fscclient.model;

import java.io.Serializable;

public class BackInfo implements Serializable {


    /**
     *
     */
    private static final long serialVersionUID = -1425307876096494974L;


    public BackInfo() {

    }

    public BackInfo(long start) {
        super();
        this.start = start;
        this.end = true;
        this.progress = 100;
    }

    public BackInfo(long start, String md5, long progress) {
        super();
        this.start = start;
        this.md5 = md5;
        this.end = false;
        this.progress = (int) progress;
    }

    private long start;

    private String md5;

    private boolean end;

    private int progress;

    public long getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public boolean isEnd() {
        return end;
    }

    public void setEnd(boolean end) {
        this.end = end;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("progress:");
        sb.append(progress);
        sb.append("\t\tstart:");
        sb.append(start);
        return sb.toString();

    }

}
