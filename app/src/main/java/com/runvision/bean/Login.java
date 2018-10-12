package com.runvision.bean;

public class Login {

    private String devnum;
    private String username;
    private String passwd;

    public Login(String devnum, String username, String passwd) {
        this.devnum = devnum;
        this.username = username;
        this.passwd = passwd;
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

    @Override
    public String toString() {
        return "Login{" +
                "devnum='" + devnum + '\'' +
                ", username='" + username + '\'' +
                ", passwd='" + passwd + '\'' +
                '}';
    }
}
