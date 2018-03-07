package com.shhb.supermoon.panda.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jaeger.library.StatusBarUtil;
import com.shhb.supermoon.panda.R;
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

import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by superMoon on 2017/8/7.
 */

public class RegisterActivity extends BaseActivity implements View.OnClickListener {
    /**
     * 1注册，2、3修改密码，4验证码登录
     */
    private int type;
    private String msgText = "";
    private LinearLayout xyView;
    private CheckBox isTy;
    private EditText phoneEdt, codeEdt, passWEdt;
    private TextView getCode, goLogin, registeXy;
    private Button register;

    private String telphoneNum, telPassword, telCode;
    /**
     * 获取验证码的倒计时
     */
    private int duration;
    /**
     * 每隔1000 毫秒执行一次
     */
    private static final int delayTime = 1000;
    private GraCodeWindow codeWindow;
    /**
     * 显示验证码
     */
    private ImageView code_url;
    /**
     * 验证
     */
    private EditText code_text;
    /**
     * 验证的地址
     */
    private String codeUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = Integer.parseInt(getIntent().getStringExtra("type"));
        setContentView(R.layout.register_activity);
        initView();
    }

    private void initView() {
        mViewNeedOffset = findViewById(R.id.titleAll);
        webView_title = (TextView) findViewById(R.id.webView_title);
        xyView = (LinearLayout) findViewById(R.id.xy_view);
        register = (Button) findViewById(R.id.register);
        register.setOnClickListener(this);
        registeXy = (TextView) findViewById(R.id.register_xy);
        registeXy.setOnClickListener(this);
        goLogin = (TextView) findViewById(R.id.go_login);
        goLogin.setOnClickListener(this);
        phoneEdt = (EditText) findViewById(R.id.phone_number);
        if (1 == type) {
            msgText = "注册";
            webView_title.setText("注册");
            xyView.setVisibility(View.VISIBLE);
            register.setText("立即注册");
            goLogin.setVisibility(View.VISIBLE);
        } else if (2 == type || 3 == type) {
            msgText = "密码修改";
            webView_title.setText("修改密码");
            xyView.setVisibility(View.GONE);
            register.setText("确认");
            goLogin.setVisibility(View.GONE);
        } else {
            msgText = "验证码登录";
            webView_title.setText("验证码登录");
            xyView.setVisibility(View.GONE);
            register.setText("立即登录");
            goLogin.setVisibility(View.GONE);
        }
        String PrePhoneNum = PrefShared.getString(context, "phoneNum");
        if (null == PrePhoneNum || TextUtils.equals(PrePhoneNum, "")) {
            phoneEdt.setEnabled(true);
        } else {
            phoneEdt.setEnabled(false);
            phoneEdt.setText(PrePhoneNum);
        }
        onBack = (LinearLayout) findViewById(R.id.onBack);
        onBack.setOnClickListener(this);
        isTy = (CheckBox) findViewById(R.id.is_tm);
        phoneEdt = (EditText) findViewById(R.id.phone_number);
        getCode = (TextView) findViewById(R.id.get_code);
        getCode.setOnClickListener(this);
        codeEdt = (EditText) findViewById(R.id.code_edt);
        passWEdt = (EditText) findViewById(R.id.password);
    }

    public class GraCodeWindow extends PopupWindow implements View.OnClickListener {
        View viewmenu;
        TextView code_clear, code_sub;

        GraCodeWindow() {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            viewmenu = inflater.inflate(R.layout.code_window, null);
            this.setWidth(BaseTools.getWindowsWidth(context));//设置窗体的宽
            this.setHeight(LinearLayout.LayoutParams.WRAP_CONTENT);//设置窗体的高
            this.setBackgroundDrawable(new ColorDrawable());//去掉黑色边框
            this.setOutsideTouchable(true);// 点击外部可关闭窗口
            this.setFocusable(true);//设置窗体可点击
            this.setTouchable(true);
            this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE | WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);
            this.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);//不被输入法挡住
            this.setContentView(viewmenu);
            code_text = (EditText) viewmenu.findViewById(R.id.code_text);
            code_url = (ImageView) viewmenu.findViewById(R.id.code_url);
            code_url.setOnClickListener(this);
            code_clear = (TextView) viewmenu.findViewById(R.id.code_clear);
            code_clear.setOnClickListener(this);
            code_sub = (TextView) viewmenu.findViewById(R.id.code_sub);
            code_sub.setOnClickListener(this);
            this.setOnDismissListener(new OnDismissListener() {
                @Override
                public void onDismiss() {
                    BaseTools.setBackgroundAlpha(context, 1f);
                }
            });
        }

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.code_url:
                    getGraCode();
                    break;
                case R.id.code_clear:
                    codeWindow.dismiss();
                    code_text.setText("");
                    break;
                case R.id.code_sub:
                    if (code_text.getText().toString().trim().length() == 4) {
                        showToast(0, context.getResources().getString(R.string.msg_jz));
                        if (getCode.isEnabled()) {
                            codeWindow.dismiss();
                            getVCode();
                        }
                    } else {
                        showToast(1, context.getResources().getString(R.string.msg_imgcode_error));
                    }
                    break;
            }
        }
    }

    /**
     * 获取图形验证码
     */
    private void getGraCode() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone",phoneEdt.getText().toString());
        String parameter = jsonObject.toString();
        new OkHttpUtils(20).postEnqueue(Constants.GET_GRA_CODE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast(1, context.getResources().getString(R.string.msg_net));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = "";
                try {
                    showToast(1, "");
                    JSONObject jsonObject = BaseTools.jsonObject(context, response, "获取图形验证码");
                    int status = jsonObject.getInteger("status");
                    if (1 == status) {
                        jsonObject = jsonObject.getJSONObject("data");
                        codeUrl = jsonObject.getString("flag");
                        if (!TextUtils.equals(codeUrl, "")) {
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    if(null == codeWindow){
                                        codeWindow = new GraCodeWindow();
                                    }
                                    BaseTools.setBackgroundAlpha(context, 0.5f);
                                    codeWindow.showAtLocation(getCode, Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
                                    code_url.setImageBitmap(BaseTools.base64ToBitmap(codeUrl));
                                }
                            });
                        } else {
                            if (getCode.isEnabled()) {
                                getVCode();
                            }
                        }
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            showDialog();
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void showDialog() {
        if (0 != duration) {
            final MaterialDialog mMaterialDialog = new MaterialDialog(this);
            mMaterialDialog.setTitle("温馨提示");
            mMaterialDialog.setMessage("获取的验证码会根据网络状况有所延迟，要不再等等？");
            mMaterialDialog.setPositiveButton("再等等", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaterialDialog.dismiss();
                }
            }).setNegativeButton("算了", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMaterialDialog.dismiss();
                    handler.removeCallbacks(timerRunnable);
                    RegisterActivity.this.finish();
                }
            });
            mMaterialDialog.show();
        } else {
            RegisterActivity.this.finish();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.onBack:
                showDialog();
                break;
            case R.id.get_code:
                telphoneNum = phoneEdt.getText().toString();
                if (TextUtils.equals(telphoneNum, "")) {
                    showToast(1, context.getResources().getString(R.string.msg_input_phone));
                } else {
                    if (BaseTools.isNumeric(telphoneNum) && telphoneNum.length() == 11) {
                        showToast(0, context.getResources().getString(R.string.msg_jz));
                        getGraCode();
                    } else {
                        showToast(1, context.getResources().getString(R.string.msg_phone_error));
                    }
                }
                break;
            case R.id.register:
                telphoneNum = phoneEdt.getText().toString();
                telPassword = passWEdt.getText().toString();
                telCode = codeEdt.getText().toString();
                if (TextUtils.equals(telphoneNum, "")) {
                    showToast(1, context.getResources().getString(R.string.msg_input_phone));
                } else {
                    if (BaseTools.isNumeric(telphoneNum) && telphoneNum.length() == 11) {
                        if (TextUtils.equals("", telCode)) {
                            showToast(1, context.getResources().getString(R.string.msg_input_code));
                        } else {
                            if (TextUtils.equals(telPassword, "")) {
                                showToast(1, context.getResources().getString(R.string.msg_input_pw));
                            } else {
                                if (6 > telPassword.length()) {
                                    showToast(1, context.getResources().getString(R.string.msg_pw_error));
                                } else {
                                    if (isTy.isChecked()) {
                                        showToast(0, context.getResources().getString(R.string.msg_jz));
                                        new Thread() {
                                            @Override
                                            public void run() {
                                                super.run();
                                                userRegister();
                                            }
                                        }.start();
                                    } else {
                                        showToast(1, context.getResources().getString(R.string.msg_please_register));
                                    }
                                }
                            }
                        }
                    } else {
                        showToast(1, context.getResources().getString(R.string.msg_phone_error));
                    }
                }
                break;
            case R.id.register_xy:
                Intent intent = new Intent(context, WebActivity.class);
                intent.putExtra("url", Constants.REGISTER_HTML);
                intent.putExtra("title", "熊猫贷注册协议");
                startActivity(intent);
                break;
            case R.id.go_login:
                this.finish();
                break;
        }
    }

    /**
     * 获取验证码
     */
    private void getVCode() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msys", "1");
        jsonObject.put("phone", telphoneNum);
        if(null != code_text){
            jsonObject.put("graphcode", code_text.getText().toString().trim());
        }
        int codeType = 1;
        if (2 == type || 3 == type || 4 == type) {
            codeType = 2;
        }
        jsonObject.put("type", codeType);
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        new OkHttpUtils(20).postEnqueue(Constants.GET_VCODE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast(1, context.getResources().getString(R.string.msg_net));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = "";
                try {
                    JSONObject jsonObject = BaseTools.jsonObject(context, response, "获取验证码信息");
                    int status = jsonObject.getInteger("status");
                    if (1 == status) {
                        duration = 300;
                        handler.postDelayed(timerRunnable, delayTime);
                    }
                    msg = jsonObject.getString("msg");
                } catch (Exception e) {
                    msg = context.getResources().getString(R.string.msg_send);
                    e.printStackTrace();
                }
                showToast(1, msg);
            }
        }, parameter);
    }

    /**
     * 倒计时的计时器
     */
    private Handler handler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (0 == duration) {
                getCode.setText("获取验证码");
                getCode.setEnabled(true);
                handler.removeCallbacks(timerRunnable);
                duration = 300;
                return;
            } else {
                getCode.setEnabled(false);
                setDuration(--duration);
            }
            handler.postDelayed(timerRunnable, delayTime);
        }
    };

    /**
     * 显示倒计时
     *
     * @param duration
     */
    private void setDuration(Integer duration) {
        getCode.setText(duration + "秒后重新获取");
    }

    /**
     * 用户验证码登录、修改密码、注册
     */
    private void userRegister() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("phone", telphoneNum);
        jsonObject.put("password", telPassword);
        jsonObject.put("device_token", PushAgent.getInstance(this).getRegistrationId());
        jsonObject.put("vcode", telCode);
        jsonObject.put("uid_type", 1);
        jsonObject.put("msys", 1);
        jsonObject.put("address", PrefShared.getString(context, "position"));
        PhoneInfo phoneInfo = new PhoneInfo(context);
        Map<String, Object> map = phoneInfo.getPhoneMsg();
        map.put("type", "1");
        for (Map.Entry<String, Object> m : map.entrySet()) {
            jsonObject.put(m.getKey(), m.getValue());
        }
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        String url = "";
        if (1 == type) {
            url = Constants.USER_REGISTER;
        } else {
            url = Constants.USER_LOGIN;
        }
        new OkHttpUtils(20).postEnqueue(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast(1, context.getResources().getString(R.string.msg_net));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = "";
                try {
                    JSONObject jsonObject = BaseTools.jsonObject(context, response, msgText);
                    int status = jsonObject.getInteger("status");
                    msg = jsonObject.getString("msg");
                    if (1 == status) {
                        jsonObject = jsonObject.getJSONObject("data");
                        MobclickAgent.onProfileSignIn(jsonObject.getString("uid"));//友盟统计（普通登录）
                        PrefShared.saveString(context, "phoneNum", telphoneNum);
                        PrefShared.saveString(context, "userId", jsonObject.getString("uid"));
                        PrefShared.saveString(context, "push", jsonObject.getString("allow_push"));
                        PrefShared.saveString(context, "weChat", jsonObject.getString("wx_openid"));
                        PrefShared.saveString(context, "userImg", jsonObject.getString("head_img"));
                        PrefShared.saveString(context, "userName", jsonObject.getString("nickname"));
                        PrefShared.saveString(context, "userGrade", jsonObject.getString("grade"));
                        PrefShared.saveString(context, "userPrice", jsonObject.getString("price"));
                        handler.removeCallbacks(timerRunnable);
                        if (1 == type || 3 == type || 4 == type) {
                            new CloseThread("RegisterActivity,LoginActivity").start();
                        } else {
                            new CloseThread("RegisterActivity,SetActivity").start();
                        }
                    }
                } catch (Exception e) {
                    msg = context.getResources().getString(R.string.msg_send);
                    e.printStackTrace();
                }
                showToast(1, msg);
            }
        }, parameter);
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTranslucentForImageView(this, 0, mViewNeedOffset);
    }
}
