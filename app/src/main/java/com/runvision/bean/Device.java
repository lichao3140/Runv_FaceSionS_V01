package com.runvision.bean;

/**
 * 考勤终端注册
 */
public class Device {

    private String inscode;//培训机构编号
    private int termtype;//计时终端类型
    private String vender;//生产厂家
    private String model;//终端型号
    private String gps;//GPS坐标
    private String imei;//imei地址

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

    public String getimei() {
        return imei;
    }

    public void setimei(String imei) {
        this.imei = imei;
    }

    public Device(String inscode, int termtype, String vender, String model, String gps, String imei) {
        this.inscode = inscode;
        this.termtype = termtype;
        this.vender = vender;
        this.model = model;
        this.gps = gps;
        this.imei = imei;
    }

    @Override
    public String toString() {
        return "Device{" +
                "inscode='" + inscode + '\'' +
                ", termtype=" + termtype +
                ", vender='" + vender + '\'' +
                ", model='" + model + '\'' +
                ", gps='" + gps + '\'' +
                ", imei='" + imei + '\'' +
                '}';
    }

}
