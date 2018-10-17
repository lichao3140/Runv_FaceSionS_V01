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

    public static String PRIVATE_KEY="privateKey";
    public static String DEVNUM="devnum";

    public static String CARD_PATH= Environment.getExternalStorageDirectory() + "/FaceAndroid/Card/";
    public static String FACE_PATH= Environment.getExternalStorageDirectory() + "/FaceAndroid/Face/";
    public static final String TEMP_DIR = "FaceTemplate";

    /**
     * 1对多比对
     */
    public static final String KEY_ONEVSMORESCORE = "oneVsMoreScore";

    /**
     *
     */
    public static final int ONEVSMORE_SCORE = 68;
    public static int ONE_VS_MORE_TIMEOUT_NUM = 0;
    public static int ONE_VS_MORE_TIMEOUT_MAXNUM = 50;

    private static String SERVER_IP = "http://124.133.246.162:38071/jsxt/api/";
    public static String REGISTER =  SERVER_IP + "device?";//考勤终端注册接口
    public static String LOGIN = SERVER_IP + "devicelogin?";//考勤终端登录接口
    public static String FACE_WITNESS = SERVER_IP + "faceVerify?";//人脸验证接口
    public static String PARAMETER = SERVER_IP + "atndquery?";//考勤参数
    public static String STULOGIN = SERVER_IP + "stulogin?";//培训登录
    public static String STULOGOUT = SERVER_IP + "stulogout?";//培训登出

    public static Boolean oneVsMoreFlag = false;





}
