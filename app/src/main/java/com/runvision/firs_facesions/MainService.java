package com.runvision.firs_facesions;

import android.annotation.SuppressLint;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;
import com.runvision.HttpCallback.HttpStudent;
import com.runvision.bean.AppData;
import com.runvision.core.Const;
import com.runvision.core.DBAdapter;
import com.runvision.core.FaceIDCardCompareLib;
import com.runvision.utils.CameraHelp;
import com.runvision.utils.LogToFile;
import com.runvision.utils.SPUtil;
import com.runvision.utils.TimeCompareUtil;
import com.runvision.utils.TimeUtils;
import com.runvision.utils.UUIDUtil;
import com.zkteco.android.IDReader.IDPhotoHelper;
import com.zkteco.android.IDReader.WLTService;
import com.zkteco.android.biometric.core.device.ParameterHelper;
import com.zkteco.android.biometric.core.device.TransportType;
import com.zkteco.android.biometric.core.utils.LogHelper;
import com.zkteco.android.biometric.module.idcard.IDCardReader;
import com.zkteco.android.biometric.module.idcard.IDCardReaderFactory;
import com.zkteco.android.biometric.module.idcard.exception.IDCardReaderException;
import com.zkteco.android.biometric.module.idcard.meta.IDCardInfo;
import java.util.HashMap;
import java.util.Map;
import android_serialport_api.SerialPort;


public class MainService extends Service {

    private String TAG = "MainService";
    // ----------------------------------------begin--------------------------------------------
    private static final int VID = 1024; // IDR VID
    private static final int PID = 50010; // IDR PID
    private final String ACTION_USB_PERMISSION = "com.example.scarx.idcardreader.USB_PERMISSION";
    private IDCardReader idCardReader = null;
    private UsbManager musbManager = null;
    private boolean bStop = false;
    public static boolean isReadCard = false;
    public static int isCompareSuccess = 0;
    // -----------------------------------------end---------------------------------------------
    private static MainService myService = null;
    private Context mContext;
    private TimeCompareUtil timecompare;
    public int timeflag = 0;
    public DBAdapter helper;

    public static MainService getService() {
        return myService;
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.i(TAG, "onCreate");

        mContext = this;
        myService = this;
        startIDCardReader();
        RequestDevicePermission();
        helper = new DBAdapter(this);
        helper.open();

        timecompare = new TimeCompareUtil();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        helper.close();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    private void startIDCardReader() {
        LogHelper.setLevel(Log.VERBOSE);
        Map idrparams = new HashMap();
        idrparams.put(ParameterHelper.PARAM_KEY_VID, VID);
        idrparams.put(ParameterHelper.PARAM_KEY_PID, PID);
        idCardReader = IDCardReaderFactory.createIDCardReader(this, TransportType.USB, idrparams);
    }

    private BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (ACTION_USB_PERMISSION.equals(action)) {
                synchronized (this) {
                    UsbDevice device = intent.getParcelableExtra(UsbManager.EXTRA_DEVICE);
                    if (intent.getBooleanExtra(UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
                        Toast.makeText(mContext, "读卡器授权成功", Toast.LENGTH_SHORT).show();
                        readCard();
                         if (SerialPort.Fill_in_light == false) {
                           SerialPort.openLED();
                         }
                    } else {
                        Toast.makeText(mContext, "USB未授权", Toast.LENGTH_SHORT).show();
                    }
                }
            } else {
                Log.i(TAG, "sdsadsadfsa");
            }

        }
    };

