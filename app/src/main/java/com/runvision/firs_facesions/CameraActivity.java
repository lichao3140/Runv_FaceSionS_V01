package com.runvision.firs_facesions;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.mylhyl.circledialog.CircleDialog;
import com.runvision.adapter.BaseAdapter;
import com.runvision.adapter.MenuCardAdapter;
import com.runvision.adapter.SignAdapter;
import com.runvision.bean.AppData;
import com.runvision.bean.Sign;
import com.runvision.core.Const;
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
    private RelativeLayout show_card;
    private TextView loadcardText;

    private List<Sign> signList = new ArrayList<Sign>();
    private SignAdapter signadapter;
    private ListView sign_listView;
    private UIThread uithread;
    private boolean signoutflag = false;

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
        navigationView.setItemIconTintList(null);//显示图片原始样式

        myFaceFrameView = findViewById(R.id.myFaceFrameView);
        myCameraView = findViewById(R.id.myCameraSurfaceView);
        myCameraView.openCamera();

        signadapter = new SignAdapter(context, R.layout.signin_item, signList);
        sign_listView = findViewById(R.id.lv_sign);
        sign_listView.setAdapter(signadapter);

        show_card	= findViewById(R.id.show_card);
        loadcardText = findViewById(R.id.loadcardText);
        uithread = new UIThread();
        uithread.start();
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
                        .setPositive("确定",  null)
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
        myCameraView.openCamera();
    }

    @Override
    protected void onResume() {
        super.onResume();
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
                            Log.i("Gavin","跑几次");
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
                        }else if (MainService.getService().timeflag == 7) {
                            ShowPromptMessage("无法重复签到", 7);
                            MainService.getService().timeflag = 0;
                        }else if (MainService.getService().timeflag == 8) {
                            ShowPromptMessage("没有签到信息", 8);
                            MainService.getService().timeflag = 0;
                        }
                        MainService.getService().isCompareSuccess = 0;
                    } else if (MainService.getService().isCompareSuccess == 2) {//比对失败
                        ShowPromptMessage("比对不通过", 7);
                        MainService.getService().isCompareSuccess = 0;
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private void ShowPromptMessage(String showmessage,int audionum) {
        if(audionum==0) {
            //  play = MediaPlayer.create(mcontext, R.raw.faceup);
            //  play.start();
        } else if(audionum==1) {
            //  play = MediaPlayer.create(mcontext, R.raw.template);
            //  play.start();
        } else if(audionum==2) {
            //  play = MediaPlayer.create(mcontext, R.raw.cardid);
            //  play.start();
        } else if(audionum==3) {
            //  play = MediaPlayer.create(mcontext, R.raw.cardid);
            //  play.start();
        } else if(audionum==4) {
            //  play = MediaPlayer.create(mcontext, R.raw.cardid);
            //  play.start();
        } else if(audionum==5) {
            //  play = MediaPlayer.create(mcontext, R.raw.cardid);
            //  play.start();
        } else if(audionum==6) {
            //  play = MediaPlayer.create(mcontext, R.raw.cardid);
            //  play.start();
        } else if(audionum==7) {
            //  play = MediaPlayer.create(mcontext, R.raw.cardid);
            //  play.start();
        }
        loadcardText.setText(showmessage);
        show_card.setVisibility(View.VISIBLE);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                show_card.setVisibility(View.GONE);
            }
        }, 2000);
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

}
