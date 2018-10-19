package com.runvision.utils;

import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeUtils {
    //十位时间戳字符串转小时分钟秒
    public static String Hourmin(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("HH:mm:ss");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }
    //十位时间戳字符串转年月
    public static String YearMon(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;

    }
    //十位时间戳字符串转月日
    public static String MonthDay(String time) {
        SimpleDateFormat sdr = new SimpleDateFormat("MM月dd日");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;

    }
    //获取13位字符串格式的时间戳
    public static String getTime13() {

        long time = System.currentTimeMillis();

        String str13 = String.valueOf(time);

        return str13;

    }
    //获取10位字符串格式的时间戳
    public static String getTime() {

        long time = System.currentTimeMillis() / 1000;//获取系统时间的10位的时间戳

        String str = String.valueOf(time);

        return str;

    }
    public static String YMDHMS(String time){
        SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH:mm:ss");
        @SuppressWarnings("unused")
        long lcc = Long.valueOf(time);
        int i = Integer.parseInt(time);
        String times = sdr.format(new Date(i * 1000L));
        return times;
    }

    public static String getYearMonth(){
        SimpleDateFormat formatter   =  new  SimpleDateFormat("yyyy-MM-dd");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }

    public static String getCurrentTime(){
        SimpleDateFormat formatter   =  new  SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date curDate = new Date(System.currentTimeMillis());
        String str = formatter.format(curDate);
        return str;
    }

}
