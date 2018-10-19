package com.runvision.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.runvision.bean.Fruit;
import com.runvision.firs_facesions.R;
import java.util.List;

public class FruitAdapter extends ArrayAdapter{
    private final int resourceId;

    public FruitAdapter(Context context, int textViewResourceId, List<Fruit> objects) {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Fruit fruit = (Fruit) getItem(position); // 获取当前项的Fruit实例
        View view = LayoutInflater.from(getContext()).inflate(resourceId, null);//实例化一个对象
        ImageView fruitImage = view.findViewById(R.id.fruit_image);//获取该布局内的图片视图
        TextView fruitName = view.findViewById(R.id.fruit_name);//获取该布局内的文本视图
        fruitImage.setImageResource(fruit.getImageId());//为图片视图设置图片资源
        fruitName.setText(fruit.getName());//为文本视图设置文本内容
        return view;
    }
}
