package com.runvision.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class TimeCompareUtil {

    //获取当前系统时间
    private Date currentTime = null;//currentTime就是系统当前时间
    //定义时间的格式
    private DateFormat fmt = new SimpleDateFormat("HH:mm");
    private Date strbeginDate = null;//起始时间
    private Date strendDate = null;//结束时间
    private boolean range = false;

    public Boolean TimeCompare(String strbeginTime, String strendTime, String currentTime1) {
        try {
            strbeginDate = fmt.parse(strbeginTime);//将时间转化成相同格式的Date类型
            strendDate = fmt.parse(strendTime);
            currentTime = fmt.parse(currentTime1);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        if ((currentTime.getTime() - strbeginDate.getTime()) > 0 && (strendDate.getTime() - currentTime.getTime()) > 0) {//使用.getTime方法把时间转化成毫秒数,然后进行比较
            range = true;
            //  ToastUtil.MyToast(UnlockActivity.this, "当前时间在范围内");
        } else {
            range = false;
            //  ToastUtil.MyToast(UnlockActivity.this, "您的操作时间已到期,请重新申请操作时间");
        }
        return range;
    }

    public static String getSystemTime() {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm");// HH:mm:ss
        //获取当前时间
        Date date = new Date(System.currentTimeMillis());
        String currentTime = simpleDateFormat.format(date);
        return currentTime;
    }
}
