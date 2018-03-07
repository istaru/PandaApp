package com.shhb.supermoon.panda.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.shhb.supermoon.panda.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by superMoon on 2017/8/17.
 */

public class MessageAdapter extends BaseAdapter{
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
        View itemView = inflate(viewGroup, R.layout.message_item);
        return new MessageHolder(itemView);
    }

    private class MessageHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView content,time;
        public MessageHolder(View itemView) {
            super(itemView);
            content = (TextView) itemView.findViewById(R.id.content);
            time = (TextView) itemView.findViewById(R.id.time);
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
        onBindRecyclerHolder((MessageHolder) viewHolder, position);
    }

    /**
     * 将数据填充到recycler上
     * @param viewHolder
     * @param position
     */
    private void onBindRecyclerHolder(MessageHolder viewHolder, int position) {
        viewHolder.content.setText(listMap.get(position).get("content")+"");
        viewHolder.time.setText(listMap.get(position).get("createdAt")+"");
    }
}
