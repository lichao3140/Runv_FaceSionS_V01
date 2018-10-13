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
import com.google.gson.Gson;
import com.runvision.bean.Device;
import com.runvision.bean.DeviceResponse;
import com.runvision.core.Const;
import com.runvision.utils.LocationUtils;
import com.runvision.utils.MACUtil;
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

public class RegisterActivity extends AppCompatActivity {

    @BindView(R.id.et_inscode)
    EditText etInscode;
    @BindView(R.id.et_termtype)
    EditText etTermtype;
    @BindView(R.id.et_vender)
    EditText etVender;
    @BindView(R.id.et_model)
    EditText etModel;
    @BindView(R.id.et_imei)
    EditText etImei;
    @BindView(R.id.bt_register)
    Button btRegister;

    public Location location;
    private SharedPreferencesHelper sharedPreferencesHelper;
    private Context mContext;
    Gson gson = new Gson();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);
        mContext = this;
        location = LocationUtils.getInstance(mContext).showLocation();
        if (location != null) {
            String address = "纬度：" + location.getLatitude() + "经度：" + location.getLongitude();
            Log.i("lichao", "address:" + address);
        }
        sharedPreferencesHelper = new SharedPreferencesHelper(mContext, "deviceInfo");
        initData();
    }

    private void initData() {
        etInscode.setText("3225974581615749");
        etTermtype.setText("1");
        etVender.setText("山东济南");
        etModel.setText("test0001");
        etImei.setText(MACUtil.getLocalMacAddressFromWifiInfo(mContext));
    }

    @OnClick({R.id.bt_register})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.bt_register:
                deviceRegister();
                break;
        }
    }

    /**
     * 考勤终端注册
     */
    private void deviceRegister() {
        OkHttpUtils.postString()
                .url(Const.REGISTER + "ts=" + TimeUtils.getTime13())
                .content(new Gson().toJson(new Device(
                        etInscode.getText().toString().trim(),
                        Integer.valueOf(etTermtype.getText().toString()),
                        etVender.getText().toString().trim(),
                        etModel.getText().toString().trim(),
                        "15|56",
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
                                String privateKey = gsonData.getData().getPrivateKey();
                                String devnum = gsonData.getData().getDevnum();
                                sharedPreferencesHelper.put("privateKey", privateKey);
                                sharedPreferencesHelper.put("devnum",devnum);
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocationUtils.getInstance(mContext).removeLocationUpdatesListener();
    }
}
