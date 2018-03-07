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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.activity.MyQuotaActivity;
import com.shhb.supermoon.panda.activity.WebActivity;
import com.shhb.supermoon.panda.adapter.BaseAdapter;
import com.shhb.supermoon.panda.adapter.RapidlyFragmentAdapter;
import com.shhb.supermoon.panda.tools.BaseTools;
import com.shhb.supermoon.panda.tools.Constants;
import com.shhb.supermoon.panda.tools.OkHttpUtils;
import com.shhb.supermoon.panda.tools.PrefShared;
import com.shhb.supermoon.panda.view.DividerItemDecoration;
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

public class RapidlyFragment extends BaseFragment implements OnRefreshListener, OnLoadMoreListener, BaseAdapter.OnClickListener {
    public static final String REFRESH_TYPE = "refresh_type";
    private RecyclerView recyclerView;
    private RapidlyFragmentAdapter mAdapter;
    /**
     * 请求页码
     */
    private int mPageIndex = 1;
    /**
     * 每页请求数量
     */
    private final static int mPageNum = 10;


    public static Fragment newInstance(int type) {
        RapidlyFragment fragment = new RapidlyFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(REFRESH_TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new RapidlyFragmentAdapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.recycler_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        swipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);

        recyclerView = (RecyclerView) view.findViewById(R.id.swipe_target);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(context, DividerItemDecoration.BOTH_SET, 6, ContextCompat.getColor(context, R.color.webBg))
        );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(this);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                RecyclerView.LayoutManager mLayoutManager = recyclerView.getLayoutManager();
                int lastVisibleItem = ((LinearLayoutManager)mLayoutManager).findLastVisibleItemPosition();
                int totalItemCount = mLayoutManager.getItemCount();
                // dy>0 表示向下滑动
                if (lastVisibleItem >= totalItemCount - 4 && dy > 0) {//lastVisibleItem >= totalItemCount - 4 表示剩下4个item自动加载
                    if(!swipeToLoadLayout.isLoadingMore()){
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
     * 查询极速贷数据
     */
    private void findByData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msys", "1");
        jsonObject.put("uid", PrefShared.getString(context, "userId"));
        jsonObject.put("page_size", mPageNum);
        jsonObject.put("current_page", mPageIndex);
        jsonObject.put("type", "2");//2、极速贷
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
                    JSONObject jsonObject = BaseTools.jsonObject(context, response, "查询极速贷数据");
                    final List<Map<String, Object>> listMap = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    if (mPageIndex == 1) {
                        Map<String, Object> cMap = new HashMap<>();
                        cMap.put("creditLine", jsonObject.getString("creditLine"));
                        listMap.add(cMap);
                    }
                    for (int i = 0; i < jsonArray.size(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        Map<String, Object> map = new HashMap<>();
                        map.put("logo", jsonObject.getString("logo"));
                        map.put("name", jsonObject.getString("name"));
                        map.put("cycle_min", jsonObject.getString("cycle_min"));
                        map.put("cycle_max", jsonObject.getString("cycle_max"));
                        map.put("status", jsonObject.getString("status"));
                        map.put("money_min", jsonObject.getString("money_min"));
                        map.put("money_max", jsonObject.getString("money_max"));
                        map.put("rate", jsonObject.getString("rate"));
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
        MobclickAgent.onPageStart("RapidlyFragment"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("RapidlyFragment");
    }
}
