package com.shhb.supermoon.panda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jaeger.library.StatusBarUtil;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.model.PhoneInfo;
import com.shhb.supermoon.panda.model.UMShare;
import com.shhb.supermoon.panda.tools.BaseTools;
import com.shhb.supermoon.panda.tools.Constants;
import com.shhb.supermoon.panda.tools.OkHttpUtils;
import com.shhb.supermoon.panda.tools.PrefShared;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.UMShareAPI;


import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by superMoon on 2017/8/3.
 */

public class LoginActivity extends BaseActivity implements View.OnClickListener{
    private EditText phoneEdt,passWEdt;
    private TextView wLogin, forgetPassword,goRegister;
    private Button uLogin;
    private LinearLayout codeLogin;

    private String telphoneNum,telPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);
        initView();
    }

    private void initView() {
        mViewNeedOffset = findViewById(R.id.titleAll);
        webView_title = (TextView) findViewById(R.id.webView_title);
        webView_title.setText("登录");
        onBack = (LinearLayout) findViewById(R.id.onBack);
        onBack.setOnClickListener(this);
        codeLogin = (LinearLayout) findViewById(R.id.on_code_login);
        codeLogin.setVisibility(View.VISIBLE);
        codeLogin.setOnClickListener(this);
        phoneEdt = (EditText) findViewById(R.id.phone_number);
        passWEdt = (EditText) findViewById(R.id.password);
        uLogin = (Button) findViewById(R.id.uLogin);
        uLogin.setOnClickListener(this);
        wLogin = (TextView) findViewById(R.id.wLogin);
        wLogin.setOnClickListener(this);
        forgetPassword = (TextView) findViewById(R.id.forget_password);
        forgetPassword.setOnClickListener(this);
        goRegister = (TextView) findViewById(R.id.go_register);
        goRegister.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()){
            case R.id.uLogin:
                telphoneNum = phoneEdt.getText().toString();
                telPassword = passWEdt.getText().toString();
                if(TextUtils.equals(telphoneNum,"")){
                    showToast(1,context.getResources().getString(R.string.msg_input_phone));
                } else {
                    if(BaseTools.isNumeric(telphoneNum) && telphoneNum.length() == 11) {
                        if (TextUtils.equals(telPassword, "")) {
                            showToast(1, context.getResources().getString(R.string.msg_input_pw));
                        } else {
                            if(6 > telPassword.length()){
                                showToast(1,context.getResources().getString(R.string.msg_pw_error));
                            } else {
                                showToast(0,context.getResources().getString(R.string.msg_jz));
                                usLogin();
                            }
                        }
                    } else {
                        showToast(1,context.getResources().getString(R.string.msg_phone_error));
                    }
                }
                break;
            case R.id.wLogin:
                new UMShare(this,"微信登录").login();
                break;
            case R.id.forget_password:
                intent = new Intent(context,RegisterActivity.class);
                intent.putExtra("type","3");
                startActivity(intent);
                break;
            case R.id.go_register:
                intent = new Intent(context,RegisterActivity.class);
                intent.putExtra("type","1");
                startActivity(intent);
                break;
            case R.id.onBack:
                finish();
                break;
            case R.id.on_code_login:
                intent = new Intent(context,RegisterActivity.class);
                intent.putExtra("type","4");
                startActivity(intent);
                break;
        }
    }

    /**
     * 用户登录
     */
    private void usLogin() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone",telphoneNum);
        jsonObject.put("password",telPassword);
        jsonObject.put("device_token", PushAgent.getInstance(this).getRegistrationId());
        jsonObject.put("vcode","");
        jsonObject.put("uid_type",1);
        jsonObject.put("msys",1);
        jsonObject.put("address",PrefShared.getString(context,"position"));
        PhoneInfo phoneInfo = new PhoneInfo(context);
        Map<String,Object> map = phoneInfo.getPhoneMsg();
        map.put("type","1");
        for(Map.Entry<String, Object> m : map.entrySet()){
            jsonObject.put(m.getKey(),m.getValue());
        }
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        new OkHttpUtils(20).postEnqueue(Constants.USER_LOGIN, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast(1,context.getResources().getString(R.string.msg_net));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = "";
                try {
                    JSONObject jsonObject = BaseTools.jsonObject(context,response,"用户登录");
                    int status = jsonObject.getInteger("status");
                    msg = jsonObject.getString("msg");
                    if(1 == status){
                        jsonObject = jsonObject.getJSONObject("data");
                        MobclickAgent.onProfileSignIn(jsonObject.getString("uid"));//友盟统计（普通登录）
                        PrefShared.saveString(context,"phoneNum",telphoneNum);
                        PrefShared.saveString(context,"userId",jsonObject.getString("uid"));
                        PrefShared.saveString(context,"push",jsonObject.getString("allow_push"));
                        PrefShared.saveString(context,"weChat",jsonObject.getString("wx_openid"));
                        PrefShared.saveString(context,"userImg",jsonObject.getString("head_img"));
                        PrefShared.saveString(context,"userName",jsonObject.getString("nickname"));
                        PrefShared.saveString(context,"userGrade",jsonObject.getString("grade"));
                        PrefShared.saveString(context,"userPrice",jsonObject.getString("price"));
                        new CloseThread("LoginActivity").start();
                    }
                } catch (Exception e){
                    msg = context.getResources().getString(R.string.msg_send);
                    e.printStackTrace();
                }
                showToast(1,msg);
            }
        },parameter);
    }

    /**
     * 友盟分享的回调
     * @param requestCode
     * @param resultCode
     * @param data
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        UMShareAPI.get(this).onActivityResult(requestCode, resultCode, data);//友盟精简版的回调
    }
}
