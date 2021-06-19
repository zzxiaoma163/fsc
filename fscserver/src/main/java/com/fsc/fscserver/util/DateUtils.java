package com.fsc.fscserver.util;

import java.util.Date;

public class DateUtils {
    public static int differentDays(long time1, long time2)
    {
        int days = (int) ((time2 - time1) / (1000*3600*24));
        return days;
    }
}
