package com.runvision.bean;

import android.graphics.Bitmap;

public class Sign {

    private String name;
    private String gender;
    private String cardNo;
    private String signtime;
    private Bitmap imageId;

    public Sign(String name, Bitmap imageId, String gender, String cardNo, String signtime) {
        this.name = name;
        this.imageId = imageId;
        this.gender = gender;
        this.cardNo = cardNo;
        this.signtime = signtime;
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

    public String getSigntime() {
        return signtime;
    }

    public Bitmap getImageId() {
        return imageId;
    }


}
