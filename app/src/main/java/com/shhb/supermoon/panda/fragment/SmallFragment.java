package com.shhb.supermoon.panda.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.activity.MyQuotaActivity;
import com.shhb.supermoon.panda.activity.WebActivity;
import com.shhb.supermoon.panda.adapter.RapidlyFragmentAdapter;
import com.shhb.supermoon.panda.adapter.SmallFragmentAdapter;
import com.shhb.supermoon.panda.tools.BaseTools;
import com.shhb.supermoon.panda.tools.Constants;
import com.shhb.supermoon.panda.tools.OkHttpUtils;
import com.shhb.supermoon.panda.tools.PrefShared;
import com.shhb.supermoon.panda.view.DividerItemDecoration;
import com.shhb.supermoon.panda.view.SpinnerView;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by superMoon on 2017/8/2.
 */

public class SmallFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener, RapidlyFragmentAdapter.OnClickListener {
    public static final String REFRESH_TYPE = "refresh_type";
    private SwipeToLoadLayout swipeToLoadLayout;
    private SpinnerView spinner1, spinner2, spinner3;
    private RecyclerView recyclerView;
    private SmallFragmentAdapter mAdapter;
    /**
     * 请求页码
     */
    private int mPageIndex = 1;
    /**
     * 每页请求数量
     */
    private final static int mPageNum = 10;
    /**
     * 区分是否第二次进来
     */
    private int lifeCycle = 1;
    private String type1,type2,type3,type4;

