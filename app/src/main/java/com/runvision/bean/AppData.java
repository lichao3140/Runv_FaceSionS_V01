package com.runvision.bean;

import android.graphics.Bitmap;

public class AppData {
    private String cardNo;// 证件号
    private String name;// 姓名
    private int gender;// 性别

    private Bitmap faceBmp;//人脸图片
    private Bitmap cardBmp;//身份证图片

    private String inscode;//培训机构编号
    private int termtype;//计时终端类型
    private String vender;//生产厂家
    private String model;//终端型号
    private String gps;//GPS坐标
    private String mac;//Mac地址

    private int errorcode;//返回码
    private String devnum;//终端编号
    private String private_rsa;//RSA私钥

    private String timestamp;//时间戳

    private String starttime="08:00:00";//课堂开始时间
    private String endtime="24:00:00";//课堂关闭时间

    private String instarttime="09:30:00";//签到开始时间
    private String inendtime="20:30:00";//签到结束时间
    private String outstarttime="21:10:00";//签退开始时间
    private String outendtime="22:30:00";//签退结束时间
    private int interval;//签到间隔(秒)

    private String cardtype;//证件类型

    private String time;//学员登录时间
    private String stucode;//学员编号
    private String imgstr;//图片base64串

    private String picName;

    private String user;
    private String password;

    private String classcode;//课堂编码
    private String sn;//学员考勤登录序列号
    private String studentName;//学员姓名
    private String period;//学习时长


    public String getPeriod() {
        return period;
    }

    public void setPeriod(String period) {
        this.period = period;
    }

    public String getClasscode() {
        return classcode;
    }

    public void setClasscode(String classcode) {
        this.classcode = classcode;
    }

    public String getSn() {
        return sn;
    }

    public void setSn(String sn) {
        this.sn = sn;
    }

    public String getStudentName() {
        return studentName;
    }

    public void setStudentName(String studentName) {
        this.studentName = studentName;
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPicName() {
        return picName;
    }

    public void setPicName(String picName) {
        this.picName = picName;
    }

    public String getCardNo() {
        return cardNo;
    }

    public void setCardNo(String cardNo) {
        this.cardNo = cardNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getGender() {
        return gender;
    }

    public void setGender(int gender) {
        this.gender = gender;
    }

    public Bitmap getFaceBmp() {
        return faceBmp;
    }

    public void setFaceBmp(Bitmap faceBmp) {
        this.faceBmp = faceBmp;
    }

    public Bitmap getCardBmp() {
        return cardBmp;
    }

    public void setCardBmp(Bitmap cardBmp) {
        this.cardBmp = cardBmp;
    }

    public String getInscode() {
        return inscode;
    }

    public void setInscode(String inscode) {
        this.inscode = inscode;
    }

    public int getTermtype() {
        return termtype;
    }

    public void setTermtype(int termtype) {
        this.termtype = termtype;
    }

    public String getVender() {
        return vender;
    }

    public void setVender(String vender) {
        this.vender = vender;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }

    public int getErrorcode() {
        return errorcode;
    }

    public void setErrorcode(int errorcode) {
        this.errorcode = errorcode;
    }

    public String getDevnum() {
        return devnum;
    }

    public void setDevnum(String devnum) {
        this.devnum = devnum;
    }

    public String getPrivate_rsa() {
        return private_rsa;
    }

    public void setPrivate_rsa(String private_rsa) {
        this.private_rsa = private_rsa;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getStarttime() {
        return starttime;
    }

    public void setStarttime(String starttime) {
        this.starttime = starttime;
    }

    public String getEndtime() {
        return endtime;
    }

    public void setEndtime(String endtime) {
        this.endtime = endtime;
    }

    public String getInstarttime() {
        return instarttime;
    }

    public void setInstarttime(String instarttime) {
        this.instarttime = instarttime;
    }

    public String getInendtime() {
        return inendtime;
    }

    public void setInendtime(String inendtime) {
        this.inendtime = inendtime;
    }

    public String getOutstarttime() {
        return outstarttime;
    }

    public void setOutstarttime(String outstarttime) {
        this.outstarttime = outstarttime;
    }

    public String getOutendtime() {
        return outendtime;
    }

    public void setOutendtime(String outendtime) {
        this.outendtime = outendtime;
    }

    public int getInterval() {
        return interval;
    }

    public void setInterval(int interval) {
        this.interval = interval;
    }

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getStucode() {
        return stucode;
    }

    public void setStucode(String stucode) {
        this.stucode = stucode;
    }

    public String getImgstr() {
        return imgstr;
    }

    public void setImgstr(String imgstr) {
        this.imgstr = imgstr;
    }

    public static AppData mAppData = new AppData();

    public static AppData getAppData() {
        return mAppData;
    }

}
