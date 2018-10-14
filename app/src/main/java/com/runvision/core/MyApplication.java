/*
 * Copyright 2017, Tnno Wu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.runvision.core;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.ColorInt;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.face.sv.FaceDM2016;
import com.face.sv.FaceDetect;
import com.face.sv.FaceLive;
import com.face.sv.FaceRecognize;
import com.face.sv.ImageUtil;
import com.runvision.bean.UnCeHandler;
import com.runvision.utils.LogToFile;
import com.zhy.http.okhttp.OkHttpUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

import es.dmoral.toasty.Toasty;
import okhttp3.OkHttpClient;

/**
 * Created by Tnno Wu on 2017/4/8.
 */

public class MyApplication extends Application {
    private static Context context;
    ArrayList<Activity> list = new ArrayList<Activity>();
    public static String tempDir = null;
    public static String libDir = null;
    public static String userDataPath = null;
    public static String saveImagePath = null;
    public static boolean mInitStatus = false;
    public static ImageUtil mImgUtil = new ImageUtil();
    public static FaceDM2016 mDM2016 = new FaceDM2016();
    public static FaceDetect mDetect = new FaceDetect();

    public static FaceLive mLive = new FaceLive();
    public static FaceRecognize mRecognize = new FaceRecognize();
    /**
     * 用户最大数量
     */
    public final static int MAX_USER_NUMBER = 10000;

    public void init() {
        UnCeHandler catchExcep = new UnCeHandler(this);
        Thread.setDefaultUncaughtExceptionHandler(catchExcep);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        String serlia = getSerialNumber();
        if (serlia.equals("") || serlia.length() < 4 || !serlia.substring(0, 4).equals("R50A")) {
            LogToFile.i("MyApplication", "没有SN序列号");
            finishActivity();
        }
        LogToFile.init(this);
        setConfig();
        initFaceModel();
        initSdkLib();
        initHttp();
    }

