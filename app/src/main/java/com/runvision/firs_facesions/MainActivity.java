package com.runvision.firs_facesions;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.runvision.bean.AppData;
import com.runvision.bean.Fruit;
import com.runvision.bean.FruitAdapter;
import com.runvision.bean.Sigin;
import com.runvision.bean.SiginAdapter;
import com.runvision.core.Const;
import com.runvision.myview.FaceFrameView;
import com.runvision.myview.MyCameraSuf;
import com.runvision.thread.ToHttpThread;
import com.runvision.utils.CameraHelp;
import com.runvision.utils.JsonTools;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends Activity implements AdapterView.OnItemClickListener {
    Context mcontext;
    public static FaceFrameView myFaceFrameView;
    public static MyCameraSuf myCameraView;
   // private Button menu_btn;
    private DrawerLayout mDrawerLayout = null;

    private RelativeLayout show_card;

    private TextView loadcardText;

   // private DrawerLayout mbotton_layout=null;

    private List<Fruit> fruitList = new ArrayList<Fruit>();

    private List<Sigin> signinList = new ArrayList<Sigin>();

    private MediaPlayer play;

    private UIThread uithread ;

    SiginAdapter siginadapter;

    ListView sigin_listView;
    private boolean signoutflag=false;


    private JsonTools mJsonTools = new JsonTools();


    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case Const.UPDATE_UI:
                        if (MainService.getService().isCompareSuccess == 1) {//比对成功
                           if (MainService.getService().timeflag == 1) {
                                ShowPromptMessage("签到时间未到", 1);
                                MainService.getService().timeflag = 0;
                            } else if (MainService.getService().timeflag == 2) {
                               initSignin();
                               Log.i("Gavin","跑几次");
                               sigin_listView.setAdapter(siginadapter);//刷新列表
                                ShowPromptMessage("签到成功", 2);
                                MainService.getService().timeflag = 0;
                            } else if (MainService.getService().timeflag == 3) {
                                ShowPromptMessage("签到时间已过" + "\r\n" + "签退时间未到", 3);
                                MainService.getService().timeflag = 0;
                            } else if (MainService.getService().timeflag == 4) {
                               signoutflag=true;
                               initSignin();
                               sigin_listView.setAdapter(siginadapter);//刷新列表
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

    private class UIThread extends Thread {
        @Override
        public void run() {
            // TODO Auto-generated method stub
            super.run();
            while (true) {
                try {
                    Thread.sleep(1000);
                    Message msg = new Message();
                    msg.what = Const.UPDATE_UI;
                    handler.sendMessage(msg);
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }

    }

    /**
     * 隐藏虚拟按键，并且全屏
     */
    @SuppressLint("NewApi")
    protected void hideBottomUIMenu() {
        // 隐藏虚拟按键，并且全屏
        if (Build.VERSION.SDK_INT > 11 && Build.VERSION.SDK_INT < 19) {
            View v = this.getWindow().getDecorView();
            v.setSystemUiVisibility(View.GONE);
        } else if (Build.VERSION.SDK_INT >= 19) {
            // for new api versions.
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_HIDE_NAVIGATION | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY | View.SYSTEM_UI_FLAG_FULLSCREEN;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);// 设置全屏
        // 隐藏下面的虚拟按�?????
        hideBottomUIMenu();
        setContentView(R.layout.activity_main);
        mcontext=this;
       // menu_btn=(Button)findViewById(R.id.menu_btn);
       // menu_btn.setOnClickListener(this);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        show_card	= (RelativeLayout) findViewById(R.id.show_card);
        loadcardText = (TextView) findViewById(R.id.loadcardText);
      //  mbotton_layout= (DrawerLayout) findViewById(R.id.botton_layout);
        myFaceFrameView = (FaceFrameView) findViewById(R.id.myFaceFrameView);
        myCameraView = (MyCameraSuf) findViewById(R.id.myCameraSurfaceView);
        myCameraView.openCamera();
        startService(new Intent(MainActivity.this, MainService.class));//刷身份证对比操作

        initFruits(); // 初始化菜单
        FruitAdapter adapter = new FruitAdapter(MainActivity.this, R.layout.fruit_item, fruitList);
        ListView listView = (ListView) findViewById(R.id.left_drawer);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(this);

        initSignin();
        siginadapter = new SiginAdapter(MainActivity.this, R.layout.signin_item, signinList);
        sigin_listView = (ListView) findViewById(R.id.below_drawer);
        sigin_listView.setAdapter(siginadapter);
       // initSignin();
       // siginadapter = new SiginAdapter(MainActivity.this, R.layout.signin_item, signinList);
       // sigin_listView = (ListView) findViewById(R.id.below_drawer);
       // sigin_listView.setAdapter(siginadapter);
       // listView.setOnItemClickListener(this);

        uithread = new UIThread();
        uithread.start();

    }

    private void initFruits() {
        Fruit set = new Fruit("设置", R.drawable.userinfomodify_n);
        fruitList.add(set);
        Fruit parameter = new Fruit("考勤参数", R.drawable.adduser_n);
        fruitList.add(parameter);
        Fruit signout = new Fruit("退出", R.drawable.switchuser_n);
        fruitList.add(signout);

    }

    private void initSignin() {
        //读取数据库显示签到数据
        Cursor c=  MainService.getService().helper.getAllTitles();
        if(c.getCount()==0)
        {
            signinList.clear();
        }
        if(c.moveToLast()) {
                if (signinList != null) {
                    if (signinList.size() > 0) {
                        signinList.clear();
                    }
                }
                do {
                    String idnum = c.getString(3).substring(0, 6) + "*********" + c.getString(3).substring(16, 18);
                    Sigin sd = new Sigin(c.getString(1), CameraHelp.getSmallBitmap(c.getString(4)),
                            c.getString(2), idnum, c.getString(6));
                    signinList.add(sd);
                } while (c.moveToPrevious());
            }

    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Fruit bean = fruitList.get(position);
        Log.i("Gavin","按的是："+bean.getName());
        if(bean.getName().equals("设置"))
        {
            //跳到设置界面
        }
        if(bean.getName().equals("考勤参数"))
        {
            //跳到考勤参数界面
            DialogInterface.OnCancelListener onCancelListener ;
            AlertDialog dialog =new AlertDialog.Builder(MainActivity.this)
                    .setTitle("考勤参数")
                    .setMessage("课堂开始时间(24小时制):"+ AppData.getAppData().getStarttime()+"\r\n"+  //从平台下载的考勤参数
                                "课堂关闭时间(24小时制):"+ AppData.getAppData().getEndtime()+"\n"+
                                "签到开始时间(24小时制):"+ AppData.getAppData().getInstarttime()+"\n"+
                                "签到结束时间(24小时制):"+ AppData.getAppData().getInendtime()+"\n"+
                                "签退开始时间(24小时制):"+ AppData.getAppData().getOutstarttime()+"\n"+
                                "签退结束时间(24小时制):"+ AppData.getAppData().getOutendtime()
                    )
                    .setNegativeButton("更新考勤参数", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                             //更新考勤参数
                             //发送考勤参数查询接口
                           // AppData.getAppData().setInscode();
                           // AppData.getAppData().setDevnum();
                            String postdate=mJsonTools.parseJSONWithString(4);
                            // Log.d("Gavin",postdate);
                            ToHttpThread mPostDateThread= new ToHttpThread(Const.REGISTER,postdate);
                            mPostDateThread.start();
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                            hideBottomUIMenu();
                            dialog.dismiss();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
            try {
                Field mAlert = AlertDialog.class.getDeclaredField("mAlert");
                mAlert.setAccessible(true);
                Object mAlertController = mAlert.get(dialog);
                Field mMessage = mAlertController.getClass().getDeclaredField("mMessageView");
                mMessage.setAccessible(true);
                TextView mMessageView = (TextView) mMessage.get(mAlertController);
                mMessageView.setTextColor(Color.RED);
                mMessageView.setTextSize(25);
                WindowManager.LayoutParams params = dialog.getWindow().getAttributes();
                params.width = 600;
                params.height = 350 ;
                dialog.getWindow().setAttributes(params);
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            }
        }
        if(bean.getName().equals("退出"))
        {
            //跳到登陆界面
            myCameraView.releaseCamera();
            Intent intent = new Intent(MainActivity.this, LandActivity.class);
            startActivity(intent);
            finish();
        }

    }


    private void ShowPromptMessage(String showmessage,int audionum) {
        if(audionum==0)
        {
          //  play = MediaPlayer.create(mcontext, R.raw.faceup);
          //  play.start();
        }
        if(audionum==1)
        {
          //  play = MediaPlayer.create(mcontext, R.raw.template);
          //  play.start();
        }
        else if(audionum==2)
        {
          //  play = MediaPlayer.create(mcontext, R.raw.cardid);
          //  play.start();
        }else if(audionum==3)
        {
            //  play = MediaPlayer.create(mcontext, R.raw.cardid);
            //  play.start();
        }else if(audionum==4)
        {
            //  play = MediaPlayer.create(mcontext, R.raw.cardid);
            //  play.start();
        }else if(audionum==5)
        {
            //  play = MediaPlayer.create(mcontext, R.raw.cardid);
            //  play.start();
        }else if(audionum==6)
        {
            //  play = MediaPlayer.create(mcontext, R.raw.cardid);
            //  play.start();
        }else if(audionum==7)
        {
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


    };
}
