package com.runvision.adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import com.runvision.firs_facesions.R;
import java.util.List;

/**
 * Created by ChaoLi on 2018/10/14 0014 - 17:06
 * Email: lichao3140@gmail.com
 * Version: v1.0
 */
public class MenuCardAdapter extends BaseAdapter<MenuCardAdapter.DefaultViewHolder>  {

    private List<String> mDataList;

    public MenuCardAdapter(Context context) {
        super(context);
    }

    @Override
    public void notifyDataSetChanged(List<String> dataList) {
        this.mDataList = dataList;
        super.notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    @Override
    public DefaultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new DefaultViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu_card, parent, false));
    }

    @Override
    public void onBindViewHolder(DefaultViewHolder holder, int position) {
        holder.setData(mDataList.get(position));
    }

    class DefaultViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle;

        public DefaultViewHolder(View itemView) {
            super(itemView);
            tvTitle = (TextView) itemView.findViewById(R.id.tv_title);

            ((CardView) itemView).getChildAt(0).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(v.getContext(), "第" + getAdapterPosition() + "个", Toast.LENGTH_SHORT).show();
                }
            });
        }

        public void setData(String title) {
            this.tvTitle.setText(title);
        }
    }

}
