package com.runvision.bean;

/**
 * 考勤
 */
public class Atnd {

    private String sign;
    private String inscode;
    private String devnum;
    private String ts;

    public Atnd(String sign, String inscode, String devnum, String ts) {
        this.sign = sign;
        this.inscode = inscode;
        this.devnum = devnum;
        this.ts = ts;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getInscode() {
        return inscode;
    }

    public void setInscode(String inscode) {
        this.inscode = inscode;
    }

    public String getDevnum() {
        return devnum;
    }

    public void setDevnum(String devnum) {
        this.devnum = devnum;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }
}
