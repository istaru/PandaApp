package com.shhb.supermoon.panda.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.jaeger.library.StatusBarUtil;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.tools.BaseTools;
import com.shhb.supermoon.panda.tools.Constants;
import com.shhb.supermoon.panda.tools.OkHttpUtils;
import com.shhb.supermoon.panda.tools.PrefShared;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by superMoon on 2017/8/7.
 */

public class MyQuotaActivity extends BaseActivity implements View.OnClickListener, OnRefreshListener {
    private RelativeLayout quota1,quota2,quota3,quota4;
    TextView myQuota,rzText1,rzText2,rzText3,rzText4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myquota_activity);
        initView();
    }

    @Override
    protected void onStart() {
        super.onStart();
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

    private void initView() {
        swipeToLoadLayout = (SwipeToLoadLayout)findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setLoadMoreEnabled(false);
        swipeToLoadLayout.setOnRefreshListener(this);

        mViewNeedOffset = findViewById(R.id.titleAll);
        webView_title = (TextView) findViewById(R.id.webView_title);
        webView_title.setText("预估我的额度");
        onBack = (LinearLayout) findViewById(R.id.onBack);
        onBack.setOnClickListener(this);

        myQuota = (TextView) findViewById(R.id.my_quota);
        rzText1 = (TextView) findViewById(R.id.rz_text1);
        rzText2 = (TextView) findViewById(R.id.rz_text2);
        rzText3 = (TextView) findViewById(R.id.rz_text3);
        rzText4 = (TextView) findViewById(R.id.rz_text4);

        quota1 = (RelativeLayout) findViewById(R.id.quota1);
        quota1.setOnClickListener(this);
        quota2 = (RelativeLayout) findViewById(R.id.quota2);
        quota2.setOnClickListener(this);
        quota3 = (RelativeLayout) findViewById(R.id.quota3);
        quota3.setOnClickListener(this);
        quota4 = (RelativeLayout) findViewById(R.id.quota4);
        quota4.setOnClickListener(this);
    }

    @Override
    public void onRefresh() {
        swipeToLoadLayout.setRefreshing(true);
        findByYZXX();
    }

    /**
     * 查询用户认证了哪些东西
     */
    private void findByYZXX() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msys", "1");
        jsonObject.put("uid", PrefShared.getString(context, "userId"));
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        new OkHttpUtils(20).postEnqueue(Constants.FIND_USER_RZ, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cLoading(swipeToLoadLayout);
                showToast(1,context.getResources().getString(R.string.msg_net));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = "";
                try {
                    cLoading(swipeToLoadLayout);
                    JSONObject jsonObject = BaseTools.jsonObject(context, response, "查询用户认证了哪些东西");
                    int status = jsonObject.getInteger("status");
                    if(1 == status){
                        jsonObject = jsonObject.getJSONObject("data");
                        final String creditPrice = jsonObject.getString("credit_price");
                        final JSONObject finalJsonObject = jsonObject;
                        myQuota.post(new Runnable() {
                            @Override
                            public void run() {
                                myQuota.setText(creditPrice);
                                if(1 == finalJsonObject.getInteger("basicInformation")){
                                    setBackGround(rzText1);
                                }
                                if(1 == finalJsonObject.getInteger("idCard")){
                                    setBackGround(rzText2);
                                }
                                if(1 == finalJsonObject.getInteger("operators")){
                                    setBackGround(rzText3);
                                }
                                if(1 == finalJsonObject.getInteger("sesameCredit")){
                                    setBackGround(rzText4);
                                }
                            }
                        });

                    } else {
                        msg = jsonObject.getString("msg");
                    }
                } catch (Exception e) {
                    msg = context.getResources().getString(R.string.msg_send);
                    e.printStackTrace();
                }
                showToast(1, msg);
            }
        }, parameter);
    }

    private void setBackGround(final TextView view){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                view.setText("已认证");
                view.setTextColor(ContextCompat.getColor(context, R.color.app_color));
                Drawable drawable= context.getResources().getDrawable(R.mipmap.zc1);
                drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight());
                view.setCompoundDrawables(drawable,null,null,null);
                view.setBackgroundResource(R.drawable.btn_app_bg);
            }
        });
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.quota1:
                intent = new Intent(context,WebActivity.class);
                intent.putExtra("url",Constants.NEWS_HTML);
                intent.putExtra("title","基本信息认证");
                startActivity(intent);
                break;
            case R.id.quota2:
                intent = new Intent(context,IdActivity.class);
                startActivity(intent);
                break;
            case R.id.quota3:
                intent = new Intent(context,OperateActivity.class);
                startActivity(intent);
                break;
            case R.id.quota4:
                intent = new Intent(context,CreditActivity.class);
                startActivity(intent);
                break;
            case R.id.onBack:
                finish();
                break;
        }
    }
}
