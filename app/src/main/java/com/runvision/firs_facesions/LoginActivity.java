package com.runvision.firs_facesions;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.face.sv.FaceInfo;
import com.github.ybq.android.spinkit.style.Wave;
import com.google.gson.Gson;
import com.runvision.bean.Login;
import com.runvision.bean.LoginResponse;
import com.runvision.core.Const;
import com.runvision.core.MyApplication;
import com.runvision.utils.FileUtils;
import com.runvision.utils.IDUtils;
import com.runvision.utils.LogUtil;
import com.runvision.utils.RSAUtils;
import com.runvision.utils.SPUtil;
import com.runvision.utils.SharedPreferencesHelper;
import com.runvision.utils.TimeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * 登录
 * Created by ChaoLi on 2018/10/13 0013 - 12:48
 * Email: lichao3140@gmail.com
 * Version: v1.0
 */
public class LoginActivity extends FragmentActivity {

    @BindView(R.id.logo)
    ImageView logo;
    @BindView(R.id.et_user)
    EditText etUser;
    @BindView(R.id.iv_clean_user)
    ImageView ivCleanUser;
    @BindView(R.id.et_password)
    EditText etPassword;
    @BindView(R.id.clean_password)
    ImageView cleanPassword;
    @BindView(R.id.iv_show_pwd)
    ImageView ivShowPwd;
    @BindView(R.id.btn_login)
    Button btnLogin;
    @BindView(R.id.regist)
    TextView regist;
    @BindView(R.id.forget_password)
    TextView forgetPassword;
    @BindView(R.id.content)
    LinearLayout content;
    @BindView(R.id.scrollView)
    ScrollView scrollView;
    @BindView(R.id.service)
    LinearLayout service;
    @BindView(R.id.root)
    RelativeLayout root;

    private int screenHeight = 0;//屏幕高度
    private int keyHeight = 0; //软件盘弹起后所占高度
    private float scale = 0.6f; //logo缩放比例

    private ProgressBar progressBar;
    private SharedPreferencesHelper faceSP;
    private Context mContext;

