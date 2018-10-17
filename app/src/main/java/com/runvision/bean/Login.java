package com.runvision.bean;

public class Login {

    private String sign;
    private String devnum;
    private String username;
    private String passwd;
    private String ts;

    public Login(String sign, String devnum, String username, String ts) {
        this.sign = sign;
        this.devnum = devnum;
        this.username = username;
        this.ts = ts;
    }

    public Login(String sign, String devnum, String username, String passwd, String ts) {
        this.sign = sign;
        this.devnum = devnum;
        this.username = username;
        this.passwd = passwd;
        this.ts = ts;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }

    public String getDevnum() {
        return devnum;
    }

    public void setDevnum(String devnum) {
        this.devnum = devnum;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswd() {
        return passwd;
    }

    public void setPasswd(String passwd) {
        this.passwd = passwd;
    }

    public String getTs() {
        return ts;
    }

    public void setTs(String ts) {
        this.ts = ts;
    }

    @Override
    public String toString() {
        return "Login{" +
                "sign='" + sign + '\'' +
                ", devnum='" + devnum + '\'' +
                ", username='" + username + '\'' +
                ", passwd='" + passwd + '\'' +
                ", ts='" + ts + '\'' +
                '}';
    }
}
