package com.runvision.utils;

import com.runvision.bean.AppData;

import org.json.JSONException;
import org.json.JSONObject;

public class JsonTools {

    public String parseJSONWithString(int num) {
        //JSONObject js = new JSONObject();
        JSONObject params = new JSONObject();
        try {
            if(num==1) {//终端设备注册
                params.put("inscode", AppData.getAppData().getInscode());
                params.put("termtype", AppData.getAppData().getTermtype());
                params.put("vender", AppData.getAppData().getVender());
                params.put("model", AppData.getAppData().getModel());
                params.put("gps", AppData.getAppData().getGps());
                params.put("imei", AppData.getAppData().getMac());
                }
            else if(num==2)//终端设备登录
            {
                params.put("devnum", AppData.getAppData().getDevnum());
                params.put("username", AppData.getAppData().getUser());
                params.put("passwd", AppData.getAppData().getPassword());
            }
            else if(num==3)//人脸验证接口
            {
                params.put("devnum", AppData.getAppData().getDevnum());
                params.put("username", AppData.getAppData().getUser());
            }

            else if(num==4)//考勤参数查询接口
            {
                params.put("inscode", AppData.getAppData().getInscode());
                params.put("devnum", AppData.getAppData().getDevnum());
            }
            else if(num==5)//学员培训登录接口
            {
                params.put("devnum", AppData.getAppData().getDevnum());
                params.put("time", AppData.getAppData().getTime());
                params.put("stucode", AppData.getAppData().getStucode());
                params.put("cardtype", AppData.getAppData().getCardtype());
                params.put("gps", AppData.getAppData().getGps());
                params.put("imgstr", AppData.getAppData().getImgstr());
                params.put("classcode", AppData.getAppData().getClasscode());
                params.put("sn", AppData.getAppData().getSn());
                params.put("studentName", AppData.getAppData().getStudentName());

            }

            else if(num==6)//学员培训登出接口
            {
                params.put("devnum", AppData.getAppData().getDevnum());
                params.put("time", AppData.getAppData().getTime());
                params.put("stucode", AppData.getAppData().getStucode());
                params.put("cardtype", AppData.getAppData().getCardtype());
                params.put("gps", AppData.getAppData().getGps());
                params.put("imgstr", AppData.getAppData().getImgstr());
                params.put("classcode", AppData.getAppData().getClasscode());
                params.put("sn", AppData.getAppData().getSn());
                params.put("period", AppData.getAppData().getPeriod());
                params.put("studentName", AppData.getAppData().getStudentName());

            }
           
        } catch (JSONException e) {
            e.printStackTrace();
        }
        final String content = String.valueOf(params);
        return content;
    }
}
