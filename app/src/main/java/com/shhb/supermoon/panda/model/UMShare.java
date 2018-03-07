package com.shhb.supermoon.panda.model;

import android.support.v4.content.ContextCompat;
import android.text.TextUtils;

import com.alibaba.fastjson.JSONObject;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.activity.BaseActivity;
import com.shhb.supermoon.panda.application.MainApplication;
import com.shhb.supermoon.panda.tools.BaseTools;
import com.shhb.supermoon.panda.tools.Constants;
import com.shhb.supermoon.panda.tools.OkHttpUtils;
import com.shhb.supermoon.panda.tools.PrefShared;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.PushAgent;
import com.umeng.socialize.ShareAction;
import com.umeng.socialize.UMAuthListener;
import com.umeng.socialize.UMShareAPI;
import com.umeng.socialize.UMShareListener;
import com.umeng.socialize.bean.SHARE_MEDIA;
import com.umeng.socialize.media.UMImage;
import com.umeng.socialize.media.UMWeb;
import com.umeng.socialize.shareboard.ShareBoardConfig;

import java.io.IOException;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;


/**
 * Created by superMoon on 2017/3/15.
 */

public class UMShare implements UMShareListener, UMAuthListener {

    private BaseActivity context;
    private String type;

    private String weChat, userName, userImg;

    public UMShare(BaseActivity context, String type) {
        this.context = context;
        this.type = type;
    }

    /**
     * 微信分享
     *
     * @param title
     * @param content
     * @param imgUrl
     * @param shareUrl
     */
    public void share(String title, String content, String imgUrl, String shareUrl) {
        ShareAction shareAction = new ShareAction(context);
        UMWeb web = new UMWeb(shareUrl);//链接
        web.setTitle(title);//标题
        web.setThumb(new UMImage(context, imgUrl));//图片
        web.setDescription(content);//内容
        shareAction.withMedia(web);
        shareAction.setCallback(this);//添加回调
        if (TextUtils.equals("邀请好友", type)) {
            shareAction.setPlatform(SHARE_MEDIA.WEIXIN);
            shareAction.share();
        } else {
            shareAction.setDisplayList(SHARE_MEDIA.WEIXIN_CIRCLE, SHARE_MEDIA.QZONE, SHARE_MEDIA.WEIXIN, SHARE_MEDIA.QQ, SHARE_MEDIA.SINA);
            ;
            ShareBoardConfig config = new ShareBoardConfig();
            config.setShareboardPostion(ShareBoardConfig.SHAREBOARD_POSITION_BOTTOM);//分享的面板底部显示
            config.setMenuItemBackgroundShape(ShareBoardConfig.BG_SHAPE_CIRCULAR);//设置面板的透明度和圆角
            config.setTitleVisibility(false);//隐藏标题
            config.setTitleText(type);
            config.setCancelButtonText("关闭面板");
            config.setCancelButtonVisibility(true);//底部取消按钮
            config.setShareboardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            shareAction.open(config);
        }
    }

    /**
     * 微信登录
     */
    public void login() {
        UMShareAPI mShareAPI = UMShareAPI.get(context);
        mShareAPI.getPlatformInfo(context, SHARE_MEDIA.WEIXIN, this);
    }

    //分享的监听
    @Override
    public void onStart(SHARE_MEDIA share_media) {
        context.showToast(0, context.getResources().getString(R.string.msg_jz));
    }

    @Override
    public void onResult(SHARE_MEDIA share_media) {
        context.showToast(1, type + "成功");
    }

    @Override
    public void onError(SHARE_MEDIA share_media, Throwable throwable) {
        if (throwable.getMessage().contains("没有安装")) {//没有安装应用
            context.showToast(1, "请先安装应用！");
        } else {
            context.showToast(1, "分享" + type + "！");
        }
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media) {
        context.showToast(1, type + "取消");
    }

