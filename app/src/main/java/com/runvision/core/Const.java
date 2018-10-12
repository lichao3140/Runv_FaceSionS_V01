package com.runvision.core;

import android.os.Environment;

public class Const {
    /**
     * 实际相机显示的大小
     */
    public static int Panel_width = 0;
    /**
     * 1：N的比对时过滤人脸的角度
     */
    public static final int ONE_VS_MORE_OFFSET = 15;
    /**
     * 是否打开1：N的线程   默认打开
     */
    public static boolean openOneVsMore = true;
    /**
     * FACE_SCORE 人脸比分阈值
     */
    public static final int FACE_SCORE = 53;

    /**
     * 相机流的高
     */
    public static final int PRE_HEIGTH = 480;
    /**
     * 相机流的宽
     */
    public static final int PRE_WIDTH = 640;
    /**
     * 最大比对次数
     */
    public static final int MAX_COMPER_NUM = 5;

    public final static int CARD_WIDTH = 102;//身份证图片的宽度

    public final static int CARD_HEIGTH = 126;//身份证图片的高度


    public final static int UPDATE_UI=1000;

    public static String CARD_PATH= Environment.getExternalStorageDirectory() + "/FaceAndroid/Card/";
    public static String FACE_PATH= Environment.getExternalStorageDirectory() + "/FaceAndroid/Face/";

    private static String SERVER_IP = "http://124.133.246.162:38071/jsxt/api/";
    public static String REGISTER =  SERVER_IP + "device?";//考勤终端注册接口
    public static String LOGIN = SERVER_IP + "devicelogin?";//考勤终端登录接口
    public static String FACE_WITNESS = SERVER_IP + "FaceVerify?";//人脸验证接口
    public static String PARAMETER = SERVER_IP + "Atndquery?";//考勤参数查询接口
    public static String STULOGIN = SERVER_IP + "Stulogin?";//学员培训登录接口
    public static String STULOGOUT = SERVER_IP + "tulogout?";//学员培训登出接口




    public static String PRI = "MIICeAIBADANBgkqhkiG9w0BAQEFAASCAmIwggJeAgEAAoGBAMJWg+jgnW5X4CHvTVDMcnBsFAdh"+
            "WNQQMjfOOie1KW+ZpPOqZ4tiUVsfbVReEkET1jEfhGfViwtmgyULonIcfjRFhESZCIDZeaOMikQ3"+
            "gL9kf7z3Yza2bxqIgLhb4XTu3OudRHbuo9uytwoDnKtx3lT6XCixklH8AKtLgDL9WV2dAgMBAAEC"+
            "gYEAnfWeUGpJ7EeHCW4uBM+48QIYIYuRnQTxHIUGpgHNhUV4WwoWEag/gnZ/8gRoh/bssY7xm0hq"+
            "NUEEtdbIGkJonP13O/QAkADgSu/OpZV5H/xP+RFhBaN3OA40/ItOOO1MIb+BwcZrDoZUkoXv1SWi"+
            "vUv2QUcAccNg2gbGYwv4A40CQQD4WQHyiviR8OpSlc5BxTg37SKapmXOcQtmpPOvlK99zftSE79+"+
            "5mIYfHsSp1HSpfk+VWVt2G7BqZOsNvXZRb77AkEAyFN5JtnIzgSgo7LpooWErYRnoqgblzyKpN1C"+
            "XgT2QGnub4PP7khKbUjSmrsoAmwFXvWiPOX/EF0I/Ezgr29SRwJBAKLW2ewLK4mmCj80cxW1F3O0"+
            "TahRyxdeEDexmQdb2uYGle/vevTeYxvjI1/Lzl7s7Uzt+Z/Y9maNpoKZVwKsNNkCQQCilczwUTV+"+
            "r5bJBX5Fn2PtiFasVw/9kO9dmw4wTIqoANG5xBtQY2+0frQfTOLOBGnfhjCkiG6ZE0klrCd3ezwl"+
            "AkBS3Ep38UyAufPmXKRS8l91BRAw4x+Obk8io3PuuRUi+5JwmR9VRzlCaJhbQmTjtXTeg1f2eIBg"+
            "+KPvP/5vfOeE";

    public static String PUB = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDCVoPo4J1uV+Ah701QzHJwbBQHYVjUEDI3zjon"+
            "tSlvmaTzqmeLYlFbH21UXhJBE9YxH4Rn1YsLZoMlC6JyHH40RYREmQiA2XmjjIpEN4C/ZH+892M2"+
            "tm8aiIC4W+F07tzrnUR27qPbsrcKA5yrcd5U+lwosZJR/ACrS4Ay/VldnQIDAQAB";
}
