package com.runvision.firs_facesions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.runvision.bean.AppData;
import com.runvision.bean.Device;
import com.runvision.bean.DeviceResponse;
import com.runvision.bean.Login;
import com.runvision.core.Const;
import com.runvision.myview.FaceFrameView;
import com.runvision.myview.MyCameraSuf;
import com.runvision.thread.ToHttpThread;
import com.runvision.utils.JsonTools;
import com.runvision.utils.LocationUtils;
import com.runvision.utils.MACUtil;
import com.runvision.utils.RSAUtils;
import com.runvision.utils.SharedPreferencesHelper;
import com.runvision.utils.TimeCompareUtil;
import com.runvision.utils.TimeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import org.json.JSONException;
import org.json.JSONObject;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;

public class LandActivity extends Activity implements View.OnClickListener {

    private View login;
    private View reg;
    private Button loginbtn;
    private Button regbtn;
    private EditText et_user;
    private EditText et_password;
    private EditText ed_mechanism;
    private EditText ed_type;
    private EditText ed_manufactor;
    private EditText ed_model;
    private EditText ed_mac;
    private Button okbtn;
    private Button cancelbtn;

    public  FaceFrameView myFaceFrameView;
    public MyCameraSuf myCameraView;
    public Location location;
    private SharedPreferencesHelper sharedPreferencesHelper;

    private JsonTools mJsonTools = new JsonTools();

    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        // 隐藏下面的虚拟按键
        hideBottomUIMenu();
        setContentView(R.layout.activity_land);
        mContext = this;
        location = LocationUtils.getInstance(LandActivity.this).showLocation();
        if (location != null) {
            String address = "纬度：" + location.getLatitude() + "经度：" + location.getLongitude();
            Log.i("lichao", "address:" + address);
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(LandActivity.this, "deviceInfo");
        initview();
       // myFaceFrameView = (FaceFrameView) findViewById(R.id.myFaceFrameView);
        // myCameraView = (MyCameraSuf) findViewById(R.id.myCameraSurfaceView);
      //  myCameraView.openCamera();

    }

    @Override
    public void onDestroy() {
        super.onDestroy(); // Always call the superclass
        android.os.Debug.stopMethodTracing();
        LocationUtils.getInstance(LandActivity.this).removeLocationUpdatesListener();
    }

    @SuppressLint("NewApi")
    protected void hideBottomUIMenu() {
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void initview() {
        login=(View)findViewById(R.id.login);
        loginbtn=(Button)login.findViewById(R.id.loginbtn);
        regbtn=(Button)login.findViewById(R.id.regbtn);
        et_user=(EditText)login.findViewById(R.id.et_user);
        et_password=(EditText)login.findViewById(R.id.et_password);
        regbtn.setOnClickListener(this);
        loginbtn.setOnClickListener(this);

        reg=(View)findViewById(R.id.reg);
        ed_mechanism=(EditText)reg.findViewById(R.id.ed_mechanism);
        ed_type=(EditText)reg.findViewById(R.id.ed_type);
        ed_manufactor=(EditText)reg.findViewById(R.id.ed_manufactor);
        ed_model=(EditText)reg.findViewById(R.id.ed_model);
        ed_mac=(EditText)reg.findViewById(R.id.ed_mac);
        okbtn=(Button)reg.findViewById(R.id.okbtn);
        cancelbtn=(Button)reg.findViewById(R.id.cancelbtn);
        okbtn.setOnClickListener(this);
        cancelbtn.setOnClickListener(this);

        ed_mac.setText(MACUtil.getLocalMacAddressFromWifiInfo(mContext));
        ed_mechanism.setText("3225974581615749");
        ed_type.setText("1");
        ed_model.setText("test0001");
        ed_manufactor.setText("山东济南");

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.regbtn:
                login.setVisibility(View.GONE);
                reg.setVisibility(View.VISIBLE);
                break;
            case R.id.loginbtn:
//                AppData.getAppData().setUser(et_user.getText().toString());
//                AppData.getAppData().setPassword(et_password.getText().toString());
//                String postdate1 = mJsonTools.parseJSONWithString(2);
//                ToHttpThread mPostDateThread1 = new ToHttpThread(Const.LOGIN, postdate1);
//                mPostDateThread1.start();
//                try {
//                    Thread.sleep(500);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                myCameraView.releaseCamera();
                try {
                    login();
                } catch (Exception e) {
                    e.printStackTrace();
                }
//                Intent intent = new Intent(LandActivity.this, MainActivity.class);
//                startActivity(intent);
//                finish();
                break;
            case R.id.okbtn:
//                Log.i("lichao", "url:" + Const.REGISTER + "ts=" + TimeUtils.getTime13());
                regin();
                break;
            case R.id.cancelbtn:
                //返回登陆界面
                login.setVisibility(View.VISIBLE);
                reg.setVisibility(View.GONE);
                break;
            default:
                break;
        }
    }

    private void login () throws Exception {
        // sign_str=devnum+username+passwd+ts加密后的字符串
        String privateKey = sharedPreferencesHelper.getSharedPreference("privateKey", "").toString().trim();
        String devnum = sharedPreferencesHelper.getSharedPreference("devnum", "").toString().trim();
        String sign = devnum + et_user.getText().toString().trim() + et_password.getText().toString().trim() + TimeUtils.getTime13();
        byte[] ss = sign.toString().getBytes();
        String sign_str = RSAUtils.sign(ss, privateKey);
        Log.i("lichao", "url:" + Const.LOGIN + "ts=" + TimeUtils.getTime13() + "&sign=" + sign_str);
        OkHttpUtils.postString()
                .url(Const.LOGIN + "ts=" + TimeUtils.getTime13() + "&sign=" + sign_str)
                .content(new Gson().toJson(new Login(devnum,
                        et_user.getText().toString().trim(),
                        et_password.getText().toString())))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("lichao", "faile:" + call.request().body().toString());
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("lichao", "success:" + response.toString());
                    }
                });
    }

    private void regin() {
        OkHttpUtils.postString()
                .url(Const.REGISTER + "ts=" + TimeUtils.getTime13())
                .content(new Gson().toJson(new Device(
                        ed_mechanism.getText().toString().trim(),
                        Integer.valueOf(ed_type.getText().toString()).intValue(),
                        ed_manufactor.getText().toString().trim(),
                        ed_model.getText().toString().trim(),
                        "15|56",
                        ed_mac.getText().toString())))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Log.e("lichao", "faile:" + call.request().body().toString());
                        Toasty.error(LandActivity.this, "注册失败", Toast.LENGTH_SHORT, true).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("lichao", "success:" + response.toString());
                        Gson gson = new Gson();
                        DeviceResponse gsonData = gson.fromJson(response.toString(), DeviceResponse.class);
                        String privateKey = gsonData.getData().getPrivateKey();
                        String devnum = String.valueOf(gsonData.getData().getDevnum());
                        sharedPreferencesHelper.put("privateKey", privateKey);
                        sharedPreferencesHelper.put("devnum",devnum);
                        Toasty.success(LandActivity.this, "注册成功", Toast.LENGTH_SHORT, true).show();
                    }
                });
        //2.返回登陆界面（记录终端型号）
    }
}
