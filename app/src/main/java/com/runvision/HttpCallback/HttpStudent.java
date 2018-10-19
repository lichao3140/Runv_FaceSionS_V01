package com.runvision.HttpCallback;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;
import com.google.gson.Gson;
import com.runvision.bean.Login;
import com.runvision.bean.LoginResponse;
import com.runvision.bean.Stulogin;
import com.runvision.core.Const;
import com.runvision.firs_facesions.R;
import com.runvision.utils.LogUtil;
import com.runvision.utils.RSAUtils;
import com.runvision.utils.SPUtil;
import com.runvision.utils.TimeUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;
import es.dmoral.toasty.Toasty;
import okhttp3.Call;
import okhttp3.MediaType;

/**
 * 学员登录、登出网络请求
 */
public class HttpStudent {

    /**
     * 学员登录
     * @param context
     * @param devnum
     * @param time
     * @param stucode
     * @param cardtype
     * @param gps
     * @param imgstr
     * @param classcode
     * @param sn
     * @param studentName
     */
    public static void Stulogin(Context context, String devnum, String time, String stucode, String cardtype,
                                String gps, String imgstr, String classcode, String sn, String studentName) {
        try {
            String privateKey = SPUtil.getString(Const.PRIVATE_KEY, "");
            String ts = TimeUtils.getTime13();
            String sign = devnum + time + stucode + cardtype + gps + imgstr + classcode + sn + studentName + ts;
            byte[] ss = sign.getBytes();
            String sign_str = RSAUtils.sign(ss, privateKey);

//            LogUtil.i("lichao", "Stulogin JSON:" + new Gson().toJson(new Stulogin(sign_str, devnum, time, stucode, cardtype, gps, imgstr, classcode, sn, studentName, ts)));

            OkHttpUtils.postString()
                    .url(Const.STULOGIN + "ts=" + TimeUtils.getTime13() + "&sign=" + sign_str)
                    .content(new Gson().toJson(new Stulogin(sign_str, devnum, time, stucode, cardtype, gps, imgstr, classcode, sn, studentName, ts)))
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
                                LoginResponse gsonLogin = gson.fromJson(response, LoginResponse.class);
                                if (gsonLogin.getErrorcode() == 0) {
                                    if (gsonLogin.getMessage().equals("操作成功")) {
                                        Toasty.success(context, context.getString(R.string.toast_update_success), Toast.LENGTH_SHORT, true).show();
                                    } else {
                                        Toasty.warning(context, context.getString(R.string.toast_update_fail) + gsonLogin.getMessage(), Toast.LENGTH_LONG, true).show();
                                    }
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


    /**
     * 学员登出
     * @param context
     * @param devnum
     * @param time
     * @param stucode
     * @param cardtype
     * @param gps
     * @param imgstr
     * @param classcode
     * @param sn
     * @param period
     * @param studentName
     */
    public static void Stulogout(Context context, String devnum, String time, String stucode, String cardtype, String gps, String imgstr, String classcode, String sn, int period, String studentName) {
        try {
            String privateKey = SPUtil.getString(Const.PRIVATE_KEY, "");
            String ts = TimeUtils.getTime13();
            String sign = devnum + time + stucode + cardtype + gps + imgstr + classcode + sn + period + studentName + ts;
            byte[] ss = sign.getBytes();
            String sign_str = RSAUtils.sign(ss, privateKey);

//            LogUtil.i("lichao", "Stulogin JSON:" + new Gson().toJson(new Stulogin(sign_str, devnum, time, stucode, cardtype, gps, imgstr, classcode, sn, period, studentName, ts)));

            OkHttpUtils.postString()
                    .url(Const.STULOGOUT + "ts=" + TimeUtils.getTime13() + "&sign=" + sign_str)
                    .content(new Gson().toJson(new Stulogin(sign_str, devnum, time, stucode, cardtype, gps, imgstr, classcode, sn, period, studentName, ts)))
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
                                LoginResponse gsonLogin = gson.fromJson(response, LoginResponse.class);
                                if (gsonLogin.getErrorcode() == 0) {
                                    if (gsonLogin.getMessage().equals("操作成功")) {
                                        Toasty.success(context, context.getString(R.string.toast_update_success), Toast.LENGTH_SHORT, true).show();
                                    } else {
                                        Toasty.error(context, context.getString(R.string.toast_update_fail) + gsonLogin.getMessage(), Toast.LENGTH_LONG, true).show();
                                    }
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
