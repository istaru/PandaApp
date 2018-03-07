package com.shhb.supermoon.panda.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.activity.CashActivity;
import com.shhb.supermoon.panda.activity.LoanOrderActivity;
import com.shhb.supermoon.panda.activity.LoginActivity;
import com.shhb.supermoon.panda.activity.MainActivity;
import com.shhb.supermoon.panda.activity.MessageActivity;
import com.shhb.supermoon.panda.activity.MyIncomeActivity;
import com.shhb.supermoon.panda.activity.SetActivity;
import com.shhb.supermoon.panda.activity.ShareActivity;
import com.shhb.supermoon.panda.activity.WebActivity;
import com.shhb.supermoon.panda.model.PhoneInfo;
import com.shhb.supermoon.panda.tools.BaseTools;
import com.shhb.supermoon.panda.tools.Constants;
import com.shhb.supermoon.panda.tools.OkHttpUtils;
import com.shhb.supermoon.panda.tools.PrefShared;
import com.shhb.supermoon.panda.view.GlideCircleTransform;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by superMoon on 2017/7/31.
 */

public class Fragment4 extends BaseFragment implements View.OnClickListener {
    private ImageView msg, usericon;
    private TextView set, userL, user_name, user_grade, user_price;
    private Button postal;
    private RelativeLayout userMsg, shareBtn;
    private LinearLayout f1View1, f1View2, f1View3, f2View1, f2View2, f2View3, f3View1, f3View2, f3View3;

