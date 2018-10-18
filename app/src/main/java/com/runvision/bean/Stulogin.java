package com.runvision.bean;

public class Stulogin {
    private String sign;
    /**
     * 终端编号
     */
    private String devnum;
    /**
     * 学员登录时间
     */
    private String time;
    /**
     * 学员编号
     */
    private String stucode;
    /**
     * 证件类型
     */
    private String cardtype;
    /**
     * Gps坐标
     */
    private String gps;
    /**
     * 图片base64串
     */
    private String imgstr;
    /**
     * 课堂编码
     */
    private String classcode;
    /**
     * 学员考勤登录序列号
     */
    private String sn;
    /**
     * 学员姓名
     */
    private String studentName;

    private int period;

    private String ts;

    /**
     * 登入
     * @param sign
     * @param devnum
     * @param time
     * @param stucode
     * @param cardtype
     * @param gps
     * @param imgstr
     * @param classcode
     * @param sn
     * @param studentName
     * @param ts
     */
    public Stulogin(String sign, String devnum, String time, String stucode, String cardtype, String gps, String imgstr, String classcode, String sn, String studentName, String ts) {
        this.sign = sign;
        this.devnum = devnum;
        this.time = time;
        this.stucode = stucode;
        this.cardtype = cardtype;
        this.gps = gps;
        this.imgstr = imgstr;
        this.classcode = classcode;
        this.sn = sn;
        this.studentName = studentName;
        this.ts = ts;
    }

    /**
     * 登出
     * @param sign
     * @param devnum
     * @param time
     * @param stucode
     * @param cardtype
     * @param gps
     * @param imgstr
     * @param classcode
     * @param sn
     * @param period
     * @param studentName
     * @param ts
     */
    public Stulogin(String sign, String devnum, String time, String stucode, String cardtype, String gps, String imgstr, String classcode, String sn, int period, String studentName, String ts) {
        this.sign = sign;
        this.devnum = devnum;
        this.time = time;
        this.stucode = stucode;
        this.cardtype = cardtype;
        this.gps = gps;
        this.imgstr = imgstr;
        this.classcode = classcode;
        this.sn = sn;
        this.period = period;
        this.studentName = studentName;
        this.ts = ts;
    }

    public String getSign_str() {
        return sign;
    }

    public void setSign_str(String sign_str) {
        this.sign = sign_str;
    }

    public String getDevnum() {
        return devnum;
    }

    public void setDevnum(String devnum) {
        this.devnum = devnum;
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

    public String getCardtype() {
        return cardtype;
    }

    public void setCardtype(String cardtype) {
        this.cardtype = cardtype;
    }

    public String getGps() {
        return gps;
    }

    public void setGps(String gps) {
        this.gps = gps;
    }

    public String getImgstr() {
        return imgstr;
    }

    public void setImgstr(String imgstr) {
        this.imgstr = imgstr;
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

    public int getPeriod() {
        return period;
    }

    public void setPeriod(int period) {
        this.period = period;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "Stulogin{" +
                "sign_str='" + sign + '\'' +
                ", devnum='" + devnum + '\'' +
                ", time='" + time + '\'' +
                ", stucode='" + stucode + '\'' +
                ", cardtype='" + cardtype + '\'' +
                ", gps='" + gps + '\'' +
                ", imgstr='" + imgstr + '\'' +
                ", classcode='" + classcode + '\'' +
                ", sn='" + sn + '\'' +
                ", studentName='" + studentName + '\'' +
                '}';
    }

}