    //登录的监听
    @Override
    public void onComplete(SHARE_MEDIA share_media, int i, Map<String, String> map) {
        MobclickAgent.onProfileSignIn("weChat", map.get("unionid"));//友盟统计（微信登录）
        weChat = map.get("openid");
        userName = map.get("name");
        userImg = map.get("iconurl");
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("wx_uid", map.get("uid"));
        jsonObject.put("unionId", map.get("unionid"));
        jsonObject.put("accessToken", map.get("accessToken"));
        jsonObject.put("refreshToken", map.get("refreshToken"));
        jsonObject.put("openid", weChat);
        jsonObject.put("nickname", userName);
        jsonObject.put("headimgurl", userImg);
        jsonObject.put("gender", map.get("gender"));
        jsonObject.put("city", map.get("city"));
        jsonObject.put("country", map.get("country"));
        jsonObject.put("language", map.get("language"));
        jsonObject.put("province", map.get("province"));
        wxLogin(jsonObject);
    }

    @Override
    public void onError(SHARE_MEDIA share_media, int i, Throwable throwable) {
        if(throwable.toString().contains("没有安装")){
            context.showToast(1, "请先安装微信客户端");
        } else {
            context.showToast(1, type + "失败！");
        }
    }

    @Override
    public void onCancel(SHARE_MEDIA share_media, int i) {
        context.showToast(1, "取消" + type);
    }

    /**
     * 微信登录
     * @param jsonObject
     */
    private void wxLogin(JSONObject jsonObject) {
        String url = "";
        String activityName = "";
        jsonObject.put("device_token", PushAgent.getInstance(context).getRegistrationId());
        jsonObject.put("uid_type", 1);
        jsonObject.put("msys", 1);
        jsonObject.put("code", "");
        if (TextUtils.equals("微信登录", type)) {
            url = Constants.WX_LOGIN;
            activityName = "LoginActivity";
        } else {
            jsonObject.put("uid", PrefShared.getString(context, "userId"));
            url = Constants.BIND_WX;
            activityName = "SetActivity";
        }
        PhoneInfo phoneInfo = new PhoneInfo(context);
        Map<String, Object> map = phoneInfo.getPhoneMsg();
        map.put("type", "1");
        for (Map.Entry<String, Object> m : map.entrySet()) {
            jsonObject.put(m.getKey(), m.getValue());
        }
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        final String finalActivityName = activityName;
        new OkHttpUtils(20).postEnqueue(url, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                context.showToast(1, context.getResources().getString(R.string.msg_net));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = "";
                try {
                    JSONObject jsonObject = BaseTools.jsonObject(context, response, "微信登录");
                    int status = jsonObject.getInteger("status");
                    msg = jsonObject.getString("msg");
                    if (1 == status) {
                        if (TextUtils.equals("微信登录", type)) {
                            jsonObject = jsonObject.getJSONObject("data");
                            PrefShared.saveString(context, "phoneNum", "");
                            PrefShared.saveString(context, "userId", jsonObject.getString("uid"));
                            PrefShared.saveString(context, "push", jsonObject.getString("allow_push"));
                            PrefShared.saveString(context, "weChat", jsonObject.getString("wx_openid"));
                            PrefShared.saveString(context, "userImg", jsonObject.getString("head_img"));
                            PrefShared.saveString(context, "userName", jsonObject.getString("nickname"));
                            PrefShared.saveString(context, "userGrade", jsonObject.getString("grade"));
                            PrefShared.saveString(context, "userPrice", jsonObject.getString("price"));
                        } else {
                            PrefShared.saveString(context, "weChat", weChat);
                            PrefShared.saveString(context, "userImg", userImg);
                            PrefShared.saveString(context, "userName", userName);
                        }
                        new CloseThread(finalActivityName).start();
                    }
                } catch (Exception e) {
                    msg = context.getResources().getString(R.string.msg_send);
                    e.printStackTrace();
                }
                context.showToast(1, msg);
            }
        }, parameter);
    }

    /**
     * 1秒之后关掉提示并结束当前Activity
     */
    public class CloseThread extends Thread {
        private String activityName = "";

        public CloseThread(String activityName) {
            this.activityName = activityName;
        }

        public void run() {
            try {
                sleep(1000);
                if (context.showView.isShowing()) {
                    context.showView.dismiss();
                }
                if (activityName.indexOf(",") > 0) {
                    String names[] = activityName.split(",");
                    for (int i = 0; i < names.length; i++) {
                        MainApplication.finishActivity(names[i]);
                    }
                } else {
                    MainApplication.finishActivity(activityName);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

}
