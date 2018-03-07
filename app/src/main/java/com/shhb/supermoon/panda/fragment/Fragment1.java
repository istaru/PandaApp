package com.shhb.supermoon.panda.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.activity.CityActivity;
import com.shhb.supermoon.panda.activity.LoginActivity;
import com.shhb.supermoon.panda.activity.MainActivity;
import com.shhb.supermoon.panda.activity.MyQuotaActivity;
import com.shhb.supermoon.panda.activity.ShareActivity;
import com.shhb.supermoon.panda.adapter.BaseAdapter;
import com.shhb.supermoon.panda.adapter.Fragment1Adapter;
import com.shhb.supermoon.panda.model.BannerInfo;
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

import static android.app.Activity.RESULT_OK;

/**
 * Created by superMoon on 2017/7/31.
 */

public class Fragment1 extends BaseFragment implements OnRefreshListener,BaseAdapter.OnClickListener,View.OnClickListener {

    private static final int REQUEST_CODE_PICK_CITY = 233;
    private TextView position;
    private LinearLayout group1,group2,group3;
    private SwipeToLoadLayout swipeToLoadLayout;
    private RecyclerView recyclerView;
    private Fragment1Adapter mAdapter;
    /** 区分是否第二次进来*/
    private int lifeCycle = 1;

    public static Fragment1 newInstance() {
        Fragment1 fragment = new Fragment1();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new Fragment1Adapter();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment1_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        position = (TextView) view.findViewById(R.id.position);
        String location = PrefShared.getString(context,"position");
        if(null == location){
            location = "定位失败";
        }
        position.setText(location);
        position.setOnClickListener(this);
        group1 = (LinearLayout) view.findViewById(R.id.group1);
        group1.setOnClickListener(this);
        group2 = (LinearLayout) view.findViewById(R.id.group2);
        group2.setOnClickListener(this);
        group3 = (LinearLayout) view.findViewById(R.id.group3);
        group3.setOnClickListener(this);
        swipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setLoadMoreEnabled(false);
        swipeToLoadLayout.setOnRefreshListener(this);
        recyclerView = (RecyclerView) view.findViewById(R.id.swipe_target);
        GridLayoutManager layoutManager = new GridLayoutManager(context, 1, LinearLayoutManager.VERTICAL, false);
        recyclerView.addItemDecoration(
                new DividerItemDecoration(context, DividerItemDecoration.BOTH_SET,24, ContextCompat.getColor(context, R.color.webBg))
        );
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(mAdapter);
        mAdapter.setOnClickListener(this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(lifeCycle == 1){
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
     * 下拉更新广告列表
     */
    @Override
    public void onRefresh() {
        swipeToLoadLayout.setRefreshing(true);
        findByData();
    }

    /**
     * 查询banner信息
     */
    private void findByData(){
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msys","1");
        jsonObject.put("uid",PrefShared.getString(context,"userId"));
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        new OkHttpUtils(20).postEnqueue(Constants.FIND_BY_HOME, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cLoading(swipeToLoadLayout);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    cLoading(swipeToLoadLayout);
                    JSONObject jsonObject = BaseTools.jsonObject(context,response,"查询banner信息");
                    final List<Map<String, Object>> listMap = new ArrayList<>();
                    JSONObject dataObject = jsonObject.getJSONObject("data");
                    JSONArray jsonArray = dataObject.getJSONArray("banner");
                    final List<BannerInfo> bannerList = new ArrayList<>();
                    for(int i = 0;i < jsonArray.size();i++){
                        jsonObject = jsonArray.getJSONObject(i);
                        BannerInfo banner = new BannerInfo();
                        banner.setAvatar(jsonObject.getString("icon_url"));
                        banner.setName(jsonObject.getString("link"));
                        bannerList.add(banner);
                    }
                    Map<String,Object> mapHoms = new HashMap<>();
                    mapHoms.put("creditLine",dataObject.getString("creditLine"));
                    mapHoms.put("creditRating",dataObject.getString("creditRating"));
                    mapHoms.put("successRate",dataObject.getString("successRate"));
                    mapHoms.put("privilege",dataObject.getString("privilege"));

                    Map<String,Object> mapBanners = new HashMap<>();
                    mapBanners.put("banners",bannerList);
                    listMap.add(mapBanners);
                    listMap.add(mapHoms);
                    recyclerView.post(new Runnable() {
                        @Override
                        public void run() {
                            mAdapter.addHomeData(listMap);
                        }
                    });
                } catch (Exception e){
                    e.printStackTrace();
                }
            }
        }, parameter);
    }

    @Override
    public void onClick(View view, int position, List<Map<String, Object>> listMap) {
        switch (view.getId()){
            case R.id.frt_btn:
                Intent intent = new Intent(context,MyQuotaActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.position:
                intent = new Intent(context,CityActivity.class);
                startActivityForResult(intent,REQUEST_CODE_PICK_CITY);
                break;
            case R.id.group1:
                MainActivity.setCurrentPage(1);
                break;
            case R.id.group2:

                break;
            case R.id.group3:
                intent = new Intent(context,ShareActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_PICK_CITY && resultCode == RESULT_OK){
            if (data != null){
                String city = data.getStringExtra(CityActivity.KEY_PICKED_CITY);
                position.setText(city);
            }
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Fragment1"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Fragment1");
    }
}
