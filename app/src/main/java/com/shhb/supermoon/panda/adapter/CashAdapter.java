package com.shhb.supermoon.panda.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.view.GlideCircleTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Created by superMoon on 2017/8/17.
 */

public class CashAdapter extends BaseAdapter{
    private List<Map<String, Object>> listMap = new ArrayList<>();
    /**
     * 通过异步请求将列表的数据填充到Adapter
     * @param datas
     */
    public void addRecyclerData(List<Map<String, Object>> datas,int pageIndex) {
        if(pageIndex == 1){
            listMap.clear();
        }
        listMap.addAll(datas);
        notifyDataSetChanged();
    }

    @Override
    public int getItemCount() {
        return listMap.size();
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View itemView = inflate(viewGroup, R.layout.cash_item);
        return new MessageHolder(itemView);
    }

    private class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView cashIcon;
        TextView cashContent,cashMoney,cashTime;
        public MessageHolder(View itemView) {
            super(itemView);
            cashIcon = (ImageView) itemView.findViewById(R.id.cash_icon);
            cashContent = (TextView) itemView.findViewById(R.id.cash_content);
            cashMoney = (TextView) itemView.findViewById(R.id.cash_money);
            cashTime = (TextView) itemView.findViewById(R.id.cash_time);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                default:
                    onClickListener.onClick(view, getPosition(), listMap);
                    break;
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Map<String,Object> map = listMap.get(position);
        onBindRecyclerHolder((MessageHolder) viewHolder, map);
    }

    /**
     * 将数据填充到recycler上
     * @param viewHolder
     * @param map
     */
    private void onBindRecyclerHolder(MessageHolder viewHolder, Map<String, Object> map) {
        int status = Integer.parseInt(map.get("status")+"");
        if(5 == status){
            viewHolder.cashIcon.setImageResource(R.mipmap.tx01);
        } else {
            viewHolder.cashIcon.setImageResource(R.mipmap.tx02);
        }
        viewHolder.cashContent.setText(map.get("comment")+" ");
        viewHolder.cashMoney.setText(map.get("price")+" ");
        viewHolder.cashTime.setText(map.get("createdAt")+"");
    }
}
