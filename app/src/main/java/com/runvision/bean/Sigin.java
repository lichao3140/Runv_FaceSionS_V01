package com.runvision.bean;

import android.graphics.Bitmap;

public class Sigin {

    private String name;
    private String gender;
    private String cardNo;
    private String sigintime;
    private Bitmap imageId;

    public Sigin(String name, Bitmap imageId, String gender, String cardNo, String sigintime) {
        this.name = name;
        this.imageId = imageId;
        this.gender = gender;
        this.cardNo = cardNo;
        this.sigintime = sigintime;
    }

    public String getName() {
        return name;
    }

    public String getGender() {
        return gender;
    }

    public String getCardNo() {
        return cardNo;
    }

    public String getSigintime() {
        return sigintime;
    }

    public Bitmap getImageId() {
        return imageId;
    }


}
