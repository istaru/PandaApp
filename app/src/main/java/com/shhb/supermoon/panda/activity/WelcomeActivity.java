package com.shhb.supermoon.panda.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.Priority;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.load.resource.drawable.GlideDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.animation.GlideAnimation;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.bumptech.glide.request.target.Target;
import com.jaeger.library.StatusBarUtil;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.adapter.PageAdapter;
import com.shhb.supermoon.panda.tools.BaseTools;
import com.shhb.supermoon.panda.tools.Constants;
import com.shhb.supermoon.panda.tools.OkHttpUtils;
import com.shhb.supermoon.panda.tools.PrefShared;
import com.shhb.supermoon.panda.view.CustomViewPager;
import com.umeng.analytics.MobclickAgent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import me.drakeet.materialdialog.MaterialDialog;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by superMoon on 2017/8/1.
 */

public class WelcomeActivity extends BaseActivity {
//    /** 声明AMapLocationClient类对象*/
//    private AMapLocationClient mLocationClient = null;
//    /** 声明AMapLocationClientOption对象*/
//    private AMapLocationClientOption mLocationOption = null;
    /**
     * 手机位置信息
     */
    private String addres = "";
    private RelativeLayout mainView;
    /**
     * 启动页的ViewPager
     */
    private CustomViewPager pageView;
    /**
     * 本地保存的次数
     */
    private int getFrequency;
    /**
     * 本地保存的版本
     */
    private int getOpenver;
    /**
     * 查到的倒计时
     */
    private int duration;
    /**
     * 查到的版本
     */
    private int openver;
    /**
     * 查到的次数
     */
    private int frequency;
    /**
     * cancel进入应用、countdown跳过
     */
    private TextView cancel, countdown;
    /**
     * 每隔1000 毫秒执行一次
     */
    private static final int delayTime = 1000;
    private boolean flag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcome_activity);
        initView();
        MobclickAgent.enableEncrypt(true);//设置是否对日志信息进行加密, 默认false(不加密).
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {//Android6.0以上的系统
            int sdPermission = checkSelfPermission(Manifest.permission.READ_PHONE_STATE);//获取手机信息的权限
            if (sdPermission != PackageManager.PERMISSION_GRANTED) {//还没有获取获取手机信息的权限
                requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, Constants.PHONE_CODE);
            } else {
                findBanner();
            }
        } else {
            findBanner();
        }
    }

    private void initView() {
        mViewNeedOffset = findViewById(R.id.view_need_offset);
        mainView = (RelativeLayout) findViewById(R.id.welcome_main);
        pageView = (CustomViewPager) findViewById(R.id.viewPager);
    }

    /***
     * 6.0的系统手动获取权限
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case Constants.PHONE_CODE:
                if (permissions[0].equals(Manifest.permission.READ_PHONE_STATE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//用户同意读取手机信息权限
                    requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, Constants.SD_CODE);//SD权限
                } else {
                    showAlertDialog(requestCode);
                }
                break;
            case Constants.SD_CODE:
                if (permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE) && grantResults[0] == PackageManager.PERMISSION_GRANTED) {//用户不同意SD读写权限
                    findBanner();
                } else {
                    showAlertDialog(requestCode);
                }
                break;
            default:
                break;
        }
    }

//    /**
//     * 开始定位
//     */
//    private void initLocation(){
//        long yqTime = PrefShared.getLong(context,"yqTime");
//        String current = (System.currentTimeMillis())+"";
//        current = current.substring(0,10);
//        long xzTime = Long.parseLong(current);
//        long s = (xzTime - yqTime) / 60;
//        if(s > 10){//十分钟进行一次定位
//            mLocationClient = new AMapLocationClient(getApplicationContext());//初始化定位
//            mLocationClient.setLocationListener(mLocationListener);//设置定位回调监听
//            mLocationOption = new AMapLocationClientOption();//初始化AMapLocationClientOption对象
//            mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);//设置定位模式为高精度模式。
//            mLocationOption.setOnceLocation(true);//获取一次定位结果：该方法默认为false。
//            mLocationOption.setOnceLocationLatest(true);//获取最近3s内精度最高的一次定位结果
//            mLocationClient.setLocationOption(mLocationOption);//给定位客户端对象设置定位参数
//            mLocationClient.startLocation();//启动定位
//        }
//    }

    //声明定位回调监听器