    public static Fragment newInstance(int type) {
        SmallFragment fragment = new SmallFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(REFRESH_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new SmallFragmentAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment2_2_view1, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);

        spinner1 = (SpinnerView) view.findViewById(R.id.spinner1);
        List<String> items1 = new ArrayList<>();
        items1.add("全部");
        items1.add("到期还款");
        items1.add("分期还款");
        spinner1.init(context, items1);
        spinner1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner1.showWindow();
            }
        });
        spinner1.setOnSpinnerItemClickListener(new SpinnerView.OnSpinnerItemClickListener() {
            @Override
            public void OnSpinnerItemClick(AdapterView<?> parent, View view, int position, long id, List<String> list) {
                type1 = position+"";
                findByData();
            }
        });

        spinner2 = (SpinnerView) view.findViewById(R.id.spinner2);
        List<String> items2 = new ArrayList<>();
        items2.add("最新");
        items2.add("最热");
        spinner2.init(context, items2);
        spinner2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner2.showWindow();
            }
        });
        spinner2.setOnSpinnerItemClickListener(new SpinnerView.OnSpinnerItemClickListener() {
            @Override
            public void OnSpinnerItemClick(AdapterView<?> parent, View view, int position, long id, List<String> list) {
                type2 = spinner2.getSelectedItemPosition()+"";
                findByData();
            }
        });

        spinner3 = (SpinnerView) view.findViewById(R.id.spinner3);
        List<String> items3 = new ArrayList<>();
        items3.add("全部");
        items3.add("0-1000");
        items3.add("1000-5000");
        items3.add("5000及以上");
        spinner3.init(context, items3);
        spinner3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinner3.showWindow();
            }
        });
        spinner3.setOnSpinnerItemClickListener(new SpinnerView.OnSpinnerItemClickListener() {
            @Override
            public void OnSpinnerItemClick(AdapterView<?> parent, View view, int position, long id, List<String> list) {
                if(1 == position){
                    type3 = "0";
                    type4 = "1000";
                    findByData();
                } else if(2 == position){
                    type3 = "1000";
                    type4 = "5000";
                    findByData();
                } else if(3 == position){
                    type3 = "";
                    type4 = "5000";
                    findByData();
                } else {
                    type3 = "";
                    type4 = "";
                    findByData();
                }
            }
        });

        recyclerView = (RecyclerView) view.findViewById(R.id.swipe_target);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(context, DividerItemDecoration.BOTH_SET, 2, ContextCompat.getColor(context, R.color.webBg))
        );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager mLayoutManager = recyclerView.getLayoutManager();
                int lastVisibleItem = ((LinearLayoutManager) mLayoutManager).findLastVisibleItemPosition();
                int totalItemCount = mLayoutManager.getItemCount();
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {//lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
                    if (!swipeToLoadLayout.isLoadingMore()) {
                        swipeToLoadLayout.setLoadingMore(true);
                    }
                }
            }
        });

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!ViewCompat.canScrollVertically(recyclerView, 1)) {
                        swipeToLoadLayout.setLoadingMore(true);
                    }
                }
            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (lifeCycle == 1) {
            swipeToLoadLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeToLoadLayout.setRefreshing(true);
                    lifeCycle = 1;
                }
            });
        }
    }

    /**
     * 更新贷款列表
     */
    @Override
    public void onRefresh() {
        mPageIndex = 1;
        swipeToLoadLayout.setRefreshing(true);
        findByData();
    }

    /**
     * 加载贷款列表
     */
    @Override
    public void onLoadMore() {
        ++mPageIndex;
        swipeToLoadLayout.setLoadingMore(true);
        findByData();
    }

    /**
     * 查询小额贷数据
     */
    private void findByData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msys", "1");
        jsonObject.put("uid", PrefShared.getString(context, "userId"));
        jsonObject.put("page_size", mPageNum);
        jsonObject.put("current_page", mPageIndex);
        jsonObject.put("type", "3");//3、小额贷
        jsonObject.put("repayment_means", type1);
        jsonObject.put("sorting", type2);
        jsonObject.put("money_min", type3);
        jsonObject.put("money_max", type4);
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        new OkHttpUtils(20).postEnqueue(Constants.FIND_BY_SPEED_LOAN, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cLoading(swipeToLoadLayout);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    cLoading(swipeToLoadLayout);
                    JSONObject jsonObject = BaseTools.jsonObject(context, response, "查询小额贷数据");
                    final List<Map<String, Object>> listMap = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        Map<String, Object> map = new HashMap<>();
                        map.put("logo", jsonObject.getString("logo"));
                        map.put("name", jsonObject.getString("name"));
                        map.put("application_num", jsonObject.getString("application_num"));
                        map.put("loanTime", jsonObject.getString("loanTime"));
                        map.put("rate", jsonObject.getString("rate"));
                        map.put("money_min", jsonObject.getString("money_min"));
                        map.put("money_max", jsonObject.getString("money_max"));
                        map.put("repayment_means", jsonObject.getString("repayment_means"));
                        map.put("detail", jsonObject.getString("detail"));
                        map.put("meaterial", jsonObject.getString("meaterial"));
                        map.put("url", jsonObject.getString("url"));
                        map.put("type", jsonObject.getString("type"));
                        listMap.add(map);
                    }
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.addRecyclerData(listMap, mPageIndex);
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, parameter);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.yged:
                intent = new Intent(context, MyQuotaActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClick(View view, int position, List<Map<String, Object>> listMap) {
        Intent intent;
        switch (view.getId()) {
            default:
                intent = new Intent(context, WebActivity.class);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", listMap.get(position).get("name") + "");
                jsonObject.put("cycle_min", listMap.get(position).get("cycle_min") + "");
                jsonObject.put("cycle_max", listMap.get(position).get("cycle_max") + "");
                jsonObject.put("money_min", listMap.get(position).get("money_min") + "");
                jsonObject.put("money_max", listMap.get(position).get("money_max") + "");
                jsonObject.put("rate",listMap.get(position).get("rate") + "");
                jsonObject.put("detail",listMap.get(position).get("detail") + "");
                jsonObject.put("material",listMap.get(position).get("meaterial") + "");
                jsonObject.put("url",listMap.get(position).get("url") + "");
                String type = listMap.get(position).get("type") + "";
                if(TextUtils.equals("1",type)){//我们的详情页
                    intent.putExtra("url", Constants.LOAN_HTML);
                } else {//第三方的
                    intent.putExtra("url", listMap.get(position).get("url") + "");
                }
                intent.putExtra("title", "");
                intent.putExtra("data", jsonObject.toString());
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("SmallFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("SmallFragment");
    }
}
