package com.shhb.supermoon.panda.activity;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aspsine.swipetoloadlayout.OnLoadMoreListener;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.jaeger.library.StatusBarUtil;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.adapter.BaseAdapter;
import com.shhb.supermoon.panda.adapter.LoanOrderAdapter;
import com.shhb.supermoon.panda.tools.BaseTools;
import com.shhb.supermoon.panda.tools.Constants;
import com.shhb.supermoon.panda.tools.OkHttpUtils;
import com.shhb.supermoon.panda.tools.PrefShared;
import com.shhb.supermoon.panda.view.DividerItemDecoration;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by superMoon on 2017/8/17.
 */

public class LoanOrderActivity extends BaseActivity implements View.OnClickListener, BaseAdapter.OnClickListener, OnRefreshListener, OnLoadMoreListener {

    private RecyclerView recyclerView;
    private LoanOrderAdapter mAdapter;
    /**
     * 请求页码
     */
    private int mPageIndex = 1;
    /**
     * 每页请求数量
     */
    private final static int mPageNum = 10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.order_activity);
        initView();
    }

    private void initView() {
        webView_title = (TextView) findViewById(R.id.webView_title);
        webView_title.setText("我的贷款订单");
        onBack = (LinearLayout) findViewById(R.id.onBack);
        onBack.setOnClickListener(this);

        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setOnRefreshListener(this);
        swipeToLoadLayout.setOnLoadMoreListener(this);

        recyclerView = (RecyclerView) findViewById(R.id.swipe_target);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(context, DividerItemDecoration.BOTH_SET, 6, ContextCompat.getColor(context, R.color.webBg))
        );
        recyclerView.setLayoutManager(layoutManager);
        mAdapter = new LoanOrderAdapter();
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
    protected void onStart() {
        super.onStart();
        if(lifeCycle == 1){
            swipeToLoadLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeToLoadLayout.setRefreshing(true);
                    lifeCycle = 2;
                }
            });
        }
    }

    /**
     * 更新订单列表
     */
    @Override
    public void onRefresh() {
        mPageIndex = 1;
        swipeToLoadLayout.setRefreshing(true);
        findByData();
    }

    /**
     * 加载订单列表
     */
    @Override
    public void onLoadMore() {
        ++mPageIndex;
        swipeToLoadLayout.setLoadingMore(true);
        findByData();
    }

    /**
     * 查询贷款订单数据
     */
    private void findByData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msys", "1");
        jsonObject.put("uid", PrefShared.getString(context, "userId"));
        jsonObject.put("page_size", mPageNum);
        jsonObject.put("current_page", mPageIndex);
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        new OkHttpUtils(20).postEnqueue(Constants.FIND_LOAN_ORDER, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cLoading(swipeToLoadLayout);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = BaseTools.jsonObject(context, response, "查询贷款订单数据");
                    final List<Map<String, Object>> listMap = new ArrayList<>();
                    JSONArray jsonArray = jsonObject.getJSONArray("data");
                    for (int i = 0; i < jsonArray.size(); i++) {
                        jsonObject = jsonArray.getJSONObject(i);
                        Map<String, Object> map = new HashMap<>();
                        map.put("id", jsonObject.getString("id"));
                        map.put("uid", jsonObject.getString("uid"));
                        map.put("logo", jsonObject.getString("logo"));
                        map.put("name", jsonObject.getString("name"));
                        map.put("price", jsonObject.getString("price"));
                        map.put("cycle", jsonObject.getString("cycle"));
                        map.put("interest", jsonObject.getString("interest"));
                        map.put("type", jsonObject.getInteger("type"));
                        map.put("status", jsonObject.getInteger("status"));
                        listMap.add(map);
                    }
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            cLoading(swipeToLoadLayout);
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
    public void onClick(View view, int position, List<Map<String, Object>> listMap) {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.onBack:
                finish();
                break;
        }
    }
}
