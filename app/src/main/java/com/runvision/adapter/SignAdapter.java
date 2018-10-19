package com.runvision.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.runvision.bean.Sign;
import com.runvision.firs_facesions.R;
import com.runvision.utils.TimeUtils;

import java.util.List;


public class SignAdapter extends ArrayAdapter{
    private final int resourceId;

    public SignAdapter(Context context, int textViewResourceId, List<Sign> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    @SuppressLint("SetTextI18n")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Sign sigin = (Sign) getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        ImageView siginImage = view.findViewById(R.id.signin_image);//获取该布局内的图片视图
        TextView siginName = view.findViewById(R.id.signin_name);//获取该布局内的文本视图
        TextView siginGender = view.findViewById(R.id.signin_gender);//获取该布局内的文本视图
        TextView siginCardNo = view.findViewById(R.id.signin_cardNo);//获取该布局内的文本视图
        TextView siginTime = view.findViewById(R.id.signin_time);//获取该布局内的文本视图
        siginImage.setImageBitmap(sigin.getImageId());//为图片视图设置图片资源
        siginName.setText(sigin.getName());//为文本视图设置文本内容
        siginGender.setText(sigin.getGender());
        siginCardNo.setText(sigin.getCardNo());
        siginTime.setText(TimeUtils.getYearMonth() + sigin.getSigntime());
        return view;
    }
}