    Gson gson = new Gson();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);
        screenHeight = this.getResources().getDisplayMetrics().heightPixels; //获取屏幕高度
        keyHeight = screenHeight / 3;//弹起高度为屏幕高度的1/3
        mContext = this;
        faceSP = new SharedPreferencesHelper(mContext, "faceInfo");
        initListener();
        initData();
    }

    private void initData() {
        etUser.setText("lichao");
        etPassword.setText("123");
        progressBar = findViewById(R.id.spin_kit);
    }

    @SuppressLint("ClickableViewAccessibility")
    private void initListener() {
        etUser.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable) && ivCleanUser.getVisibility() == View.GONE) {
                    ivCleanUser.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(editable)) {
                    ivCleanUser.setVisibility(View.GONE);
                }
            }
        });

        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (!TextUtils.isEmpty(editable) && cleanPassword.getVisibility() == View.GONE) {
                    cleanPassword.setVisibility(View.VISIBLE);
                } else if (TextUtils.isEmpty(editable)) {
                    cleanPassword.setVisibility(View.GONE);
                }
            }
        });

        /**
         * 禁止键盘弹起的时候可以滚动
         */
        scrollView.setOnTouchListener((view, motionEvent) -> true);

        scrollView.addOnLayoutChangeListener((view, left, top, right, bottom, oldLeft, oldTop, oldRight, oldBottom) -> {
            if (oldBottom != 0 && bottom != 0 && (oldBottom - bottom > keyHeight)) {
                int dist = content.getBottom() - bottom;
                if (dist > 0) {
                    ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(content, "translationY", 0.0f, -dist);
                    mAnimatorTranslateY.setDuration(300);
                    mAnimatorTranslateY.setInterpolator(new LinearInterpolator());
                    mAnimatorTranslateY.start();
                    zoomIn(logo, dist);
                }
                service.setVisibility(View.INVISIBLE);

            } else if (oldBottom != 0 && bottom != 0 && (bottom - oldBottom > keyHeight)) {
                if ((content.getBottom() - oldBottom) > 0) {
                    ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(content, "translationY", content.getTranslationY(), 0);
                    mAnimatorTranslateY.setDuration(300);
                    mAnimatorTranslateY.setInterpolator(new LinearInterpolator());
                    mAnimatorTranslateY.start();
                    //键盘收回后，logo恢复原来大小，位置同样回到初始位置
                    zoomOut(logo);
                }
                service.setVisibility(View.VISIBLE);
            }
        });
    }

    @OnClick({R.id.regist, R.id.iv_clean_user, R.id.clean_password, R.id.iv_show_pwd, R.id.btn_login})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.regist:
                Intent intent = new Intent(mContext, RegisterActivity.class);
                startActivity(intent);
                break;
            case R.id.iv_clean_user:
                etUser.setText("");
                break;
            case R.id.clean_password:
                etPassword.setText("");
                break;
            case R.id.iv_show_pwd:
                if (etPassword.getInputType() != InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD) {
                    etPassword.setInputType(InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                    ivShowPwd.setImageResource(R.mipmap.pass_visuable);
                } else {
                    etPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                    ivShowPwd.setImageResource(R.mipmap.pass_gone);
                }
                String pwd = etPassword.getText().toString();
                if (!TextUtils.isEmpty(pwd))
                    etPassword.setSelection(pwd.length());
                break;
            case R.id.btn_login:
                progressBar.setVisibility(View.VISIBLE);
                Wave doubleBounce = new Wave();
                progressBar.setIndeterminateDrawable(doubleBounce);
                login();
                break;
        }
    }

    /**
     * 用户登录
     */
    private void login() {
        try {
            String privateKey = SPUtil.getString(Const.PRIVATE_KEY,"");
            String devnum = SPUtil.getString(Const.DEV_NUM,"");
            String username = etUser.getText().toString().trim();
            String passwd = etPassword.getText().toString().trim();
            String ts = TimeUtils.getTime13();
            String sign = devnum + username + passwd + ts;
            byte[] ss = sign.getBytes();
            String sign_str = RSAUtils.sign(ss, privateKey);

            OkHttpUtils.postString()
                    .url(Const.LOGIN + "ts=" + TimeUtils.getTime13() + "&sign=" + sign_str)
                    .content(new Gson().toJson(new Login(sign_str, devnum, username, passwd, ts)))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .build()
                    .execute(new StringCallback() {

                        @Override
                        public void onError(Call call, Exception e, int id) {
                            progressBar.setVisibility(View.INVISIBLE);
                            Toasty.error(mContext, getString(R.string.toast_request_error), Toast.LENGTH_LONG, true).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
//                            LogUtil.i("lichao", "success:" + response);
                            if (!response.equals("resource/500")) {
                                LoginResponse gsonLogin = gson.fromJson(response, LoginResponse.class);
                                if (gsonLogin.getErrorcode() == 0) {
                                    if (gsonLogin.getData().getFace() != null) {
                                        SPUtil.putString("username", username);
                                        SPUtil.putString("passwd", passwd);
                                        faceSP.put("face", gsonLogin.getData().getFace());
                                        addFace(gsonLogin.getData().getFace());
                                        Intent intent = new Intent(mContext, FaceActivity.class);
                                        startActivity(intent);
                                        finish();
                                        Toasty.success(mContext, getString(R.string.toast_login_success), Toast.LENGTH_SHORT, true).show();
                                    } else {
                                        progressBar.setVisibility(View.INVISIBLE);
                                        Toasty.error(mContext, getString(R.string.toast_login_error_no_face), Toast.LENGTH_LONG, true).show();
                                    }
                                }  else if (gsonLogin.getErrorcode() == 1) {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toasty.error(mContext, getString(R.string.toast_login_error) + gsonLogin.getMessage(), Toast.LENGTH_LONG, true).show();
                                } else {
                                    progressBar.setVisibility(View.INVISIBLE);
                                    Toasty.error(mContext, getString(R.string.toast_login_error_code) + gsonLogin.getErrorcode(), Toast.LENGTH_LONG, true).show();
                                }
                            } else {
                                progressBar.setVisibility(View.INVISIBLE);
                                Toasty.error(mContext, getString(R.string.toast_server_error), Toast.LENGTH_LONG, true).show();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 保存人脸模板
     *
     * @param faceInfo
     */
    private void addFace(String faceInfo) {
        if (faceInfo.isEmpty()) {
            Toasty.warning(mContext, "获取模板图片失败", Toast.LENGTH_SHORT, true).show();
            return;
        }
        byte[] decode = Base64.decode(faceInfo, Base64.DEFAULT);
        Bitmap faceBitmap = BitmapFactory.decodeByteArray(decode, 0, decode.length);
        insertTemplate(faceBitmap);
        if (insertTemplate(faceBitmap).equals("success")) {
            Log.i("Gavin", "模板success");
        } else {
            Log.i("Gavin", "模板error");
        }
    }

    private String insertTemplate(Bitmap bmp) {
        //转RGB
        byte[] mBGR = FileUtils.bitmapToBGR24(bmp);
        FaceInfo faceInfoKj = MyApplication.mDetect.getFacePositionScaleFromGray(mBGR, bmp.getWidth(), bmp.getHeight(), 5);
        if (faceInfoKj.getRet() != 1) {
            return "检测不到人脸";
        }
        int ret = MyApplication.mRecognize.registerFaceFeature(1, mBGR, bmp.getWidth(), bmp.getHeight(), faceInfoKj.getFacePosData(0));
        if (ret > 0) {
            return "success";
        } else {
            return "注册人脸异常,错误码:" + ret;
        }
    }

    /**
     * 缩小
     *
     * @param view
     */
    public void zoomIn(final View view, float dist) {
        view.setPivotY(view.getHeight());
        view.setPivotX(view.getWidth() / 2);
        AnimatorSet mAnimatorSet = new AnimatorSet();
        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.0f, scale);
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.0f, scale);
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", 0.0f, -dist);

        mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX);
        mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY);
        mAnimatorSet.setDuration(300);
        mAnimatorSet.start();
    }

    /**
     * f放大
     *
     * @param view
     */
    public void zoomOut(final View view) {
        view.setPivotY(view.getHeight());
        view.setPivotX(view.getWidth() / 2);
        AnimatorSet mAnimatorSet = new AnimatorSet();

        ObjectAnimator mAnimatorScaleX = ObjectAnimator.ofFloat(view, "scaleX", scale, 1.0f);
        ObjectAnimator mAnimatorScaleY = ObjectAnimator.ofFloat(view, "scaleY", scale, 1.0f);
        ObjectAnimator mAnimatorTranslateY = ObjectAnimator.ofFloat(view, "translationY", view.getTranslationY(), 0);

        mAnimatorSet.play(mAnimatorTranslateY).with(mAnimatorScaleX);
        mAnimatorSet.play(mAnimatorScaleX).with(mAnimatorScaleY);
        mAnimatorSet.setDuration(300);
        mAnimatorSet.start();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        android.os.Debug.stopMethodTracing();
    }

}
