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

/**
 * Created by superMoon on 2017/8/17.
 */

public class LoanOrderAdapter extends BaseAdapter{
    private List<Map<String, Object>> listMap = new ArrayList<>();
    /** 请求数据的页码 */
    private int mPageIndex;

    /**
     * 通过异步请求将列表的数据填充到Adapter
     * @param datas
     */
    public void addRecyclerData(List<Map<String, Object>> datas,int pageIndex) {
        this.mPageIndex = pageIndex;
        if(mPageIndex == 1){
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
        View itemView = inflate(viewGroup, R.layout.order_item);
        return new OrderHolder(itemView);
    }

    private class OrderHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        ImageView orderIcon,orderStatus;
        TextView orderName, orderPrice,orderInterest,orderCycle,orderType,orderDetails;
        public OrderHolder(View itemView) {
            super(itemView);
            orderIcon = (ImageView) itemView.findViewById(R.id.order_icon);
            orderStatus = (ImageView) itemView.findViewById(R.id.order_status);
            orderName = (TextView) itemView.findViewById(R.id.order_name);
            orderPrice = (TextView) itemView.findViewById(R.id.order_price);
            orderInterest = (TextView) itemView.findViewById(R.id.order_interest);
            orderCycle = (TextView) itemView.findViewById(R.id.order_cycle);
            orderType = (TextView) itemView.findViewById(R.id.order_type);
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()){
                case 0:
                    break;
            }
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        onBindRecyclerHolder((OrderHolder) viewHolder, position);
    }

    /**
     * 将数据填充到recycler上
     * @param viewHolder
     * @param position
     */
    private void onBindRecyclerHolder(OrderHolder viewHolder, int position) {
        String url = listMap.get(position).get("logo")+"";
        Glide.with(viewHolder.itemView.getContext())
                .load(url)
                .placeholder(R.mipmap.error_z)
                .error(R.mipmap.error_z)//加载出错的图片
                .priority(Priority.HIGH)//优先加载
                .transform(new GlideCircleTransform(viewHolder.itemView.getContext()))
                .diskCacheStrategy(DiskCacheStrategy.NONE)//设置缓存策略
                .into(viewHolder.orderIcon);
        viewHolder.orderName.setText(listMap.get(position).get("name")+"");
        int status = (int) listMap.get(position).get("status");
        if(1 == status) {
            viewHolder.orderStatus.setImageResource(R.mipmap.dk7);
        } else {
            viewHolder.orderStatus.setImageResource(R.mipmap.dk5);
        }
        viewHolder.orderPrice.setText(listMap.get(position).get("price")+"");
        viewHolder.orderInterest.setText(listMap.get(position).get("interest")+"");
        viewHolder.orderCycle.setText(listMap.get(position).get("cycle")+"");
        int type = (int) listMap.get(position).get("type");
        if(1 == type){
            viewHolder.orderType.setText("分期还款");
        } else {
            viewHolder.orderType.setText("全款");
        }
    }
}
