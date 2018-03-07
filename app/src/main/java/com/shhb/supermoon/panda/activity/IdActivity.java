package com.shhb.supermoon.panda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.jaeger.library.StatusBarUtil;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.tools.BaseTools;
import com.shhb.supermoon.panda.tools.Constants;
import com.shhb.supermoon.panda.tools.GlideLoader;
import com.shhb.supermoon.panda.tools.OkHttpUtils;
import com.shhb.supermoon.panda.tools.PrefShared;
import com.yancy.imageselector.ImageConfig;
import com.yancy.imageselector.ImageSelector;
import com.yancy.imageselector.ImageSelectorActivity;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;
import top.zibin.luban.Luban;
import top.zibin.luban.OnCompressListener;

/**
 * Created by superMoon on 2017/8/8.
 */

public class IdActivity extends BaseActivity implements View.OnClickListener, OnRefreshListener {

    private LinearLayout yyz, wyz;
    private ImageView idimg1, idimg2;
    private Button upload;
    public static final int REQUEST_CODE = 1000;
    private ArrayList<String> path = new ArrayList<>();
    private ImageConfig imageConfig;
    private int type = 1;
    private Map<Integer,Object> base64 = new HashMap<>();
    private boolean flag1 = false;
    private boolean flag2 = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.id_activity);
        initView();
    }

    private void initView() {
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setLoadMoreEnabled(false);
        swipeToLoadLayout.setOnRefreshListener(this);

        mViewNeedOffset = findViewById(R.id.titleAll);
        webView_title = (TextView) findViewById(R.id.webView_title);
        webView_title.setText("身份证上传");
        onBack = (LinearLayout) findViewById(R.id.onBack);
        yyz = (LinearLayout) findViewById(R.id.yyz);
        wyz = (LinearLayout) findViewById(R.id.wyz);
        onBack.setOnClickListener(this);
        idimg1 = (ImageView) findViewById(R.id.idimg1);
        idimg1.setOnClickListener(this);
        idimg2 = (ImageView) findViewById(R.id.idimg2);
        idimg2.setOnClickListener(this);
        upload = (Button) findViewById(R.id.upload);
        upload.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (lifeCycle == 1) {
            swipeToLoadLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeToLoadLayout.setRefreshing(true);
                    lifeCycle = 2;
                }
            });
        }
    }

    @Override
    public void onRefresh() {
        swipeToLoadLayout.setRefreshing(true);
        findByYZXX();
    }

    /**
     * 查询身份证是否验证
     */
    private void findByYZXX() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msys", "1");
        jsonObject.put("uid", PrefShared.getString(context, "userId"));
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        new OkHttpUtils(20).postEnqueue(Constants.FIND_ID, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        wyz.setVisibility(View.VISIBLE);
                    }
                });
                cLoading(swipeToLoadLayout);
                showToast(1, context.getResources().getString(R.string.msg_net));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = "";
                try {
                    cLoading(swipeToLoadLayout);
                    JSONObject jsonObject = BaseTools.jsonObject(context, response, "查询身份证是否验证");
                    int status = jsonObject.getInteger("status");
                    if (1 == status) {
                        jsonObject = jsonObject.getJSONObject("data");
                        final JSONObject finalJsonObject = jsonObject;
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (1 == finalJsonObject.getInteger("type")) {
                                    wyz.setVisibility(View.GONE);
                                    yyz.setVisibility(View.VISIBLE);
                                } else {
                                    yyz.setVisibility(View.GONE);
                                    wyz.setVisibility(View.VISIBLE);
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

    private void setImageSize() {
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(
                (int) (BaseTools.getWindowsWidth(context) / 1.3),
                BaseTools.getWindowsHeight(context) / 4
        );
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(
                (int) (BaseTools.getWindowsWidth(context) / 1.55),
                BaseTools.getWindowsHeight(context) / 4
        );
        params1.gravity = Gravity.CENTER;
        params2.gravity = Gravity.CENTER;
        idimg1.setLayoutParams(params1);
        idimg2.setLayoutParams(params2);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.idimg1:
                type = 1;
                selectImg();
                break;
            case R.id.idimg2:
                type = 2;
                selectImg();
                break;
            case R.id.upload:
                showToast(0, context.getResources().getString(R.string.msg_jz));
                uploadID();
                break;
            case R.id.onBack:
                finish();
                break;
        }
    }

    private void selectImg() {
        try {
            File dir = BaseTools.makeFile(Constants.IMAGE_PATH);
            if (!dir.exists()) dir.mkdirs();
            imageConfig
                    = new ImageConfig.Builder(
                    // GlideLoader 可用自己用的缓存库
                    new GlideLoader())
                    // 如果在 4.4 以上，则修改状态栏颜色 （默认黑色）
                    .steepToolBarColor(context.getResources().getColor(R.color.app_color))
                    // 标题的背景颜色 （默认黑色）
                    .titleBgColor(context.getResources().getColor(R.color.app_color))
                    // 提交按钮字体的颜色  （默认白色）
                    .titleSubmitTextColor(context.getResources().getColor(R.color.white))
                    // 标题颜色 （默认白色）
                    .titleTextColor(context.getResources().getColor(R.color.white))
                    // 开启多选   （默认为多选）  (单选 为 singleSelect)
                    .singleSelect()
//                        .crop()
                    // 多选时的最大数量   （默认 9 张）
                    .mutiSelectMaxSize(9)
                    // 已选择的图片路径
                    .pathList(path)
                    // 拍照后存放的图片路径（默认 /temp/picture）
                    .filePath(dir.getPath())
                    // 开启拍照功能 （默认开启）
                    .showCamera()
                    .requestCode(REQUEST_CODE)
                    .build();
            ImageSelector.open(IdActivity.this, imageConfig);   // 开启图片选择器
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTranslucentForImageView(this, 0, mViewNeedOffset);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            List<String> pathList = data.getStringArrayListExtra(ImageSelectorActivity.EXTRA_RESULT);
            path.clear();
            path.addAll(pathList);
            String idUrl = path.get(path.size()-1);
            if (type == 1) {
                imageConfig.getImageLoader().displayImage(context, idUrl, idimg1);
                flag1 = true;
            } else {
                imageConfig.getImageLoader().displayImage(context, idUrl, idimg2);
                flag2 = true;
            }
            File file = new File(idUrl);
            final boolean finalFlag1 = flag1;
            final boolean finalFlag2 = flag2;
            Luban.with(this)
                    .load(file)                     //传人要压缩的图片
                    .setCompressListener(new OnCompressListener() { //设置回调
                        @Override
                        public void onStart() {
                            // TODO 压缩开始前调用，可以在方法内启动 loading UI
                        }

                        @Override
                        public void onSuccess(File file) {
                            // TODO 压缩成功后调用，返回压缩后的图片文件
                            try {
                                base64.put(type,BaseTools.fileToBase64(file));
                                if(finalFlag1 == true && finalFlag2 == true){
                                    upload.setEnabled(true);
                                    upload.setTextColor(ContextCompat.getColor(context, R.color.white));
                                    upload.setBackgroundResource(R.drawable.btn_blue_bg);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onError(Throwable e) {
                            // TODO 当压缩过程出现问题时调用
                        }
                    }).launch();//启动压缩
        }
    }

    /**
     * 个人身份上传
     */
    private void uploadID() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msys", "1");
        jsonObject.put("uid", PrefShared.getString(context, "userId"));
        jsonObject.put("id_card_front", base64.get(1));
        jsonObject.put("id_card_back", base64.get(2));
        String parameter = jsonObject.toString();
        new OkHttpUtils(120).postEnqueue(Constants.ID_UPLOAD, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                showToast(1, context.getResources().getString(R.string.msg_net));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = "";
                try {
                    JSONObject jsonObject = BaseTools.jsonObject(context, response, "个人身份上传");
                    int status = jsonObject.getInteger("status");
                    msg = jsonObject.getString("msg");
                    if (1 == status) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                wyz.setVisibility(View.GONE);
                                yyz.setVisibility(View.VISIBLE);
                            }
                        });
                    }
                } catch (Exception e) {
                    msg = context.getResources().getString(R.string.msg_send);
                    e.printStackTrace();
                }
                showToast(1, msg);
            }
        }, parameter);
    }
}
