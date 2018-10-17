package com.runvision.HttpCallback;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.google.gson.Gson;
import com.runvision.bean.Atnd;
import com.runvision.bean.AtndResponse;
import com.runvision.core.Const;
import com.runvision.firs_facesions.R;
import com.runvision.utils.RSAUtils;
import com.runvision.utils.SPUtil;
import com.runvision.utils.SharedPreferencesHelper;
import com.runvision.utils.TimeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * 考勤参数
 */
public class HttpAtndquery {

    public static void Atndquery(Context context) {
        try {
            String inscode = SPUtil.getString(Const.DEV_INSCODE,"");
            String privateKey = SPUtil.getString(Const.PRIVATE_KEY,"");
            String devnum = SPUtil.getString(Const.DEV_NUM,"");
            String ts = TimeUtils.getTime13();
            String sign = inscode + devnum + ts;
            byte[] ss = sign.getBytes();
            String sign_str = RSAUtils.sign(ss, privateKey);

            Log.i("lichao", "inscode:" + inscode);

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
                            Log.i("lichao", "success:" + response);
                            if (!response.equals("resource/500")) {
                                Gson gson = new Gson();
                                AtndResponse gsonAtnd = gson.fromJson(response, AtndResponse.class);
                                if (gsonAtnd.getErrorcode().equals("0")) {

                                    Toasty.success(context, context.getString(R.string.toast_update_success), Toast.LENGTH_LONG, true).show();
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
}