    private void initHttp() {
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10000L, TimeUnit.MILLISECONDS)
                .readTimeout(10000L, TimeUnit.MILLISECONDS)
                .build();
        OkHttpUtils.initClient(okHttpClient);
    }

    public static Context getContext() {
        return context;
    }

    public void removeActivity(Activity a) {
        if (list.contains(a)) {
            list.remove(a);
            if (a != null) {
                a.finish();
            }
        }
    }

    public void addActivity(Activity a) {
        list.add(a);
    }

    public void finishActivity() {
        for (Activity activity : list) {
            if (null != activity) {
                activity.finish();
            }
        }
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public String getSerialNumber() {
        String serial = "";
        try {
            Class<?> c = Class.forName("android.os.SystemProperties");
            Method get = c.getMethod("get", String.class);
            serial = (String) get.invoke(c, "ro.serialno");

        } catch (Exception e) {
            Log.i("error", e.getMessage());
        }
        return serial;
    }

    private void setConfig() {
        String dataPath = this.getFilesDir().getAbsolutePath();
        // 算法运行所需要的缓存目录，需要读写权限。
        tempDir = "/mnt/sdcard/SysConfig/cache/";
        // 对应initFaceModel()模型文件创建目录，需要读取权限。
        libDir = "/mnt/sdcard/SysConfig/model/";
        saveImagePath = "/mnt/sdcard/SysConfig/image/";
        System.out.println("tempDir:" + tempDir + " libDir:" + libDir);
        LogToFile.i(TAG, "tempDir:" + tempDir + " libDir:" + libDir);
        // 初始化目录
        createDir(tempDir);
        createDir(libDir);
        createDir(saveImagePath);
        userDataPath = dataPath + "/userData";
        createDir(userDataPath);

    }

    public void createDir(String dir) {
        File file = new File(dir);
        if (!file.exists() || !file.isDirectory()) {
            file.mkdirs();
        }
    }

    private void initSdkLib() {
        InitLib initLib = new InitLib();
        initLib.start();
    }

    @SuppressLint("HandlerLeak")
    public Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0) {
                Toast.makeText(context, "算法初始化失败", Toast.LENGTH_SHORT).show();
                LogToFile.e(TAG, "算法初始化失败");
            }
            if (msg.what == 1) {
                LogToFile.i(TAG, "算法初始化成功");
                Toast.makeText(context, "算法初始化成功", Toast.LENGTH_SHORT).show();
            }
        }
    };

    @Override
    public void onTerminate() {
        super.onTerminate();
        mDetect.releaseFaceDetectLib();
        mLive.ReleaseFaceLive();
        mRecognize.releaseFaceLibrary();
    }

    class InitLib extends Thread {
        @Override
        public void run() {
            super.run();
            byte[] bts = null;
            int ret = 0;
            //一些读取加密信息授权操作
            bts = mDetect.getDetectSN();
            bts = mDM2016.encodeKeyCode(bts);
            ret = mDetect.checkDetectSN(bts);
            System.out.println("mDetect.checkDetectSN(bts) ret:" + ret);
            //初始化人脸检测算法库
            ret = mDetect.initFaceDetectLib(libDir, tempDir);
            System.out.println("mDetect.initFaceDetectLib(libDir,tempDir, 1) ret:" + ret);
            if (ret != 1) {
                handler.sendEmptyMessage(0);
                mInitStatus = false;
                return;
            }
            //一些读取加密信息授权操作
            bts = mLive.getLiveSN();
            bts = mDM2016.encodeKeyCode(bts);
            ret = mLive.checkLiveSN(bts);
            if (ret != 1) {
                handler.sendEmptyMessage(0);
                mInitStatus = false;
                return;
            }
            System.out.println("mLive.checkLiveSN(bts) ret:" + ret);
            //初始化活体检测算法库
            ret = mLive.InitFaceLive(libDir, tempDir);
            System.out.println("mLive.InitFaceLive(FaceApp.libDir, FaceApp.tempDir) ret:" + ret);
            //一些读取加密信息授权操作
            bts = mRecognize.getFeatureSN();
            bts = mDM2016.encodeKeyCode(bts);
            ret = mRecognize.checkFeatureSN(bts);
            if (ret != 1) {
                handler.sendEmptyMessage(0);
                mInitStatus = false;
                return;
            }
            System.out.println("mRecognize.checkFeatureSN(bts) ret:" + ret);
            // 初始化人脸检测算法库
            ret = mRecognize.initFaceLibrary(libDir, tempDir, userDataPath, MAX_USER_NUMBER);
            System.out.println("mRecognize.initFaceLibrary(FaceApp.libDir, FaceApp.tempDir, FaceApp.userDataPath, MAX_USER_NUMBER) ret:" + ret);
            if (ret != 1) {
                handler.sendEmptyMessage(0);
                mInitStatus = false;
                return;
            }
            handler.sendEmptyMessage(1);
            mInitStatus = true;
        }
    }

    private final static String DETECT_KO_SO = "libTHDetect_ko.so";
    private final static String DETECT_KO = "libTHDetect_ko";
    private final static String POS_KO_SO = "libTHFacialPos_ko.so";
    private final static String POS_KO = "libTHFacialPos_ko";
    private final static String FEATURE_DB30A_KO_SO = "libTHFeature_db30a_ko.so";
    private final static String DB30A_KO = "libTHFeature_db30a_ko";
    private final static String LIVE_VI_SO = "libTHFaceLive_vi.so";
    private final static String LIVE_VI = "libTHFaceLive_vi";

    public void initFaceModel() {
        log("initFaceModel()");
        String mainPath = null;
        String subPath = null;
        File manFile = null;
        File subFile = null;
        FileOutputStream output = null;
        InputStream input = null;
        byte[] bts = new byte[1024];
        int size = 0;
        // 生成libTHDect_dpbin.so文件
        try {
            mainPath = libDir + DETECT_KO_SO;
            manFile = new File(mainPath);
            if (!manFile.exists()) {
                manFile.createNewFile();

                output = new FileOutputStream(manFile, true);
                for (int i = 0; i < 3; i++) {
                    subPath = DETECT_KO + "/" + DETECT_KO + (i + 1);
                    input = this.getClass().getClassLoader().getResourceAsStream("assets/" + subPath);
                    if (input != null) {
                        while ((size = input.read(bts)) != -1) {
                            output.write(bts, 0, size);
                        }
                        input.close();
                    } else {
                        log("AssetManager.open(file) is null. path:" + subPath);
                    }
                }
                output.flush();
                output.close();

            } else {
                log("This model file is exist. file:" + mainPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log("create model file fail. file:" + mainPath);
        }

        // 生成libTHFacialPos_con.so文件
        try {
            mainPath = libDir + POS_KO_SO;
            manFile = new File(mainPath);
            if (!manFile.exists()) {
                manFile.createNewFile();


                output = new FileOutputStream(manFile, true);
                for (int i = 0; i < 1; i++) {
                    subPath = POS_KO + "/" + POS_KO + (i + 1);
                    input = this.getClass().getClassLoader().getResourceAsStream("assets/" + subPath);
                    if (input != null) {
                        while ((size = input.read(bts)) != -1) {
                            output.write(bts, 0, size);
                        }
                        input.close();
                    } else {
                        log("AssetManager.open(file) is null. path:"
                                + subPath);
                    }
                }
                output.flush();
                output.close();

            } else {
                log("This model file is exist. file:" + mainPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log("create model file fail. file:" + mainPath);
        }

        // 生成libTHFeature_db30a.so文件
        try {
            mainPath = libDir + FEATURE_DB30A_KO_SO;
            manFile = new File(mainPath);
            if (!manFile.exists()) {
                manFile.createNewFile();


                output = new FileOutputStream(manFile, true);
                for (int i = 0; i < 25; i++) {
                    subPath = DB30A_KO + "/" + DB30A_KO + (i + 1);
                    input = this.getClass().getClassLoader().getResourceAsStream("assets/" + subPath);
                    if (input != null) {
                        while ((size = input.read(bts)) != -1) {
                            output.write(bts, 0, size);
                        }
                        input.close();
                    } else {
                        log("AssetManager.open(file) is null. path:"
                                + subPath);
                    }
                }
                output.flush();
                output.close();


            } else {
                log("This model file is exist. file:" + mainPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log("create model file fail. file:" + mainPath);
        }

        // 生成libTHFaceLive_vi.so文件
        try {
            mainPath = libDir + LIVE_VI_SO;
            manFile = new File(mainPath);
            if (!manFile.exists()) {
                manFile.createNewFile();

                output = new FileOutputStream(manFile, true);
                for (int i = 0; i < 3; i++) {
                    subPath = LIVE_VI + "/" + LIVE_VI + (i + 1);
                    input = this.getClass().getClassLoader().getResourceAsStream("assets/" + subPath);
                    if (input != null) {
                        while ((size = input.read(bts)) != -1) {
                            output.write(bts, 0, size);
                        }
                        input.close();
                    } else {
                        log("AssetManager.open(file) is null. path:"
                                + subPath);
                    }
                }
                output.flush();
                output.close();

            } else {
                log("This model file is exist. file:" + mainPath);
            }
        } catch (IOException e) {
            e.printStackTrace();
            log("create model file fail. file:" + mainPath);
        }
    }

    private void log(String msg) {
        Log.d(TAG, msg);
    }

    private String TAG = this.getClass().getSimpleName();
}
