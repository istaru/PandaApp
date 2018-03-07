package com.shhb.supermoon.panda.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.text.ClipboardManager;
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
import com.shhb.supermoon.panda.model.UMShare;
import com.shhb.supermoon.panda.tools.BaseTools;
import com.shhb.supermoon.panda.tools.Constants;
import com.shhb.supermoon.panda.tools.OkHttpUtils;
import com.shhb.supermoon.panda.tools.PrefShared;
import com.shhb.supermoon.panda.view.Encoder;
import com.umeng.socialize.UMShareAPI;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by superMoon on 2017/8/3.
 */

public class ShareActivity extends BaseActivity implements View.OnClickListener ,OnRefreshListener {
    private Encoder mEncoder;
    private ImageView QRCode;
    private Bitmap bitmap;
    private String title,content,img,inviteUrl,shareUrl;
    private TextView link;
    private Button copyL,uInvite;
    private TextView share;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.share_activity);
        initView();
    }

    private void initView() {
        swipeToLoadLayout = (SwipeToLoadLayout)findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setLoadMoreEnabled(false);
        swipeToLoadLayout.setOnRefreshListener(this);

        mViewNeedOffset = findViewById(R.id.titleAll);
        QRCode = (ImageView) findViewById(R.id.QRCode);
        mEncoder = new Encoder.Builder()
                .setBackgroundColor(0xffffffff) // 指定背景颜色，默认为白色
                .setCodeColor(0xff000000) // 指定编码块颜色，默认为黑色
                .setOutputBitmapWidth((int) BaseTools.dpChangePx(context,350)) // 生成图片宽度
                .setOutputBitmapHeight((int) BaseTools.dpChangePx(context,350)) // 生成图片高度
                .setOutputBitmapPadding(1) // 设置为没有白边
                .build();
        onBack = (LinearLayout) findViewById(R.id.onBack);
        onBack.setOnClickListener(this);
        onShare = (LinearLayout) findViewById(R.id.onShare);
        onShare.setVisibility(View.VISIBLE);
        onShare.setOnClickListener(null);
        webView_title = (TextView) findViewById(R.id.webView_title);
        webView_title.setText("邀请好友");
        share = (TextView) findViewById(R.id.on_share);
        share.setOnClickListener(this);
        link = (TextView) findViewById(R.id.link);
        copyL = (Button) findViewById(R.id.copyL);
        copyL.setOnClickListener(this);
        uInvite = (Button) findViewById(R.id.uInvite);
        uInvite.setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(lifeCycle == 1){
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
        createQRCode();
    }

    /**
     * 查询分享信息
     */
    private void createQRCode() {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("msys","1");
        jsonObject.put("uid", PrefShared.getString(context,"userId"));
        String parameter = BaseTools.encodeJson(jsonObject.toString());
        new OkHttpUtils(20).postEnqueue(Constants.FIND_BY_SHARE, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                cLoading(swipeToLoadLayout);
                showToast(1,context.getResources().getString(R.string.msg_net));
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String msg = "";
                try {
                    cLoading(swipeToLoadLayout);
                    JSONObject jsonObject = BaseTools.jsonObject(context,response,"查询分享信息");
                    int status = jsonObject.getInteger("status");
                    if(1 == status){
                        JSONObject dataObject = jsonObject.getJSONObject("data");
                        title = dataObject.getString("title");
                        content = dataObject.getString("content");
                        img = dataObject.getString("share_img");
                        inviteUrl = dataObject.getString("invite_url");
                        shareUrl = dataObject.getString("share_url");
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                link.setText(inviteUrl);
                                bitmap = mEncoder.encode(inviteUrl);
                                QRCode.setImageBitmap(bitmap);
                                onShare.setOnClickListener(ShareActivity.this);
                                uInvite.setEnabled(true);
                                uInvite.setTextColor(ContextCompat.getColor(context,R.color.white));
                                uInvite.setBackgroundResource(R.drawable.btn_blue_bg);
                            }
                        });
                    } else {
                        msg = jsonObject.getString("msg");
                    }
                } catch (Exception e){
                    msg = context.getResources().getString(R.string.msg_send);
                    e.printStackTrace();
                }
                showToast(1,msg);
            }
        }, parameter);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.onBack:
                finish();
                break;
            case R.id.on_share:
                Intent intent = new Intent(context,MyIncomeActivity.class);
                intent.putExtra("type","邀请记录");
                startActivity(intent);
                break;
            case R.id.onShare:
                new UMShare(this,"分享给好友").share(title,content,img,shareUrl);
                break;
            case R.id.copyL:
                try {
                    ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
                    clipboard.setText(inviteUrl);
                    showDialog("已复制邀请链接",inviteUrl+"\n(该链接仅在微信中使用)");
                } catch (Exception e){
                    showDialog("温馨提示","邀请链接复制失败");
                }
                break;
            case R.id.uInvite:
                new UMShare(this,"邀请好友").share(title,content,img,inviteUrl);
                break;
        }
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