    @SuppressLint("NewApi")
    private void RequestDevicePermission() {
        musbManager = (UsbManager) this.getSystemService(Context.USB_SERVICE);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ACTION_USB_PERMISSION);
        filter.addAction(UsbManager.ACTION_USB_ACCESSORY_ATTACHED);
        mContext.registerReceiver(mUsbReceiver, filter);
        boolean flag = true;
        for (UsbDevice device : musbManager.getDeviceList().values()) {
            if (device.getVendorId() == VID && device.getProductId() == PID) {
                Intent intent = new Intent(ACTION_USB_PERMISSION);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(mContext, 0, intent, 0);
                musbManager.requestPermission(device, pendingIntent);
                flag = false;
            }
        }
        if (flag) {
            Toast.makeText(mContext, "没有检测到身份证读卡器,请插入", Toast.LENGTH_SHORT).show();
        }
    }

    private void readCard() {
        try {
            idCardReader.open(0);
            bStop = false;
            Log.i(TAG, "设备连接成功");
            new Thread(() -> {
                while (!bStop) {
                    long begin = System.currentTimeMillis();
                    IDCardInfo idCardInfo = new IDCardInfo();
                    boolean ret = false;
                    try {

                        idCardReader.findCard(0);
                        idCardReader.selectCard(0);
                    } catch (IDCardReaderException e) {
                        continue;
                    }
                    try {
                        ret = idCardReader.readCard(0, 0, idCardInfo);
                    } catch (IDCardReaderException e) {
                        Log.i(TAG, "读卡失败，错误信息：" + e.getMessage());
                    }

                    Log.i(TAG, "ret:" + ret);
                    Log.i("Gavin", "ret:" + ret);
                    if (ret) {
                        isReadCard = true;
                        //mReadCard=false;
                        //Const.canRead = false;
                        final long nTickUsed = (System.currentTimeMillis() - begin);
                        Log.i(TAG, "success>>>" + nTickUsed + ",name:" + idCardInfo.getName() + "," + idCardInfo.getValidityTime() + "��" + idCardInfo.getDepart());
                        Message msg = new Message();
                        msg.what = 2;
                        msg.obj = idCardInfo;
                        mhandler.sendMessage(msg);
                    }
                }
            }).start();

        } catch (IDCardReaderException e) {
            Log.i(TAG, "连接设备失败");
            Log.i(TAG, "开始读卡失败，错误码：" + e.getErrorCode() + "\n错误信息：" + e.getMessage() + "\n内部代码=" + e.getInternalErrorCode());
        }
    }

    @SuppressLint("HandlerLeak")
    public Handler mhandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 2) {
                final IDCardInfo idCardInfo = (IDCardInfo) msg.obj;
                if (idCardInfo.getPhotolength() > 0) {
                    byte[] buf = new byte[WLTService.imgLength];
                    if (1 == WLTService.wlt2Bmp(idCardInfo.getPhoto(), buf)) {
                        final Bitmap cardBmp = IDPhotoHelper.Bgr2Bitmap(buf);
                        if (cardBmp != null) {
                            String path = Environment.getExternalStorageDirectory() + "/FaceAndroid/Card/";
                            CameraHelp.saveImgToDisk(path, idCardInfo.getId() + ".jpg", cardBmp);
                            AppData.getAppData().setPicName(idCardInfo.getId());
                            final byte[] nv21 = CameraHelp.getNV21(cardBmp.getWidth(), cardBmp.getHeight(), cardBmp);
                            new Thread(() -> {
                                int socre = FaceIDCardCompareLib.getInstance().faceComperFrame(nv21);
                                Log.i("Gavin", "socre:" + socre);
                                setAlertConfig(socre, idCardInfo);
                            }).start();
                        }
                    } else {
                        Log.i(TAG, "cardBmp==null");
                        LogToFile.i(TAG, "cardBmp==null");
                    }
                } else {
                    LogToFile.i(TAG, "WLTService.wlt2Bmp(idCardInfo.getPhoto(), buf)!=1");
                }
            }
        }
    };

    //1.比较是否满足阈值满足人脸对比
    private void setAlertConfig(int aVoid, IDCardInfo idCardInfo) {
        if (aVoid > Const.FACE_SCORE) {
            isCompareSuccess = 1;
            JudgeTime(timecompare.getSystemTime(), idCardInfo);
        } else {
            isCompareSuccess = 2;
            //比对不成功
        }
    }

    //2.比较是否满足签到或者签退时间
    private void JudgeTime(String currentTime, IDCardInfo idCardInfo) {
        if (SPUtil.getString(Const.SELECT_COURSE_NAME, "").equals("")) {
            timeflag = 10;
            Log.i("Gavin", "未选择课程");
        } else if (timecompare.TimeCompare(AppData.getAppData().getStarttime(), AppData.getAppData().getInstarttime(), currentTime)) {
            timeflag = 1;
            Log.i("Gavin", "签到时间未到");
        } else if (timecompare.TimeCompare(AppData.getAppData().getInstarttime(), AppData.getAppData().getInendtime(), currentTime)) {
            //签到需要显示签到信息
            AppData.getAppData().setDevnum(SPUtil.getString(Const.DEV_NUM, ""));
            AppData.getAppData().setTime(currentTime);
            AppData.getAppData().setStucode(idCardInfo.getId());
            AppData.getAppData().setCardtype("1");
            AppData.getAppData().setGps(SPUtil.getString(Const.DEV_GPS, ""));
            AppData.getAppData().setImgstr(CameraHelp.bitmapToBase64(CameraHelp.getSmallBitmap(Environment.getExternalStorageDirectory() + "/FaceAndroid/Face/" + idCardInfo.getId() + ".jpg")));
            AppData.getAppData().setClasscode(SPUtil.getString(Const.SELECT_COURSE_NAME, ""));
            AppData.getAppData().setSn(UUIDUtil.getUniqueID(mContext) + TimeUtils.getTime13());
            AppData.getAppData().setStudentName(idCardInfo.getName());

            HttpStudent.Stulogin(mContext, AppData.getAppData().getDevnum(), TimeUtils.getCurrentTime(), AppData.getAppData().getStucode(),
                    AppData.getAppData().getCardtype(), AppData.getAppData().getGps(), AppData.getAppData().getImgstr(),
                    AppData.getAppData().getClasscode(), AppData.getAppData().getSn(), AppData.getAppData().getStudentName());

            timeflag = 2;
            operation_Sign_in(idCardInfo, currentTime);
            Log.i("Gavin", "正常签到");
        } else if (timecompare.TimeCompare(AppData.getAppData().getInendtime(), AppData.getAppData().getOutstarttime(), currentTime)) {
            timeflag = 3;
            Log.i("Gavin", "签到已过，签退未到");
        } else if (timecompare.TimeCompare(AppData.getAppData().getOutstarttime(), AppData.getAppData().getOutendtime(), currentTime)) {
            //签退需要显示签退信息
            AppData.getAppData().setDevnum(SPUtil.getString(Const.DEV_NUM, ""));
            AppData.getAppData().setTime(currentTime);
            AppData.getAppData().setStucode(idCardInfo.getId());
            AppData.getAppData().setCardtype("1");
            AppData.getAppData().setGps(SPUtil.getString(Const.DEV_GPS, ""));
            AppData.getAppData().setImgstr(CameraHelp.bitmapToBase64(CameraHelp.getSmallBitmap(Environment.getExternalStorageDirectory() + "/FaceAndroid/Face/" + idCardInfo.getId() + ".jpg")));
            AppData.getAppData().setClasscode(SPUtil.getString(Const.SELECT_COURSE_NAME, ""));
            AppData.getAppData().setSn(UUIDUtil.getUniqueID(mContext) + TimeUtils.getTime13());
            AppData.getAppData().setStudentName(idCardInfo.getName());

            HttpStudent.Stulogout(mContext, AppData.getAppData().getDevnum(), TimeUtils.getCurrentTime(), AppData.getAppData().getStucode(),
            AppData.getAppData().getCardtype(), AppData.getAppData().getGps(), AppData.getAppData().getImgstr(),
            AppData.getAppData().getClasscode(), AppData.getAppData().getSn(), 0, AppData.getAppData().getStudentName());

            //需要删除签到信息
            operation_Sign_back(idCardInfo);
            Log.i("Gavin", "正常签退");
        } else if (timecompare.TimeCompare(AppData.getAppData().getOutendtime(), AppData.getAppData().getEndtime(), currentTime)) {
            timeflag = 5;
            Log.i("Gavin", "签退时间已过");
        } else {
            timeflag = 6;
            Log.i("Gavin", "不是课堂时间");
        }

    }

    //签到需要显示签到信息
    private void operation_Sign_in(IDCardInfo idCardInfo, String currentTime) {
        //添加签到信息到数据库
        if (QueryCursor(idCardInfo)) {//重复签到
            timeflag = 7;
        } else {
            AddCursor(idCardInfo, currentTime);//签到成功
        }
        //给平台发送签到报文

    }

    //需要删除签到信息
    private void operation_Sign_back(IDCardInfo idCardInfo) {
        if (QueryCursor1(idCardInfo) == 0)//没有签到
        {
            timeflag = 8;
            Log.i("Gavin", "没有签到：");
        } else {
            Log.i("Gavin", "删除信息：" + QueryCursor1(idCardInfo));
            helper.deleteTitle(QueryCursor1(idCardInfo));
            timeflag = 4;
        }
        //删除签到信息
        //给平台发送签退报文
    }


    //////////////////////////////////////////////////////////////////////查询数据库
    private int QueryCursor1(IDCardInfo idCardInfo) {
        int rowid = 0;
        Cursor c = helper.getAllTitles();
        if (c.moveToFirst()) {
            do {
                if (idCardInfo.getId().equals(c.getString(3))) {
                    rowid = Integer.parseInt(c.getString(0));
                }
                Log.i("Gavin", "查询数据" + rowid);
            } while (c.moveToNext());
        }
        return rowid;
    }


    private boolean QueryCursor(IDCardInfo idCardInfo) {

        boolean alreadySign_in = false;//是否已经签到
        Cursor c = helper.getAllTitles();

        if (c.moveToFirst()) {
            do {
                if (idCardInfo.getId().equals(c.getString(3))) {
                    alreadySign_in = true;
                }
                Log.i("Gavin", "查询数据" + c.getString(3));
            } while (c.moveToNext());
        }
        return alreadySign_in;
    }

    /////////////////////////////////////////////////////////////////////向数据库添加数据
    private void AddCursor(IDCardInfo idCardInfo, String currentTime) {

        Cursor c = helper.getAllTitles();
        helper.insertTitle(idCardInfo.getName(),
                idCardInfo.getSex(),
                idCardInfo.getId(),
                Environment.getExternalStorageDirectory() + "/FaceAndroid/Face/" + idCardInfo.getId() + ".jpg",
                Environment.getExternalStorageDirectory() + "/FaceAndroid/card/" + idCardInfo.getId() + ".jpg",
                currentTime);

        if (c.moveToFirst()) {
            do {
                if (c != null) {
                    Log.i("Gavin", "姓名：" + c.getString(1) +
                            "性别：" + c.getString(2) +
                            "身份证号：" + c.getString(3) +
                            "脸地址：" + c.getString(4) +
                            "证地址：" + c.getString(5) +
                            "签到时间：" + c.getString(6));
                }

            } while (c.moveToNext());
        }
    }
}
