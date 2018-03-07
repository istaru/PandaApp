package com.shhb.supermoon.panda.adapter;

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

public class SmallFragmentAdapter extends BaseAdapter {
    /**
     * 请求数据的页码
     */
    private int mPageIndex;
    /**
     * 小额贷预估列表
     */
    private final int TYPE0_2 = 1;
    /**
     * recycler的数据
     */
    private List<Map<String, Object>> listMap;

    public SmallFragmentAdapter() {
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
        return TYPE0_2;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView;
        int type = getItemViewType(i);
        switch (type) {
            case TYPE0_2:
                itemView = inflate(viewGroup, R.layout.fragment2_2_view2);
                return new RecyclerHolder(itemView);
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
        ImageView fr2Icon;
        TextView fr2Name, fr2People, fr2Type, fr2Rate, fr2Describe;

        /**
         * 获取极速贷列表中的每一个View
         */
        public RecyclerHolder(View itemView) {
            super(itemView);
            fr2Icon = (ImageView) itemView.findViewById(R.id.fr2_icon);
            fr2Name = (TextView) itemView.findViewById(R.id.fr2_name);
            fr2People = (TextView) itemView.findViewById(R.id.fr2_people);
            fr2Type = (TextView) itemView.findViewById(R.id.fr2_type);
            fr2Rate = (TextView) itemView.findViewById(R.id.fr2_rate);
            fr2Describe = (TextView) itemView.findViewById(R.id.fr2_describe);
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

    private class FooterHolder extends RecyclerHolder {
        public FooterHolder(View itemView) {
            super(itemView);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int type = getItemViewType(position);
        switch (type) {
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
                .into(viewHolder.fr2Icon);
        viewHolder.fr2Name.setText(listMap.get(position).get("name") + "");
        viewHolder.fr2People.setText(listMap.get(position).get("application_num") + "人申请/" + listMap.get(position).get("loanTime") + "天放款");
        viewHolder.fr2Rate.setText(listMap.get(position).get("rate") + "");
        String repayment = listMap.get(position).get("repayment_means") + "";
        String describe = "";
        if (TextUtils.equals("1", repayment)) {
            describe = "到期还款";
        } else {
            describe = "分期还款";
        }
        viewHolder.fr2Describe.setText(listMap.get(position).get("money_min") + "-" + listMap.get(position).get("money_max") + " " + describe);
    }
}