//    public AMapLocationListener mLocationListener = new AMapLocationListener() {
//        @Override
//        public void onLocationChanged(AMapLocation aMapLocation) {
//            if (aMapLocation != null) {
//                if (aMapLocation.getErrorCode() == 0) {
//                    //可在其中解析amapLocation获取相应内容。
//                    double latitude = aMapLocation.getLatitude();//获取纬度
//                    double longitude = aMapLocation.getLongitude();//获取经度
//                    float accuracy = aMapLocation.getAccuracy();//获取精度信息
//                    String address = aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
//                    String country = aMapLocation.getCountry();//国家信息
//                    String province = aMapLocation.getProvince();//省信息
//                    String city = aMapLocation.getCity();//城市信息
//                    String distric = aMapLocation.getDistrict();//城区信息
//                    String street = aMapLocation.getStreet();//街道信息
//                    String streetNum = aMapLocation.getStreetNum();//街道门牌号信息
//                    String cityCode = aMapLocation.getCityCode();//城市编码
//                    String adCode = aMapLocation.getAdCode();//地区编码
//                    String aoiName = aMapLocation.getAoiName();//获取当前定位点的AOI信息
//                    addres = country + province + city + distric + street + streetNum + aoiName;
//                } else {
//                    //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
////                    addres = aMapLocation.getErrorCode()+"";
//                    addres = "定位失败";
//                }
//                PrefShared.saveString(context,"position",addres);
//                String current = (System.currentTimeMillis())+"";
//                current = current.substring(0,10);
//                long xzTime = Long.parseLong(current);
//                PrefShared.saveLong(context,"yqTime",xzTime);
//            }
//        }
//    };

    /**
     * 弹出提示框
     *
     * @param requestCode
     */
    private void showAlertDialog(final int requestCode) {
        final MaterialDialog mMaterialDialog = new MaterialDialog(this);
        mMaterialDialog.setTitle("温馨提示");
        mMaterialDialog.setMessage("仅在您同意的情况下，可以使用本应用的所有服务。");
        mMaterialDialog.setPositiveButton("OK", new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                switch (requestCode) {
                    case Constants.SD_CODE:
                        requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, requestCode);
                        break;
                    case Constants.PHONE_CODE:
                        requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, requestCode);
                        break;
                    default:
                        break;
                }
                mMaterialDialog.dismiss();
            }
        });
        mMaterialDialog.show();
    }

    /**
     * 查找banner或欢迎页
     */
    private void findBanner() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msys", "1");
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        new OkHttpUtils(20).postEnqueue(Constants.FIND_BY_IMG, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                mianHandler.sendEmptyMessage(1);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jsonObject = BaseTools.jsonObject(context, response, "查找banner或欢迎页");
                    JSONArray jsArray = jsonObject.getJSONArray("data");
                    JSONObject jsObject = new JSONObject();
                    jsObject.put("countdown", jsonObject.getInteger("countdown"));//倒计时
                    jsObject.put("frequency", jsonObject.getInteger("frequency"));//显示的次数
                    jsObject.put("openver", jsonObject.getInteger("openver"));//版本
                    jsObject.put("data", jsArray);//图片
                    Message msg = new Message();
                    msg.obj = jsObject;
                    msg.what = 0;
                    mianHandler.sendMessage(msg);
                } catch (Exception e) {
                    mianHandler.sendEmptyMessage(1);
                    e.printStackTrace();
                }
            }
        }, parameter);
    }

    /**
     * 处理banner或欢迎页的线程
     */
    Handler mianHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 0) {
                JSONObject jsObject = (JSONObject) msg.obj;
                if (!jsObject.getJSONArray("data").isEmpty()) {
                    pageView.setScanScroll(false);
                    List<ImageView> imgs = new ArrayList<>();
                    openver = jsObject.getInteger("openver");
                    frequency = jsObject.getInteger("frequency");
                    getFrequency = PrefShared.getInt(context, "banner3");
                    getOpenver = PrefShared.getInt(context, "openver");
                    if (getOpenver != openver) {
                        runNetMain(jsObject, imgs);
                    } else {
                        if (getFrequency != frequency) {
                            runNetMain(jsObject, imgs);
                        } else {
                            runMain();
                        }
                    }
                } else {
                    context.startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                    context.finish();
                }
            } else {
                context.startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                context.finish();
            }
        }
    };

    /**
     * 显示网络图片
     *
     * @param jsObject,imgs
     */
    private void runNetMain(JSONObject jsObject, List<ImageView> imgs) {
        duration = jsObject.getInteger("countdown");
        JSONArray jsArray = jsObject.getJSONArray("data");
        final int size = jsArray.size();
        pageView.setOffscreenPageLimit(size);
        for (int i = 0; i < size; i++) {
            ImageView imageView = new ImageView(WelcomeActivity.this);
            imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
            imageView.setScaleType(ImageView.ScaleType.FIT_XY);
            String url = jsArray.getJSONObject(i).getString("icon_url");
            final int finalI = i;
            Glide.with(context)
                    .load(url)//加载地址
                    .placeholder(R.mipmap.welcome)//加载中的图片
                    .listener(requestListener)
                    .error(R.mipmap.welcome)//加载出错的图片
                    .priority(Priority.HIGH)//优先加载
                    .diskCacheStrategy(DiskCacheStrategy.NONE)//设置缓存策略
                    .into(new GlideDrawableImageViewTarget(imageView) {
                        @Override
                        public void onResourceReady(GlideDrawable resource, GlideAnimation<? super GlideDrawable> animation) {
                            super.onResourceReady(resource, animation);
                            if (finalI == 0) {
                                if (size > 1) {
                                    flag = true;
                                    pageView.setScanScroll(true);//ViewPager禁止滑动
                                } else {
                                    pageView.setScanScroll(true);//ViewPager可以滑动
                                    countdown = new TextView(WelcomeActivity.this);
                                    countdown.setTextColor(ContextCompat.getColor(context, R.color.white));
                                    countdown.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 15);
                                    int countdownWidth = BaseTools.getWindowsWidth(WelcomeActivity.this) / 8;
                                    RelativeLayout.LayoutParams countdownParams = new RelativeLayout.LayoutParams(countdownWidth, countdownWidth);
                                    countdownParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
                                    countdownParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                                    int statusBarHeight = PrefShared.getInt(context, "statusBarHeight");
                                    countdownParams.setMargins(0, statusBarHeight, statusBarHeight, 0);
                                    countdown.setLayoutParams(countdownParams);
                                    countdown.setBackgroundResource(R.drawable.dis_view_bg);
                                    countdown.setGravity(Gravity.CENTER);
                                    countdown.setOnClickListener(countdownListener);
                                    mainView.addView(countdown);
                                    setDuration(duration);
                                    handler.postDelayed(timerRunnable, delayTime);
                                }
                            }
                        }
                    });
            imgs.add(imageView);
        }
        PageAdapter pageAdapter = new PageAdapter(imgs);
        pageView.setAdapter(pageAdapter);
        pageView.addOnPageChangeListener(pageViewListener);
    }

    /**
     * 监听图片出错时的处理
     */
    private RequestListener<String, GlideDrawable> requestListener = new RequestListener<String, GlideDrawable>() {
        @Override
        public boolean onException(Exception e, String model, Target<GlideDrawable> target, boolean isFirstResource) {
            context.startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            context.finish();
            return true;
        }

        @Override
        public boolean onResourceReady(GlideDrawable resource, String model, Target<GlideDrawable> target, boolean isFromMemoryCache, boolean isFirstResource) {
            return false;
        }
    };

    /**
     * 跳过点击事件
     */
    View.OnClickListener countdownListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            duration = 1;
        }
    };

    /**
     * 显示倒计时
     *
     * @param duration
     */
    private void setDuration(Integer duration) {
        countdown.setText(String.format("跳过\n%d s", duration));
    }

    /**
     * 倒计时的计时器
     */
    private Handler handler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if (1 == duration) {//当及时
                handler.removeCallbacks(timerRunnable);
                addPrefShared();
                context.startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
                context.finish();
                return;
            } else {
                setDuration(--duration);
            }
            handler.postDelayed(timerRunnable, delayTime);
        }
    };

    /**
     * 保存次数在本地
     */
    private void addPrefShared() {
        getFrequency = PrefShared.getInt(context, "banner3");
        getOpenver = PrefShared.getInt(context, "openver");
        if (getOpenver != openver) {
            PrefShared.saveInt(context, "openver", openver);
            PrefShared.removeData(context, "banner3");
            if (getFrequency != frequency) {
                getFrequency++;
                PrefShared.saveInt(context, "banner3", getFrequency);
            }
        } else {
            if (getFrequency != frequency) {
                getFrequency++;
                PrefShared.saveInt(context, "banner3", getFrequency);
            }
        }
    }

    /**
     * 滑动到最后一页时显示进入应用按钮
     */
    ViewPager.OnPageChangeListener pageViewListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {

        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case ViewPager.SCROLL_STATE_DRAGGING:
//                    flag= false;
                    break;
                case ViewPager.SCROLL_STATE_SETTLING:
//                    flag = true;
                    break;
                case ViewPager.SCROLL_STATE_IDLE:
                    if (flag) {
                        if (pageView.getCurrentItem() == pageView.getAdapter().getCount() - 1) {
                            cancel = new TextView(WelcomeActivity.this);
                            cancel.setText("进入应用");
                            cancel.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 12);
                            cancel.setTextColor(ContextCompat.getColor(context, R.color.white));
                            int cancelWidth = (int) (BaseTools.getWindowsWidth(WelcomeActivity.this) / 6);
                            RelativeLayout.LayoutParams cancelParams = new RelativeLayout.LayoutParams(cancelWidth, cancelWidth);
                            cancelParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                            cancelParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                            cancelParams.setMargins(0, 0, (int) BaseTools.pxChangeDp(context, 60), (int) BaseTools.pxChangeDp(context, 60));
                            cancel.setLayoutParams(cancelParams);
                            cancel.setBackgroundResource(R.drawable.dis_view_bg);
                            cancel.setGravity(Gravity.CENTER);
                            cancel.setOnClickListener(cancelListener);
                            mainView.addView(cancel);
                            flag = false;
                        }
                    }
                    break;
            }
        }
    };

    /**
     * 进入应用点击事件
     */
    View.OnClickListener cancelListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            pageHandler.sendEmptyMessage(1);
        }
    };

    /**
     * 关闭欢迎页的线程
     */
    Handler pageHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == 1) {
                addPrefShared();
            }
            context.startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
            context.finish();
        }
    };

    /**
     * 显示默认图片
     */
    private void runMain() {
        ImageView imageView = new ImageView(WelcomeActivity.this);
        imageView.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));
        imageView.setScaleType(ImageView.ScaleType.FIT_XY);
        imageView.setImageResource(R.mipmap.welcome);
        mainView.addView(imageView);
        context.startActivity(new Intent(WelcomeActivity.this, MainActivity.class));
        context.finish();
    }

}
