package com.shhb.supermoon.panda.model;

import android.content.Intent;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;

import com.alibaba.fastjson.JSONObject;
import com.shhb.supermoon.panda.activity.BaseActivity;
import com.shhb.supermoon.panda.activity.CreditActivity;
import com.shhb.supermoon.panda.activity.IdActivity;
import com.shhb.supermoon.panda.activity.LoginActivity;
import com.shhb.supermoon.panda.activity.OperateActivity;
import com.shhb.supermoon.panda.activity.WebActivity;
import com.shhb.supermoon.panda.tools.Constants;
import com.shhb.supermoon.panda.tools.PrefShared;

/**
 * Created by superMoon on 2017/8/10.
 */

public class JsObject {
    private BaseActivity context;
    private WebView webView;
    private JSONObject jsonObject;
    private Intent intent;

    public JsObject(WebView webView, BaseActivity context) {
        this.context = context;
        this.webView = webView;
    }

    /**
     * 把userId给网页
     * @param result
     */
    @JavascriptInterface
    public void js_getUserId(String result){
        try {
            jsonObject = JSONObject.parseObject(result);
            String userId = PrefShared.getString(context,"userId");
            if(null == userId){
                userId = "";
            }
            JSONObject resultJson = new JSONObject();
            resultJson.put("userId",userId);
            callBack(jsonObject.getString("name"),resultJson.toString());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 贷款详情
     * @param result
     */
    @JavascriptInterface
    public void js_getListInfo(String result){
        try {
            jsonObject = JSONObject.parseObject(result);
            callBack(jsonObject.getString("name"),context.getJson());
        } catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * 验证身份证
     * @param result
     */
    @JavascriptInterface
    public void js_authidcard(String result){
        intent = new Intent(context,IdActivity.class);
        context.startActivity(intent);
    }

    /**
     * 验证手机号
     * @param result
     */
    @JavascriptInterface
    public void js_authphone(String result){
        intent = new Intent(context,OperateActivity.class);
        context.startActivity(intent);
    }

    /**
     * 验证芝麻信用
     * @param result
     */
    @JavascriptInterface
    public void js_authsesame(String result){
        intent = new Intent(context,CreditActivity.class);
        context.startActivity(intent);
    }

    /**
     * 验证基本信息
     * @param result
     */
    @JavascriptInterface
    public void js_authbaseinfo(String result){
        intent = new Intent(context,WebActivity.class);
        intent.putExtra("url", Constants.NEWS_HTML);
        intent.putExtra("title","基本信息认证");
        context.startActivity(intent);
    }

    /**
     * 提现成功以后更新余额数据
     * @param result
     */
    @JavascriptInterface
    public void js_getcrashsuccess(String result){
        jsonObject = JSONObject.parseObject(result);
        PrefShared.saveString(context,"userPrice",jsonObject.getString("money"));
    }

    /**
     * 服务条款页面
     * @param result
     */
    @JavascriptInterface
    public void js_informedletter(String result){
        intent = new Intent(context,WebActivity.class);
        intent.putExtra("url", Constants.FORM_HTML);
        intent.putExtra("title","贷款知情书");
        context.startActivity(intent);
    }

    /**
     * 返回上一级页面
     * @param result
     */
    @JavascriptInterface
    public void js_goback(String result){
        context.finish();
    }

    /**
     * 登录
     * @param result
     */
    @JavascriptInterface
    public void js_login(String result){
        context.startActivity(new Intent(context,LoginActivity.class));
    }

    /**
     * 所有方法的回调
     * @param result
     */
    public void callBack(final String callBack,final String result){
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl("javascript:"+callBack+"(" + result + ");");
            }
        });
    }
}
