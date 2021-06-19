package com.fsc.fscclient;

import com.fsc.fscclient.core.FileOper;
import com.fsc.fscclient.enums.Content;
import com.fsc.fscclient.util.PropertiesUtils;

import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class FscClientMain {

    public static void main(String[] args) {
        int minute = PropertiesUtils.getIntValue(Content.MINUTE);
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                try {
                    FileOper.read();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 1000 * 60 * minute);

    }

}
