package com.shhb.supermoon.panda.fragment;

import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.tools.BaseTools;
import com.shhb.supermoon.panda.tools.Constants;
import com.shhb.supermoon.panda.tools.OkHttpUtils;
import com.shhb.supermoon.panda.tools.PrefShared;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by superMoon on 2017/7/31.
 */

public class Fragment3 extends BaseFragment implements View.OnClickListener, OnRefreshListener {
    private TextView title;
    private EditText money, time;
    private Button fr3Btn;

    public static Fragment3 newInstance() {
        Fragment3 fragment = new Fragment3();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment3_view, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        title = (TextView) view.findViewById(R.id.webView_title);
        title.setText("意向贷");
        view.findViewById(R.id.onBack).setVisibility(View.GONE);

        swipeToLoadLayout = (SwipeToLoadLayout) view.findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setLoadMoreEnabled(false);
        swipeToLoadLayout.setOnRefreshListener(this);

        money = (EditText) view.findViewById(R.id.money);
        time = (EditText) view.findViewById(R.id.time);
        fr3Btn = (Button) view.findViewById(R.id.fr3_btn);
        fr3Btn.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
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

    @Override
    public void onRefresh() {
        swipeToLoadLayout.setRefreshing(true);
        findByData();
    }

    /**
     * 查找是否提交过贷款信息
     */
    private void findByData() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msys", "1");
        jsonObject.put("uid", PrefShared.getString(context, "userId"));
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        new OkHttpUtils(20).postEnqueue(Constants.FIND_BY_INTENTION, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cLoading(swipeToLoadLayout);
                showToast(1, context.getResources().getString(R.string.msg_net));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = "";
                try {
                    cLoading(swipeToLoadLayout);
                    JSONObject jsonObject = BaseTools.jsonObject(context, response, "查找是否提交过贷款信息");
                    int status = jsonObject.getInteger("status");
                    if (1 == status) {
                        final JSONObject dataObject = jsonObject.getJSONObject("data");
                        final int type = dataObject.getInteger("type");
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (1 != type) {
                                    money.setEnabled(true);
                                    money.setTextColor(ContextCompat.getColor(context, R.color.black));
                                    time.setEnabled(true);
                                    time.setTextColor(ContextCompat.getColor(context, R.color.black));
                                    fr3Btn.setEnabled(true);
                                    fr3Btn.setText("立即申请");
                                    fr3Btn.setTextColor(ContextCompat.getColor(context, R.color.white));
                                    fr3Btn.setBackgroundResource(R.drawable.btn_blue_bg);
                                } else {
                                    money.setEnabled(false);
                                    money.setTextColor(ContextCompat.getColor(context, R.color.text_color));
                                    money.setText(dataObject.getString("money"));
                                    time.setEnabled(false);
                                    time.setTextColor(ContextCompat.getColor(context, R.color.text_color));
                                    time.setText(dataObject.getString("cycle"));
                                    fr3Btn.setEnabled(false);
                                    fr3Btn.setTextColor(ContextCompat.getColor(context, R.color.text_color));
                                    fr3Btn.setBackgroundResource(R.drawable.btn_web_bg);
                                    fr3Btn.setText("审核中");
                                }
                            }
                        });
                    } else {
                        context.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                money.setEnabled(true);
                                money.setText("");
                                money.setHint(context.getResources().getString(R.string.msg_input_money));
                                time.setEnabled(true);
                                time.setText("");
                                time.setHint(context.getResources().getString(R.string.msg_input_time));
                                fr3Btn.setEnabled(true);
                                fr3Btn.setTextColor(ContextCompat.getColor(context, R.color.white));
                                fr3Btn.setBackgroundResource(R.drawable.btn_blue_bg);
                                fr3Btn.setText("立即申请");
                            }
                        });
                        msg = jsonObject.getString("msg");
                    }
                } catch (Exception e) {
                    msg = context.getResources().getString(R.string.msg_send);
                    e.printStackTrace();
                }
//                showToast(1,msg);
            }
        }, parameter);
    }


    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.fr3_btn:
                String moneyC = money.getText().toString();
                String timeC = time.getText().toString();
                if(TextUtils.equals(moneyC,"")){
                    showToast(1,context.getResources().getString(R.string.msg_input_money));
                } else {
                    if(TextUtils.equals(timeC,"")){
                        showToast(1,context.getResources().getString(R.string.msg_input_time));
                    } else {
                        showToast(0,context.getResources().getString(R.string.msg_jz));
                        applyLoan();
                    }
                }
                break;
        }
    }

    /**
     * 申请意向贷
     */
    private void applyLoan() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msys", "1");
        jsonObject.put("uid", PrefShared.getString(context, "userId"));
        jsonObject.put("yx_money", money.getText().toString());
        jsonObject.put("yx_cycle", time.getText().toString());
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        new OkHttpUtils(20).postEnqueue(Constants.APPLY_LOAN, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast(1, context.getResources().getString(R.string.msg_net));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = "";
                try {
                    JSONObject jsonObject = BaseTools.jsonObject(context, response, "申请意向贷");
                    final int status = jsonObject.getInteger("status");
                    msg = jsonObject.getString("msg");
                    context.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (1 == status) {
                                money.setEnabled(false);
                                money.setTextColor(ContextCompat.getColor(context, R.color.text_color));
                                money.setText(money.getText().toString());
                                time.setEnabled(false);
                                time.setTextColor(ContextCompat.getColor(context, R.color.text_color));
                                time.setText(time.getText().toString());
                                fr3Btn.setEnabled(false);
                                fr3Btn.setTextColor(ContextCompat.getColor(context, R.color.text_color));
                                fr3Btn.setBackgroundResource(R.drawable.btn_web_bg);
                                fr3Btn.setText("审核中");
                            }
                        }
                    });
                } catch (Exception e) {
                    msg = context.getResources().getString(R.string.msg_send);
                    e.printStackTrace();
                }
                showToast(1,msg);
            }
        }, parameter);
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart("Fragment3"); //统计页面，"MainScreen"为页面名称，可自定义
    }
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd("Fragment3");
    }
}
