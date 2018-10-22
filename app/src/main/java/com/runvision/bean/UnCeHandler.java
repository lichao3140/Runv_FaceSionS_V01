package com.runvision.bean;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Looper;
import android.util.Log;

import com.runvision.core.MyApplication;
import com.runvision.firs_facesions.CameraActivity;
import com.runvision.utils.LogToFile;

public class UnCeHandler implements Thread.UncaughtExceptionHandler {

    private Thread.UncaughtExceptionHandler mDefaultHandler;
    public static final String TAG = "MyApplication";
    MyApplication application;

    public UnCeHandler(MyApplication application) {
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        this.application = application;
    }

    @Override
    public void uncaughtException(Thread thread, Throwable ex) {

        Log.d(TAG, "adsadsa");

        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果用户没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            Intent intent = new Intent(application.getApplicationContext(), CameraActivity.class);
            AlarmManager mAlarmManager = (AlarmManager) application
                    .getSystemService(Context.ALARM_SERVICE);
            PendingIntent restartIntent = PendingIntent.getActivity(application.getApplicationContext(), 0, intent, Intent.FLAG_ACTIVITY_NEW_TASK);

            mAlarmManager.set(AlarmManager.RTC,
                    System.currentTimeMillis() + 1000, restartIntent);
            application.finishActivity();
        }
    }


    private boolean handleException(final Throwable ex) {
        if (ex == null) {
            return false;
        }
        Log.i("sulin", ex.getMessage());
        LogToFile.i("Exception", ex.getMessage());
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                //Toast.makeText(application.getContext(), ex.getMessage(), Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
        return true;
    }
}