    public static Fragment4 newInstance() {
        Fragment4 fragment = new Fragment4();
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("3、onCreateView", "执行");
        return inflater.inflate(R.layout.fragment4_view, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        set = (TextView) view.findViewById(R.id.set);
        set.setOnClickListener(this);
        msg = (ImageView) view.findViewById(R.id.msg_view);
        msg.setOnClickListener(this);
        usericon = (ImageView) view.findViewById(R.id.usericon);
        userL = (TextView) view.findViewById(R.id.userL);
        userL.setOnClickListener(this);
        userMsg = (RelativeLayout) view.findViewById(R.id.userMsg);
        user_name = (TextView) view.findViewById(R.id.user_name);
        user_grade = (TextView) view.findViewById(R.id.user_grade);
        user_price = (TextView) view.findViewById(R.id.user_price);
        postal = (Button) view.findViewById(R.id.postal);
        postal.setOnClickListener(this);

        f1View1 = (LinearLayout) view.findViewById(R.id.f1_view1);
        f1View1.setOnClickListener(this);
        f1View2 = (LinearLayout) view.findViewById(R.id.f1_view2);
        f1View2.setOnClickListener(this);
        f1View3 = (LinearLayout) view.findViewById(R.id.f1_view3);
        f1View3.setOnClickListener(this);

        f2View1 = (LinearLayout) view.findViewById(R.id.f2_view1);
        f2View1.setOnClickListener(this);
        f2View2 = (LinearLayout) view.findViewById(R.id.f2_view2);
        f2View2.setOnClickListener(this);
        f2View3 = (LinearLayout) view.findViewById(R.id.f2_view3);
        f2View3.setOnClickListener(this);

        f3View1 = (LinearLayout) view.findViewById(R.id.f3_view1);
        f3View1.setOnClickListener(this);
        f3View2 = (LinearLayout) view.findViewById(R.id.f3_view2);
        f3View2.setOnClickListener(this);
        f3View3 = (LinearLayout) view.findViewById(R.id.f3_view3);
        f3View3.setOnClickListener(this);

        shareBtn = (RelativeLayout) view.findViewById(R.id.share_btn);
        shareBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.set:
                intent = new Intent(context, SetActivity.class);
                startActivity(intent);
                break;
            case R.id.msg_view:
                intent = new Intent(context, MessageActivity.class);
                startActivity(intent);
                msg.setImageResource(R.mipmap.my1);
                break;
            case R.id.userL:
                intent = new Intent(context, LoginActivity.class);
                startActivity(intent);
                break;
            case R.id.postal:
                intent = new Intent(context, WebActivity.class);
                intent.putExtra("url", Constants.POSTAL_HTML);
                intent.putExtra("title", "申请提现");
                startActivity(intent);
                break;
            case R.id.f1_view1:
                break;
            case R.id.f1_view2:
                break;
            case R.id.f1_view3:
                break;
            case R.id.f2_view1:
                intent = new Intent(context, MyIncomeActivity.class);
                intent.putExtra("type", "我的收入");
                startActivity(intent);
                break;
            case R.id.f2_view2:
                intent = new Intent(context, MyIncomeActivity.class);
                intent.putExtra("type", "邀请记录");
                startActivity(intent);
                break;
            case R.id.f2_view3:
                MainActivity.setCurrentPage(2);
                break;
            case R.id.f3_view1:
                intent = new Intent(context, LoanOrderActivity.class);
                startActivity(intent);
                break;
            case R.id.f3_view2:
                intent = new Intent(context, CashActivity.class);
                startActivity(intent);
                break;
            case R.id.f3_view3:
                try {
                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(Constants.OPEN_QQ)));
                } catch (Exception e) {
                    e.printStackTrace();
                    if (e.toString().contains("ActivityNotFoundException")) {
                        showToast(1, "请先安装QQ客户端");
                    } else {
                        showToast(1, "无法打开QQ客户端");
                    }
                }
                break;
            case R.id.share_btn:
                intent = new Intent(context, ShareActivity.class);
                startActivity(intent);
                break;
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        Log.e("1、onAttach", "执行");
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.e("2、onCreate", "执行");
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.e("4、onCreateView", "执行");
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.e("5、onStart", "执行");
        findByMsg();
        String userId = PrefShared.getString(context, "userId");
        String userImg = PrefShared.getString(context, "userImg");
        String userName = PrefShared.getString(context, "userName");
        String userGrade = PrefShared.getString(context, "userGrade");
        String userPrice = PrefShared.getString(context, "userPrice");
        if (null != userId) {
            userL.setVisibility(View.GONE);
            userMsg.setVisibility(View.VISIBLE);
            user_name.setText(userName);
            user_grade.setText("信用等级:" + userGrade);
            user_price.setText(userPrice);
        } else {
            userMsg.setVisibility(View.GONE);
            userL.setVisibility(View.VISIBLE);
        }
        Glide.with(context)
                .load(userImg)
                .placeholder(R.mipmap.error_z)
                .error(R.mipmap.error_z)//加载出错的图片
                .priority(Priority.HIGH)//优先加载
                .transform(new GlideCircleTransform(context))
                .diskCacheStrategy(DiskCacheStrategy.NONE)//设置缓存策略
                .into(usericon);
    }

    /**
     * 查询是否有消息图片
     */
    private void findByMsg() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("device_token", PushAgent.getInstance(context).getRegistrationId());
        jsonObject.put("msys", 1);
        jsonObject.put("uid", PrefShared.getString(context, "userId"));
        jsonObject.put("gpsAddress", PrefShared.getString(context, "position"));
        PhoneInfo phoneInfo = new PhoneInfo(context);
        Map<String, Object> map = phoneInfo.getPhoneMsg();
        map.put("type", "1");
        for (Map.Entry<String, Object> m : map.entrySet()) {
            jsonObject.put(m.getKey(), m.getValue());
        }
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        new OkHttpUtils(20).postEnqueue(Constants.FIND_MSG_ICON, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = BaseTools.jsonObject(context, response, "查询是否有消息图片");
                    int status = jsonObject.getInteger("status");
                    if (1 == status) {
                        jsonObject = jsonObject.getJSONObject("data");
                        final int haveMsg = jsonObject.getInteger("have_msg");
                        msg.post(new Runnable() {
                            @Override
                            public void run() {
                                if (1 == haveMsg) {
                                    msg.setImageResource(R.mipmap.my1_on);
                                } else {
                                    msg.setImageResource(R.mipmap.my1);
                                }
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, parameter);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.e("6、onResume", "执行");
        MobclickAgent.onPageStart("Fragment4"); //统计页面，"MainScreen"为页面名称，可自定义
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.e("7、onPause", "执行");
        MobclickAgent.onPageEnd("Fragment4");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.e("8、onStop", "执行");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.e("9、onDestroyView", "执行");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.e("10、onDestroy", "执行");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.e("11、onDetach", "执行");
    }
}
