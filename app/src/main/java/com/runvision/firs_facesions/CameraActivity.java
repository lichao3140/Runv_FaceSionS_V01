package com.runvision.firs_facesions;

import android.annotation.SuppressLint;
import com.jzxiang.pickerview.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.jzxiang.pickerview.data.Type;
import com.jzxiang.pickerview.listener.OnDateSetListener;
import com.mylhyl.circledialog.CircleDialog;
import com.runvision.adapter.CheckedAdapter;
import com.runvision.adapter.PictureTypeEntity;
import com.runvision.adapter.SignAdapter;
import com.runvision.bean.AppData;
import com.runvision.bean.Atnd;
import com.runvision.bean.AtndResponse;
import com.runvision.bean.Cours;
import com.runvision.bean.Sign;
import com.runvision.core.Const;
import com.runvision.myview.FaceFrameView;
import com.runvision.myview.MyCameraSuf;
import com.runvision.utils.CameraHelp;
import com.runvision.utils.RSAUtils;
import com.runvision.utils.SPUtil;
import com.runvision.utils.TimeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * 学员考勤签到
 */
public class CameraActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener, OnDateSetListener {

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    @BindView(R.id.fullscreen_content)
    View mContentView;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    public Context context;
    public FaceFrameView myFaceFrameView;
    public MyCameraSuf myCameraView;
    private RelativeLayout show_card;
    private TextView loadcardText;

    private List<Sign> signList = new ArrayList<Sign>();
    private SignAdapter signadapter;
    private ListView sign_listView;
    private UIThread uithread;
    private boolean signoutflag = false;
    private DialogFragment dialogFragment;
    private TimePickerDialog mDialogHourMinute;
    private TimePickerDialog mDialogAll;
    //时间选择
    private String selectTime;
    //考勤课程选择
    private String select_index;
    private int selectId;

    private MediaPlayer play;

