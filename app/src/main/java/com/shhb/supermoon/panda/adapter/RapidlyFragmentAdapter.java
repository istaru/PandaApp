package com.shhb.supermoon.panda.adapter;

import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

/**
 * Created by superMoon on 2017/8/2.
 */

public class RapidlyFragmentAdapter extends BaseAdapter {
    /**
     * 请求数据的页码
     */
    private int mPageIndex;
    /**
     * 极速贷预估额度
     */
    private final int TYPE0_1 = 0;
    /**
     * 极速贷预估列表
     */
    private final int TYPE0_2 = 1;
    private final int TYPE0_3 = 2;
    /**
     * recycler的数据
     */
    private List<Map<String, Object>> listMap;

    public RapidlyFragmentAdapter() {
        listMap = new ArrayList<>();
    }

    /**
     * 通过异步请求将列表的数据填充到Adapter
     *
     * @param datas
     */
    public void addRecyclerData(List<Map<String, Object>> datas, int pageIndex) {
        this.mPageIndex = pageIndex;
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
//        if(position == getItemCount() - 1){
//            return TYPE0_3;
//        } else {
        if (0 == position) {
            return TYPE0_1;
        } else {
            return TYPE0_2;
        }
//        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView;
        int type = getItemViewType(i);
        switch (type) {
            case TYPE0_1:
                itemView = inflate(viewGroup, R.layout.fragment2_view1);
                return new QuotaHolder(itemView);
            case TYPE0_2:
                itemView = inflate(viewGroup, R.layout.fragment2_view2);
                return new RecyclerHolder(itemView);
//            case TYPE0_3:
//                itemView = inflate(viewGroup,R.layout.footer_view);
//                return new FooterHolder(itemView);
        }
        throw new IllegalArgumentException("Wrong type!");
    }

    private class QuotaHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView creditLine;
        Button yged;

        /**
         * 获取预估额度中的每一个View
         */
        public QuotaHolder(View itemView) {
            super(itemView);
            creditLine = (TextView) itemView.findViewById(R.id.credit_line);
            yged = (Button) itemView.findViewById(R.id.yged);
            yged.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.yged:
                    onClickListener.onClick(view);
                    break;
            }
        }
    }

    private class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        ImageView icon;
        TextView name, cycle, status, quota, value, day;

        /**
         * 获取极速贷列表中的每一个View
         */
        public RecyclerHolder(View itemView) {
            super(itemView);
            icon = (ImageView) itemView.findViewById(R.id.icon);
            name = (TextView) itemView.findViewById(R.id.name);
            cycle = (TextView) itemView.findViewById(R.id.cycle);
            status = (TextView) itemView.findViewById(R.id.status);
            quota = (TextView) itemView.findViewById(R.id.quota);
            value = (TextView) itemView.findViewById(R.id.value);
            day = (TextView) itemView.findViewById(R.id.day);
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

    private class FooterHolder extends RecyclerHolder{
        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case TYPE0_1:
                onBindQuotaHolder((QuotaHolder) viewHolder, position);
                break;
            case TYPE0_2:
                onBindRecyclerHolder((RecyclerHolder) viewHolder, position);
                break;
        }
    }

    /**
     * 将数据填充到预估额度上
     *
     * @param viewHolder
     * @param position
     */
    private void onBindQuotaHolder(QuotaHolder viewHolder, int position) {
        viewHolder.creditLine.setText(listMap.get(position).get("creditLine") + "");
    }

    /**
     * 将数据填充到recycler上
     *
     * @param viewHolder
     * @param position
     */
    private void onBindRecyclerHolder(RecyclerHolder viewHolder, int position) {
        String url = listMap.get(position).get("logo") + "";
        Glide.with(viewHolder.itemView.getContext())
                .load(url)
                .placeholder(R.mipmap.error_z)
                .error(R.mipmap.error_z)//加载出错的图片
                .priority(Priority.HIGH)//优先加载
                .transform(new GlideCircleTransform(viewHolder.itemView.getContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)//设置缓存策略
                .into(viewHolder.icon);
        viewHolder.name.setText(listMap.get(position).get("name") + "");
        viewHolder.cycle.setText("期限" + listMap.get(position).get("cycle_min") + "~" + listMap.get(position).get("cycle_max") + "期");
        String status = listMap.get(position).get("status") + "";
        if (TextUtils.equals("1", status)) {
            viewHolder.status.setTextColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.btn_yellow_s));
            viewHolder.status.setBackgroundResource(R.drawable.btn_yellow_bg);
            viewHolder.status.setText("申请贷款");
        } else {
            viewHolder.status.setTextColor(ContextCompat.getColor(viewHolder.itemView.getContext(), R.color.webBg));
            viewHolder.status.setBackgroundResource(R.drawable.btn_gray_bg);
            viewHolder.status.setText("人数已满");
        }
        viewHolder.quota.setText(listMap.get(position).get("money_max") + "-" + listMap.get(position).get("money_min") + "");
        String rete = listMap.get(position).get("rate") + "";
        if (rete.contains("%")) {
            viewHolder.day.setVisibility(View.GONE);
        } else {
            viewHolder.day.setVisibility(View.VISIBLE);
        }
        viewHolder.value.setText(rete);
    }
}
