package com.shhb.supermoon.panda.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.List;
import java.util.Map;

/**
 * Created by superMoon on 2017/8/17.
 */

public class BaseAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{
    public OnClickListener onClickListener;

    @Override
    public int getItemCount() {
        return 0;
    }

    /**
     * 根据这个方法填充不同的布局
     * @param position
     * @return
     */
    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    /**
     * 填充页面的封装
     * @param parent
     * @param layoutRes
     * @return
     */
    public View inflate(ViewGroup parent, int layoutRes) {
        return LayoutInflater.from(parent.getContext()).inflate(layoutRes, parent, false);
    }

    /**
     * 填充页面
     * @param parent
     * @param viewType
     * @return
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }

    /**
     * 设置空布局
     */
    public class EmptyHolder extends RecyclerView.ViewHolder {

        public EmptyHolder(View itemView) {
            super(itemView);
        }
    }

    /**
     * 绑定数据
     * @param holder
     * @param position
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }

    /** 定义所有单击事件接口 */
    public interface OnClickListener {
        void onClick(View view, int position, List<Map<String,Object>> listMap);
        void onClick(View view);
    }

    /** 所有单击事件的处理方法 */
    public void setOnClickListener(final OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
