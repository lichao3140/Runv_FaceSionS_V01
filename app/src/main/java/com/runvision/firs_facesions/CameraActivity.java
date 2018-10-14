package com.runvision.firs_facesions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;
import com.mylhyl.circledialog.CircleDialog;
import com.runvision.adapter.BaseAdapter;
import com.runvision.adapter.MenuCardAdapter;
import com.runvision.adapter.SignAdapter;
import com.runvision.bean.AppData;
import com.runvision.bean.Sign;
import com.runvision.myview.FaceFrameView;
import com.runvision.myview.MyCameraSuf;
import com.runvision.utils.CameraHelp;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;


public class CameraActivity extends BaseActivity implements
        NavigationView.OnNavigationItemSelectedListener {

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    @BindView(R.id.fullscreen_content)
    View mContentView;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    public Context context;
    public FaceFrameView myFaceFrameView;
    public MyCameraSuf myCameraView;

    private List<Sign> signList = new ArrayList<Sign>();
    private SignAdapter signadapter;
    private ListView sign_listView;;

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
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };

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

        myFaceFrameView = findViewById(R.id.myFaceFrameView);
        myCameraView = findViewById(R.id.myCameraSurfaceView);
        myCameraView.openCamera();

        signadapter = new SignAdapter(context, R.layout.signin_item, signList);
        sign_listView = findViewById(R.id.lv_sign);
        sign_listView.setAdapter(signadapter);
    }

    private void initSign() {
        //读取数据库显示签到数据
        Cursor c=  MainService.getService().helper.getAllTitles();
        if(c.getCount()==0) {
            signList.clear();
        }
        if(c.moveToLast()) {
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

                break;
            case R.id.nav_config:
                configDialog();
                break;
            case R.id.nav_exit:

                break;
            default:
                break;
        }
        drawerLayout.closeDrawer(GravityCompat.START);
        return false;
    }

    @Override
    protected BaseAdapter createAdapter() {
        return new MenuCardAdapter(this);
    }

    /**
     * 考勤参数
     */
    private void configDialog() {
        new CircleDialog.Builder()
                .setTitle("考勤参数")
                .setText(
                        "课堂开始时间(24小时制):"+ AppData.getAppData().getStarttime()+"\r\n"+
                        "课堂关闭时间(24小时制):"+ AppData.getAppData().getEndtime()+"\n"+
                        "签到开始时间(24小时制):"+ AppData.getAppData().getInstarttime()+"\n"+
                        "签到结束时间(24小时制):"+ AppData.getAppData().getInendtime()+"\n"+
                        "签退开始时间(24小时制):"+ AppData.getAppData().getOutstarttime()+"\n"+
                        "签退结束时间(24小时制):"+ AppData.getAppData().getOutendtime())
                .setPositive("更新参数",  v ->
                        Toasty.info(context, "参数更新", Toast.LENGTH_SHORT, true).show())
                .setNegative("取消", null)
                .setOnCancelListener(dialog ->
                        Toasty.info(context, "取消！", Toast.LENGTH_SHORT, true).show())
                .show(getSupportFragmentManager());
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
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

}
