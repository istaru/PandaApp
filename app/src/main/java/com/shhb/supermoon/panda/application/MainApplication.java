package com.shhb.supermoon.panda.application;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.shhb.supermoon.panda.tools.Constants;
import com.shhb.supermoon.panda.tools.PrefShared;
import com.umeng.analytics.MobclickAgent;
import com.umeng.message.IUmengRegisterCallback;
import com.umeng.message.PushAgent;
import com.umeng.socialize.Config;
import com.umeng.socialize.PlatformConfig;
import com.umeng.socialize.UMShareAPI;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by superMoon on 2017/7/31.
 */

public class MainApplication extends Application {
    private static Context context;

    private static MainApplication instance;

    private static List<Activity> activityList = new ArrayList<>();

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        initUM();
        getStatusBarHeight();
    }

    /**
     * 初始化友盟
     */
    private void initUM() {
        MobclickAgent.openActivityDurationTrack(false);
        initUMPush();
        initUMShare();
    }

    /**
     * 初始化友盟推送
     */
    private void initUMPush() {
        try {
            //注册推送服务，每次调用register方法都会回调该接口
            PushAgent mPushAgent = PushAgent.getInstance(this);//初始化消息推送对象
            mPushAgent.register(new IUmengRegisterCallback() {
                @Override
                public void onSuccess(String deviceToken) {
                    Log.e("UmPush","success|||"+deviceToken);
                }

                @Override
                public void onFailure(String s, String s1) {
                    Log.e("UmPush","failure|||"+s+s1);
                }
            });
        } catch (Exception e) {
            Log.e("UmPush","error");
            e.printStackTrace();
        }
    }

    /**
     * 友盟分享
     */
    private void initUMShare() {
        Config.DEBUG = false;//关闭友盟debug模式
        PlatformConfig.setWeixin(Constants.WX_APPID, Constants.WX_SECRET);
        PlatformConfig.setQQZone(Constants.QQ_APPID, Constants.QQ_SECRET);
        PlatformConfig.setSinaWeibo(Constants.WB_APPID, Constants.WB_SECRET, "http://sns.whalecloud.com");
        UMShareAPI.get(this);
    }

    /**
     * 获取状态栏高度
     */
    private void getStatusBarHeight() {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        PrefShared.saveInt(context, "statusBarHeight", statusBarHeight);
    }

    /**
     * 单例模式中获取唯一的MyApplication实例
     * @return
     */
    public static MainApplication getInstance() {
        if (null == instance) {
            instance = new MainApplication();
        }
        return instance;
    }

    /**
     * 添加Activity到容其中
     * @param activity
     */
    public static void addActivity(Activity activity) {
        activityList.add(activity);
    }

    /**
     * 销毁指定Activity
     */
    public static void finishActivity(String activityName){
        for(int i = 0;i < activityList.size();i++){
            Activity activity = activityList.get(i);
            String name = activity.toString();
            name = name.substring(name.lastIndexOf(".") + 1, name.indexOf("@"));
            if(TextUtils.equals(activityName,name)){
                activity.finish();
            }
        }
    }

    /**
     * 销毁所有Activity
     */
    public static void exit() {
        for (Activity activity : activityList) {
            activity.finish();
        }
        System.exit(0);
    }
}