    SimpleDateFormat sf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };

    private View mControlsView;
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
            initSign();
            mControlsView.setVisibility(View.VISIBLE);
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = () -> hide();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_camera);
        ButterKnife.bind(this);
        context = this;
        mVisible = true;
        startService(new Intent(context, MainService.class));
        init();
    }

    private void init() {
        drawerLayout = findViewById(R.id.drawer);
        mControlsView = findViewById(R.id.fullscreen_content_controls);
        navigationView = findViewById(R.id.navigation);
        navigationView.setNavigationItemSelectedListener(this);
        navigationView.setItemIconTintList(null);//显示图片原始样式

        myFaceFrameView = findViewById(R.id.myFaceFrameView);
        myCameraView = findViewById(R.id.myCameraSurfaceView);
        myCameraView.openCamera();

        signadapter = new SignAdapter(context, R.layout.signin_item, signList);
        sign_listView = findViewById(R.id.lv_sign);
        sign_listView.setAdapter(signadapter);

        show_card = findViewById(R.id.show_card);
        loadcardText = findViewById(R.id.loadcardText);
        uithread = new UIThread();
        uithread.start();
    }

    private void initSign() {
        //读取数据库显示签到数据
        Cursor c = MainService.getService().helper.getAllTitles();
        if (c.getCount() == 0) {
            signList.clear();
        }
        if (c.moveToLast()) {
            if (signList != null) {
                if (signList.size() > 0) {
                    signList.clear();
                }
            }
            do {
                String idnum = c.getString(3).substring(0, 6) + "*********" + c.getString(3).substring(16, 18);
                Sign sd = new Sign(c.getString(1), CameraHelp.getSmallBitmap(c.getString(4)),
                        c.getString(2), idnum, c.getString(6));
                signList.add(sd);
            } while (c.moveToPrevious());
        }
    }

    @OnClick({R.id.fullscreen_content})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fullscreen_content:
                toggle();
                break;
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        int id = menuItem.getItemId();
        switch (id) {
            case R.id.nav_setting:
                settingTimeDialog();
                break;
            case R.id.nav_config:
                Atndquery();
                break;
            case R.id.nav_sign:
                Intent sign = new Intent(context, SignRecordActivity.class);
                startActivity(sign);
                break;
            case R.id.nav_exit:
                finish();
                break;
            case R.id.nav_about:
                new CircleDialog.Builder()
                        .setTitle("技术支持")
                        .setText("深圳市元视科技有限公司")
                        .setPositive("确定", null)
                        .show(getSupportFragmentManager());
                break;
            default:
                drawerLayout.closeDrawers();
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    /**
     * 考勤时间设置
     */
    private void settingTimeDialog() {
        final List<PictureTypeEntity> list = new ArrayList<>();
        list.add(new PictureTypeEntity(1, "签到开始时间:\t"));
        list.add(new PictureTypeEntity(3, TimeUtils.getYearMonth() + "\t" + AppData.getAppData().getInstarttime()));
        list.add(new PictureTypeEntity(2, "签到结束时间:\t"));
        list.add(new PictureTypeEntity(4, TimeUtils.getYearMonth() + "\t" + AppData.getAppData().getInendtime()));
        list.add(new PictureTypeEntity(5, "签退开始时间:\t"));
        list.add(new PictureTypeEntity(7, TimeUtils.getYearMonth() + "\t" + AppData.getAppData().getOutstarttime()));
        list.add(new PictureTypeEntity(6, "签退结束时间:\t"));
        list.add(new PictureTypeEntity(8, TimeUtils.getYearMonth() + "\t" + AppData.getAppData().getOutendtime()));

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        new CircleDialog.Builder()
                .setTitle("考勤时间设置")
                .configItems(params -> params.dividerHeight = 0)
                .setItems(list, gridLayoutManager, (view13, position13) -> {
                    showTimeHoursMin();
                    mDialogHourMinute.show(getSupportFragmentManager(), "hour_minute");
                    selectId = list.get(position13).id;
                })
                .setNegative("取消", null)
                .show(getSupportFragmentManager());
    }

    /**
     * 时分
     */
    private void showTimeHoursMin() {
        mDialogHourMinute = new TimePickerDialog.Builder()
                .setTitleStringId("选择时间")
                .setType(Type.HOURS_MINS)
                .setCallBack(this)
                .build();
    }

    /**
     * 年月日时分
     */
    private void showTimePick() {
        long tenYears = 10L * 365 * 1000 * 60 * 60 * 24L;
        mDialogAll = new TimePickerDialog.Builder()
                .setCallBack(this)
                .setCancelStringId("取消")
                .setSureStringId("确定")
                .setTitleStringId("时间选择")
                .setYearText("年")
                .setMonthText("月")
                .setDayText("日")
                .setHourText("时")
                .setMinuteText("分")
                .setCyclic(false)
                .setMinMillseconds(System.currentTimeMillis())
                .setMaxMillseconds(System.currentTimeMillis() + tenYears)
                .setCurrentMillseconds(System.currentTimeMillis())
                .setThemeColor(getResources().getColor(R.color.timepicker_dialog_bg))
                .setType(Type.HOURS_MINS)
                .setWheelItemTextNormalColor(getResources().getColor(R.color.timetimepicker_default_text_color))
                .setWheelItemTextSelectorColor(getResources().getColor(R.color.timepicker_toolbar_bg))
                .setWheelItemTextSize(18)
                .build();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        myCameraView.openCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        myCameraView.releaseCamera();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        myCameraView.releaseCamera();
    }

    @SuppressLint("HandlerLeak")
    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Const.UPDATE_UI:
                    if (MainService.getService().isCompareSuccess == 1) {//比对成功
                        if (MainService.getService().timeflag == 1) {
                            ShowPromptMessage("签到时间未到", 1);
                            MainService.getService().timeflag = 0;
                        } else if (MainService.getService().timeflag == 2) {
                            initSign();
                            sign_listView.setAdapter(signadapter);//刷新列表
                            ShowPromptMessage("签到成功", 2);
                            MainService.getService().timeflag = 0;
                        } else if (MainService.getService().timeflag == 3) {
                            ShowPromptMessage("签到时间已过" + "\r\n" + "签退时间未到", 3);
                            MainService.getService().timeflag = 0;
                        } else if (MainService.getService().timeflag == 4) {
                            signoutflag = true;
                            initSign();
                            sign_listView.setAdapter(signadapter);
                            ShowPromptMessage("签退成功", 4);
                            MainService.getService().timeflag = 0;
                        } else if (MainService.getService().timeflag == 5) {
                            ShowPromptMessage("签退时间已过", 5);
                            MainService.getService().timeflag = 0;
                        } else if (MainService.getService().timeflag == 6) {
                            ShowPromptMessage("不是课堂时间", 6);
                            MainService.getService().timeflag = 0;
                        } else if (MainService.getService().timeflag == 7) {
                            ShowPromptMessage("无法重复签到", 7);
                            MainService.getService().timeflag = 0;
                        } else if (MainService.getService().timeflag == 8) {
                            ShowPromptMessage("没有签到信息", 8);
                            MainService.getService().timeflag = 0;
                        } else if (MainService.getService().timeflag == 10) {
                            ShowPromptMessage("未选择课程", 10);
                            MainService.getService().timeflag = 0;
                        }
                        MainService.getService().isCompareSuccess = 0;
                    } else if (MainService.getService().isCompareSuccess == 2) {//比对失败
                        ShowPromptMessage("比对不通过", 9);
                        MainService.getService().isCompareSuccess = 0;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void ShowPromptMessage(String showmessage, int audionum) {
        if (audionum == 1) {
            play = MediaPlayer.create(context, R.raw.no_sign_time);
            play.start();
        } else if (audionum == 2) {
            play = MediaPlayer.create(context, R.raw.sign_success);
            play.start();
        } else if (audionum == 3) {
            play = MediaPlayer.create(context, R.raw.sign_time_over);
            play.start();
        } else if (audionum == 4) {
            play = MediaPlayer.create(context, R.raw.sign_out_success);
            play.start();
        } else if (audionum == 5) {
            play = MediaPlayer.create(context, R.raw.sign_out_time_over);
            play.start();
        } else if (audionum == 6) {
            play = MediaPlayer.create(context, R.raw.no_cours_time);
            play.start();
        } else if (audionum == 7) {
            play = MediaPlayer.create(context, R.raw.no_agin_sign);
            play.start();
        } else if (audionum == 8) {
            play = MediaPlayer.create(context, R.raw.no_sign_info);
            play.start();
        } else if (audionum == 9) {
            play = MediaPlayer.create(context, R.raw.valid_not_pass);
            play.start();
        } else if (audionum == 10) {
            play = MediaPlayer.create(context, R.raw.no_select_coures);
            play.start();
        }
        loadcardText.setText(showmessage);
        show_card.setVisibility(View.VISIBLE);
        handler.postDelayed(() -> show_card.setVisibility(View.GONE), 2000);
    }

    @Override
    public void onDateSet(TimePickerDialog timePickerView, long millseconds) {
        selectTime = getDateToString(millseconds).substring(11, 19);

        switch (selectId) {
            case 1:
            case 3:
                AppData.getAppData().setInstarttime(selectTime);
                Toast.makeText(context, "签到开始时间:" + selectTime, Toast.LENGTH_SHORT).show();
                break;

            case 2:
            case 4:
                AppData.getAppData().setInendtime(selectTime);
                Toast.makeText(context, "签到结束时间:" + selectTime, Toast.LENGTH_SHORT).show();
                break;

            case 5:
            case 7:
                AppData.getAppData().setOutstarttime(selectTime);
                Toast.makeText(context, "签退开始时间:" + selectTime, Toast.LENGTH_SHORT).show();
                break;

            case 6:
            case 8:
                AppData.getAppData().setOutendtime(selectTime);
                Toast.makeText(context, "签退结束时间:" + selectTime, Toast.LENGTH_SHORT).show();
                break;
        }
    }

    public String getDateToString(long time) {
        Date d = new Date(time);
        return sf.format(d);
    }

    private class UIThread extends Thread {
        @Override
        public void run() {
            super.run();
            while (true) {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = Const.UPDATE_UI;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mControlsView.setVisibility(View.GONE);
        mVisible = false;

        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }

    public void Atndquery() {
        try {
            String inscode = SPUtil.getString(Const.DEV_INSCODE, "");
            String privateKey = SPUtil.getString(Const.PRIVATE_KEY, "");
            String devnum = SPUtil.getString(Const.DEV_NUM, "");
            String ts = TimeUtils.getTime13();
            String sign = inscode + devnum + ts;
            byte[] ss = sign.getBytes();
            String sign_str = RSAUtils.sign(ss, privateKey);

            OkHttpUtils.postString()
                    .url(Const.PARAMETER + "ts=" + TimeUtils.getTime13() + "&sign=" + sign_str)
                    .content(new Gson().toJson(new Atnd(sign_str, inscode, devnum, ts)))
                    .mediaType(MediaType.parse("application/json; charset=utf-8"))
                    .build()
                    .execute(new StringCallback() {
                        @Override
                        public void onError(Call call, Exception e, int id) {
                            Toasty.error(context, context.getString(R.string.toast_request_error), Toast.LENGTH_LONG, true).show();
                        }

                        @Override
                        public void onResponse(String response, int id) {
                            if (!response.equals("resource/500")) {
                                Gson gson = new Gson();
                                AtndResponse gsonAtnd = gson.fromJson(response, AtndResponse.class);
                                if (gsonAtnd.getErrorcode().equals("0")) {
                                    showInfo(gsonAtnd.getData());
                                    Toasty.success(context, context.getString(R.string.toast_update_success), Toast.LENGTH_SHORT, true).show();
                                } else {
                                    Toasty.error(context, context.getString(R.string.toast_update_fail), Toast.LENGTH_LONG, true).show();
                                }
                            } else {
                                Toasty.error(context, context.getString(R.string.toast_server_error), Toast.LENGTH_LONG, true).show();
                            }
                        }
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showInfo(String coursData) {
        try {
            Gson gson = new Gson();
            List<Cours> coursList = gson.fromJson(coursData, new TypeToken<List<Cours>>() {
            }.getType());
            String[] cours_Coursename = new String[coursList.size()];

            for (int i = 0; i < coursList.size(); i++) {
                cours_Coursename[i] = String.valueOf(coursList.get(i).getCoursename());
            }

            CheckedAdapter checkedAdapterR = new CheckedAdapter(this, cours_Coursename, true);
            new CircleDialog.Builder()
                    .configDialog(params -> params.backgroundColorPress = Color.CYAN)
                    .setTitle("考勤参数")
                    .setSubTitle("请选择要考勤的课程")
                    .setItems(checkedAdapterR, (parent, view15, position15, id) ->
                            checkedAdapterR.toggle(position15, cours_Coursename[position15]))
                    .setItemsManualClose(true)
                    .setPositive("确定", v -> {
                        select_index = checkedAdapterR.getSaveChecked().toString().substring(1, 2);
                        SPUtil.putString(Const.SELECT_COURSE_NAME, coursList.get(Integer.parseInt(select_index)).getClasscode());
                        Toasty.info(context, "选课成功", Toast.LENGTH_SHORT, true).show();
                    })
                    .show(getSupportFragmentManager());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
