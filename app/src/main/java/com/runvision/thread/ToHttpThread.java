package com.runvision.thread;

import android.util.Log;

import com.runvision.utils.HttpUtil;

public class ToHttpThread extends Thread{

    private HttpUtil mHttpUtil = new HttpUtil();
    private String url;
    private String jsondata;
    public ToHttpThread(String url, String jsondata){
           this.jsondata=jsondata;
           this.url=url;
    }
    private boolean bleRunning = false;

    @Override
    public void run() {
        bleRunning = true;
        int jsonpostnum=0;
        String httpdata;
        super.run();
        while (bleRunning) {
            if(jsonpostnum<1) {
            	httpdata=mHttpUtil.doJsonPost(url, jsondata);
            	Log.d("Gavin",httpdata);
                if (httpdata.equals("0")) {
                    bleRunning = false;
                    continue;
                } else {
                    jsonpostnum++;
                }
            }
            else
            {
                bleRunning=false;
                continue;
            }

            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    public void close() {
        bleRunning = false;
    }

}
