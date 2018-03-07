package com.shhb.supermoon.panda.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.model.BannerInfo;
import com.shhb.supermoon.panda.view.CustomViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by superMoon on 2017/8/2.
 */

public class Fragment1Adapter extends BaseAdapter {
    private List<Map<String, Object>> listMap;
    /**
     * banner的View标识
     */
    private static final int TYPE_BANNER = 0;
    /**
     * recycler的ItemView标识
     */
    private static final int TYPE_RECYCLER = 1;
    private LoopViewPagerAdapter mPagerAdapter;

    public Fragment1Adapter() {
        listMap = new ArrayList<>();
    }

    /**
     * 通过异步请求将Banner的数据填充到Adapter
     */
    public void addHomeData(List<Map<String, Object>> datas) {
        listMap.clear();
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
            return TYPE_BANNER;
        } else {
            return TYPE_RECYCLER;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View itemView;
        int type = getItemViewType(i);
        switch (type) {
            case TYPE_BANNER:
                itemView = inflate(viewGroup, R.layout.fragment1_view1);
                return new BannerHolder(itemView);
            case TYPE_RECYCLER:
                itemView = inflate(viewGroup, R.layout.fragment1_view2);
                return new RecyclerHolder(itemView);
        }
        throw new IllegalArgumentException("Wrong type!");
    }

    private class BannerHolder extends RecyclerView.ViewHolder {
        RelativeLayout bannerG;
        CustomViewPager viewPager;
        ViewGroup indicators;

        /**
         * 获取到banner中的每一个View
         */
        public BannerHolder(View itemView) {
            super(itemView);
            viewPager = (CustomViewPager) itemView.findViewById(R.id.viewPager);
            viewPager.setScanScroll(true);
            indicators = (ViewGroup) itemView.findViewById(R.id.indicators);
        }
    }

    private class RecyclerHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView credit, quota, privilege, success;
        Button frtbtn;

        /**
         * 获取到recycler中的每一个View
         */
        public RecyclerHolder(View itemView) {
            super(itemView);
            credit = (TextView) itemView.findViewById(R.id.credit);
            quota = (TextView) itemView.findViewById(R.id.quota);
            privilege = (TextView) itemView.findViewById(R.id.privilege);
            success = (TextView) itemView.findViewById(R.id.success);
            frtbtn = (Button) itemView.findViewById(R.id.frt_btn);
            frtbtn.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case R.id.frt_btn:
                    onClickListener.onClick(view, getPosition(), listMap);
                    break;
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        int type = getItemViewType(position);
        switch (type) {
            case TYPE_BANNER:
                onBindBannerHolder((BannerHolder) viewHolder, position);
                break;
            case TYPE_RECYCLER:
                onBindRecyclerHolder((RecyclerHolder) viewHolder, position);
                break;
        }
    }

    /**
     * 将数据填充到banner上
     *
     * @param viewHolder
     * @param position
     */
    private void onBindBannerHolder(BannerHolder viewHolder, int position) {
        List<BannerInfo> bannerInfos = (List<BannerInfo>) listMap.get(position).get("banners");
        if (viewHolder.viewPager.getAdapter() == null) {
            mPagerAdapter = new LoopViewPagerAdapter(viewHolder.viewPager, viewHolder.indicators);
            viewHolder.viewPager.setAdapter(mPagerAdapter);
            viewHolder.viewPager.addOnPageChangeListener(mPagerAdapter);
            mPagerAdapter.setList(bannerInfos);
        } else {
            mPagerAdapter.setList(bannerInfos);
        }
    }

    /**
     * 将数据填充到recycler上
     *
     * @param viewHolder
     * @param position
     */
    private void onBindRecyclerHolder(RecyclerHolder viewHolder, int position) {
        viewHolder.credit.setText(listMap.get(position).get("creditRating") + "");
        viewHolder.quota.setText(listMap.get(position).get("creditLine") + "");
//        viewHolder.privilege.setText(listMap.get(position).get("privilege") + "个");
//        viewHolder.success.setText(listMap.get(position).get("successRate") + "");
    }
}
