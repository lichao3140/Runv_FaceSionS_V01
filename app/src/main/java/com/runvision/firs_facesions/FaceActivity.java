package com.runvision.firs_facesions;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.runvision.bean.FaceVerifyResponse;
import com.runvision.bean.Login;
import com.runvision.core.Const;
import com.runvision.myview.FaceFrameView;
import com.runvision.myview.MyCameraSuf;
import com.runvision.utils.RSAUtils;
import com.runvision.utils.SPUtil;
import com.runvision.utils.TimeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import android_serialport_api.SerialPort;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;
import static com.runvision.core.Const.oneVsMoreFlag;

/**
 * 人脸验证登录
 */
public class FaceActivity extends BaseActivity {

    private Context context;
    private FaceFrameView myFaceFrameView;
    private MyCameraSuf myCameraView;
    private RelativeLayout show_card;
    private TextView loadcardText;

    private UIThread uithread;
    Gson gson = new Gson();
    private int timingnum = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face);
        context = this;
        init();

        if (uithread == null) {
            uithread = new UIThread();
            uithread.start();
        }
    }

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Const.UPDATE_UI://更新UI
                    if(oneVsMoreFlag == true) {
                        oneVsMoreFlag = false;
                        adminLogin();
                    }
                    if (SerialPort.Fill_in_light == false) {
                                SerialPort.openLED();
                            }

                    if (SerialPort.Fill_in_light == true) {   //补光灯
                        timingnum++;
                        if (timingnum >= 100) {
                            Log.i("zhuhuilong", "Fill_in_light:" + SerialPort.Fill_in_light);
                            SerialPort.Fill_in_light = false;
                            timingnum = 0;
                        }
                    }
                    break;
            }
            }
        };

    /**
     * 更新UI标志线程
     */
    private class UIThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(250);
                    Message msg = new Message();
                    msg.what = Const.UPDATE_UI;
                    mHandler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void init() {
        myFaceFrameView = findViewById(R.id.myFaceFrameView);
        myCameraView = findViewById(R.id.myCameraSurfaceView);
        myCameraView.openCamera();

        show_card	= findViewById(R.id.show_card);
        loadcardText = findViewById(R.id.loadcardText);
    }

    private void adminLogin() {
        try {
            String privateKey = SPUtil.getString(Const.PRIVATE_KEY,"");
            String devnum = SPUtil.getString(Const.DEV_NUM,"");
            String username = SPUtil.getString("username", "");
            String ts = TimeUtils.getTime13();
            String sign = devnum + username +ts;
            byte[] ss = sign.getBytes();
            String sign_str = RSAUtils.sign(ss, privateKey);

            OkHttpUtils.postString()
                    .url(Const.FACE_WITNESS + "ts=" + TimeUtils.getTime13() + "&sign=" + sign_str)
                    .content(new Gson().toJson(new Login(sign_str, devnum, username, ts)))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toasty.error(context, getString(R.string.toast_request_error), Toast.LENGTH_LONG, true).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
//                            Log.i("lichao", "success:" + response);
                            if (!response.equals("resource/500")) {
                                FaceVerifyResponse gsonFace = gson.fromJson(response, FaceVerifyResponse.class);
                                if (gsonFace.getErrorcode().equals("0")) {
                                    Intent intent = new Intent(context, CameraActivity.class);
                                    startActivity(intent);
                                    Toasty.success(context, getString(R.string.toast_face_verify_success), Toast.LENGTH_LONG, true).show();
                                    finish();
                                } else {
                                    Toasty.error(context, getString(R.string.toast_face_verify_fail) + gsonFace.getMessage(), Toast.LENGTH_LONG, true).show();
                                }
                            } else {
                                Toasty.error(context, getString(R.string.toast_server_error), Toast.LENGTH_LONG, true).show();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        myCameraView.openCamera();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myCameraView.releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
