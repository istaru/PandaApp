package com.shhb.supermoon.panda.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.alibaba.fastjson.JSON;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.view.GlideCircleTransform;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by superMoon on 2017/8/22.
 */

public class ListAdapter extends BaseAdapter {
    private List<Map<String, Object>> listMap;
    /** 显示时间*/
    private String TYPE = "";
    /** 显示时间*/
    private static final int TYPE_TIME = 0;
    /** 显示内容*/
    private static final int TYPE_CONTENT = 1;

    public ListAdapter(String type) {
        TYPE = type;
        listMap = new ArrayList<>();
    }

    /**
     * 通过异步请求将Banner的数据填充到Adapter
     */
    public void addRecyclerData(List<Map<String, Object>> datas, int mPageIndex) {
        if (mPageIndex == 1) {
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
    public int getItemViewType(int position) {
        if (0 == position) {
            return TYPE_TIME;
        } else {
            String currentDate = listMap.get(position).get("date").toString();
            int prevIndex = position - 1;
            boolean isDifferent = !listMap.get(prevIndex).get("date").toString().equals(currentDate);
            return isDifferent ? TYPE_TIME : TYPE_CONTENT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView;
        int type = getItemViewType(i);
        switch (type) {
            case TYPE_TIME:
                itemView = inflate(viewGroup, R.layout.list_item1);
                return new TimeHolder(itemView);
            case TYPE_CONTENT:
                itemView = inflate(viewGroup, R.layout.list_item2);
                return new ContentHolder(itemView);

        }
        throw new IllegalArgumentException("Wrong type!");
    }

    private class TimeHolder extends ContentHolder {
        TextView timeText;

        /**
         * 获取到banner中的每一个View
         */
        public TimeHolder(View itemView) {
            super(itemView);
            timeText = (TextView) itemView.findViewById(R.id.time_text);
        }
    }

    private class ContentHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView listIcon;
        TextView listContent, listTime, listMoney;

        /**
         * 获取到recycler中的每一个View
         */
        public ContentHolder(View itemView) {
            super(itemView);
            listIcon = (ImageView) itemView.findViewById(R.id.list_icon);
            listContent = (TextView) itemView.findViewById(R.id.list_content);
            listTime = (TextView) itemView.findViewById(R.id.list_time);
            listMoney = (TextView) itemView.findViewById(R.id.list_money);
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
        Map<String, Object> map = listMap.get(position);
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_TIME:
                onBindTimeData((TimeHolder) viewHolder, map);
                break;
            case TYPE_CONTENT:
                onBindContentData((ContentHolder) viewHolder, map);
                break;
        }
    }

    /**
     * 将数据填充到time上
     *
     * @param viewHolder
     * @param map
     */
    private void onBindTimeData(TimeHolder viewHolder, Map<String, Object> map) {
        onBindContentData(viewHolder, map);
        viewHolder.timeText.setText(map.get("date") + "");
    }

    /**
     * 将数据填充到content上
     *
     * @param viewHolder
     * @param map
     */
    private void onBindContentData(ContentHolder viewHolder, Map<String, Object> map) {
        Glide.with(viewHolder.itemView.getContext())
                .load(map.get("head_img"))
                .placeholder(R.mipmap.error_z)
                .error(R.mipmap.error_z)//加载出错的图片
                .priority(Priority.HIGH)//优先加载
                .transform(new GlideCircleTransform(viewHolder.itemView.getContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)//设置缓存策略
                .into(viewHolder.listIcon);
        if(TextUtils.equals("我的收入",TYPE)){
            viewHolder.listContent.setText(map.get("description") + "");
            viewHolder.listMoney.setText("+" + map.get("money"));
            viewHolder.listTime.setText(map.get("createdAt") + "");
        } else {
            viewHolder.listTime.setVisibility(View.GONE);
            viewHolder.listContent.setText(map.get("nickname") + " " + map.get("description"));
            viewHolder.listMoney.setText(map.get("money")+"");
            viewHolder.listMoney.setTextColor(ContextCompat.getColor(viewHolder.itemView.getContext(),R.color.text_color));
        }
    }
}