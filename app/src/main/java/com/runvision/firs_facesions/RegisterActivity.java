package com.runvision.firs_facesions;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.google.gson.Gson;
import com.runvision.bean.Device;
import com.runvision.bean.DeviceResponse;
import com.runvision.core.Const;
import com.runvision.utils.MACUtil;
import com.runvision.utils.SPUtil;
import com.runvision.utils.TimeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.et_inscode)
    EditText etInscode;
    @BindView(R.id.et_termtype)
    EditText etTermtype;
    @BindView(R.id.et_vender)
    EditText etVender;
    @BindView(R.id.et_model)
    EditText etModel;
    @BindView(R.id.et_gps_lon)
    EditText etGpsLon;
    @BindView(R.id.et_gps_lat)
    EditText etGpsLat;
    @BindView(R.id.et_imei)
    EditText etImei;
    @BindView(R.id.bt_register)
    Button btRegister;
    @BindView(R.id.bt_location)
    Button btLocation;

    //高德地图定位
    private AMapLocationClient locationClient = null;
    private AMapLocationClientOption locationOption = null;
    private Context mContext;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mContext = this;
        //初始化定位
        initLocation();
        initData();
    }

    private void initData() {
        etInscode.setText("3225974581615749");
        etTermtype.setText("1");
        etVender.setText("山东济南");
        etModel.setText("test0001");
        etGpsLon.setText("15");
        etGpsLat.setText("16");
        etImei.setText(MACUtil.getLocalMacAddressFromWifiInfo(mContext));
    }

    /**
     * 初始化定位
     */
    private void initLocation() {
        //初始化client
        locationClient = new AMapLocationClient(this.getApplicationContext());
        locationOption = getDefaultOption();
        //设置定位参数
        locationClient.setLocationOption(locationOption);
        // 设置定位监听
        locationClient.setLocationListener(locationListener);
    }

    @OnClick({R.id.bt_register, R.id.bt_location})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_register:
                deviceRegister();
                break;
            case R.id.bt_location:
                startLocation();
                break;
        }
    }

    /**
     * 考勤终端注册
     */
    private void deviceRegister() {
        String gps = etGpsLon.getText().toString().trim() + "|" + etGpsLat.getText().toString().trim();
        OkHttpUtils.postString()
                .url(Const.REGISTER + "ts=" + TimeUtils.getTime13())
                .content(new Gson().toJson(new Device(
                        etInscode.getText().toString().trim(),
                        Integer.valueOf(etTermtype.getText().toString()),
                        etVender.getText().toString().trim(),
                        etModel.getText().toString().trim(),
                        gps,
                        etImei.getText().toString())))
                .mediaType(MediaType.parse("application/json; charset=utf-8"))
                .build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {
                        Toasty.error(mContext, getString(R.string.toast_request_error), Toast.LENGTH_LONG, true).show();
                    }

                    @Override
                    public void onResponse(String response, int id) {
                        Log.i("lichao", "success:" + response);
                        if (!response.equals("resource/500")) {
                            DeviceResponse gsonData = gson.fromJson(response, DeviceResponse.class);
                            if (gsonData.getErrorcode() == 0) {
                                SPUtil.putString(Const.DEV_INSCODE, etInscode.getText().toString().trim());
                                SPUtil.putString(Const.PRIVATE_KEY, gsonData.getData().getPrivateKey());
                                SPUtil.putString(Const.DEV_GPS, gps);
                                SPUtil.putString(Const.DEV_NUM, gsonData.getData().getDevnum());
                                finish();
                                Toasty.success(mContext, getString(R.string.toast_register_success), Toast.LENGTH_SHORT, true).show();
                            } else {
                                Toasty.error(mContext, getString(R.string.toast_register_error_code) + gsonData.getErrorcode(), Toast.LENGTH_LONG, true).show();
                            }
                        } else {
                            Toasty.error(mContext, getString(R.string.toast_server_error), Toast.LENGTH_LONG, true).show();
                        }
                    }
                });
    }

    /**
     * 默认的定位参数
     *
     * @return
     */
    private AMapLocationClientOption getDefaultOption() {
        AMapLocationClientOption mOption = new AMapLocationClientOption();
        mOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mOption.setGpsFirst(false);//可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mOption.setHttpTimeOut(30000);//可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mOption.setInterval(2000);//可选，设置定位间隔。默认为2秒
        mOption.setNeedAddress(true);//可选，设置是否返回逆地理地址信息。默认是true
        mOption.setOnceLocation(false);//可选，设置是否单次定位。默认是false
        mOption.setOnceLocationLatest(false);//可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP);//可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mOption.setSensorEnable(false);//可选，设置是否使用传感器。默认是false
        mOption.setWifiScan(true); //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mOption.setLocationCacheEnable(true); //可选，设置是否使用缓存定位，默认为true
        mOption.setGeoLanguage(AMapLocationClientOption.GeoLanguage.DEFAULT);//可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        return mOption;
    }

    /**
     * 开始定位
     */
    private void startLocation(){
        // 设置定位参数
        locationClient.setLocationOption(locationOption);
        // 启动定位
        locationClient.startLocation();
    }

    /**
     * 定位监听
     */
    AMapLocationListener locationListener = location -> {
        if (null != location) {
            StringBuffer sb = new StringBuffer();
            //errCode等于0代表定位成功，其他的为定位失败，具体的可以参照官网定位错误码说明
            if (location.getErrorCode() == 0) {
                sb.append("定位成功" + "\n");
                sb.append("定位类型: " + location.getLocationType() + "\n");
                sb.append("经    度    : " + location.getLongitude() + "\n");
                sb.append("纬    度    : " + location.getLatitude() + "\n");
                sb.append("精    度    : " + location.getAccuracy() + "米" + "\n");
                sb.append("提供者    : " + location.getProvider() + "\n");
                sb.append("速    度    : " + location.getSpeed() + "米/秒" + "\n");
                sb.append("角    度    : " + location.getBearing() + "\n");
                sb.append("星    数    : " + location.getSatellites() + "\n");
                sb.append("国    家    : " + location.getCountry() + "\n");
                sb.append("省            : " + location.getProvince() + "\n");
                sb.append("市            : " + location.getCity() + "\n");
                sb.append("城市编码 : " + location.getCityCode() + "\n");
                sb.append("区            : " + location.getDistrict() + "\n");
                sb.append("区域 码   : " + location.getAdCode() + "\n");
                sb.append("地    址    : " + location.getAddress() + "\n");
                sb.append("兴趣点    : " + location.getPoiName() + "\n");
                etGpsLon.setText(location.getLongitude() + "");
                etGpsLat.setText(location.getLatitude() + "");
            } else {
                //定位失败
                sb.append("定位失败" + "\n");
                sb.append("错误码:" + location.getErrorCode() + "\n");
                sb.append("错误信息:" + location.getErrorInfo() + "\n");
                sb.append("错误描述:" + location.getLocationDetail() + "\n");
                Toasty.error(mContext, "定位失败," + location.getErrorInfo(), Toast.LENGTH_LONG, true).show();
            }
            //解析定位结果
            String result = sb.toString();
            Log.i("lichao", "解析定位结果:" + result);
        } else {
            Toasty.error(mContext, "定位失败,loc is null", Toast.LENGTH_LONG, true).show();
        }
    };

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (null != locationClient) {
            /**
             * 如果AMapLocationClient是在当前Activity实例化的，
             * 在Activity的onDestroy中一定要执行AMapLocationClient的onDestroy
             */
            locationClient.onDestroy();
            locationClient = null;
            locationOption = null;
        }
    }
}
