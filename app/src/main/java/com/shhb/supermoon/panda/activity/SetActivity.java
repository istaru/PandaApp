package com.shhb.supermoon.panda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.jaeger.library.StatusBarUtil;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.model.UMShare;
import com.shhb.supermoon.panda.tools.BaseTools;
import com.shhb.supermoon.panda.tools.Constants;
import com.shhb.supermoon.panda.tools.OkHttpUtils;
import com.shhb.supermoon.panda.tools.PrefShared;

import java.io.IOException;

import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by superMoon on 2017/8/9.
 */

public class SetActivity extends BaseActivity implements View.OnClickListener,CompoundButton.OnCheckedChangeListener {
    private RelativeLayout updateP,bindWx;
    private TextView setText1;
    private SwitchCompat push;
    private Button unLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.set_activity);
        initView();
    }

    private void initView() {
        mViewNeedOffset = findViewById(R.id.titleAll);
        onBack = (LinearLayout) findViewById(R.id.onBack);
        onBack.setOnClickListener(this);
        webView_title = (TextView) findViewById(R.id.webView_title);
        webView_title.setText("设置");
        bindWx = (RelativeLayout) findViewById(R.id.bind_wx);
        bindWx.setOnClickListener(this);
        setText1 = (TextView) findViewById(R.id.set_text1);
        push = (SwitchCompat) findViewById(R.id.push);
        String pushType = PrefShared.getString(context,"push");
        if(null != pushType){
            if(TextUtils.equals(pushType,"1")){
                push.setChecked(true);
            } else {
                push.setChecked(false);
            }
        }
        push.setOnCheckedChangeListener(this);
        String weChat = PrefShared.getString(context,"weChat");
        if(null != weChat && !TextUtils.equals(weChat,"")){
            bindWx.setEnabled(false);
            setText1.setVisibility(View.VISIBLE);
        } else {
            bindWx.setEnabled(true);
            setText1.setVisibility(View.GONE);
        }
        updateP = (RelativeLayout) findViewById(R.id.updateP);
        updateP.setOnClickListener(this);
        unLogin = (Button) findViewById(R.id.unLogin);
        unLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.bind_wx:
                new UMShare(this,"绑定微信").login();
                break;
            case R.id.updateP:
                intent = new Intent(context,RegisterActivity.class);
                intent.putExtra("type","2");
                startActivity(intent);
                break;
            case R.id.unLogin:
                unLogin();
                break;
            case R.id.onBack:
                finish();
                break;
        }
    }

    /**
     * 退出用户和淘宝登录
     */
    private void unLogin() {
        final MaterialDialog mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle("温馨提示");
        mMaterialDialog.setMessage("确认退出当前账号的所有信息？");
        mMaterialDialog.setPositiveButton("确认", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
                PrefShared.removeData(context,"phoneNum");
                PrefShared.removeData(context,"userId");
                PrefShared.removeData(context,"push");
                PrefShared.removeData(context,"weChat");
                PrefShared.removeData(context,"userImg");
                PrefShared.removeData(context,"userName");
                PrefShared.removeData(context,"userGrade");
                PrefShared.removeData(context,"userPrice");
//                PrefShared.removeData(context,"position");
                SetActivity.this.finish();
            }
        }).setNegativeButton("取消", new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.show();
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean checked) {
        showToast(0,context.getResources().getString(R.string.msg_jz));
        if(checked){
            setPush(1);
        } else {
            setPush(2);
        }
    }

    /**
     * 设置消息推送
     */
    private void setPush(final int type) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msys","1");
        jsonObject.put("uid", PrefShared.getString(context,"userId"));
        jsonObject.put("allow",type);
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        new OkHttpUtils(20).postEnqueue(Constants.SET_PUSH, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast(1,context.getResources().getString(R.string.msg_net));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = "";
                try {
                    JSONObject jsonObject = BaseTools.jsonObject(context,response,"设置消息推送");
                    int status = jsonObject.getInteger("status");
                    msg = jsonObject.getString("msg");
                    if(1 == status){
                        PrefShared.saveString(context,"push",type+"");
                    }
                } catch (Exception e){
                    msg = context.getResources().getString(R.string.msg_send);
                    e.printStackTrace();
                }
                showToast(1,msg);
            }
        }, parameter);
    }


}
