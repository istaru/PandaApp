package com.shhb.supermoon.panda.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.jaeger.library.StatusBarUtil;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.tools.Constants;

/**
 * Created by superMoon on 2017/8/11.
 */

public class OperateActivity extends BaseActivity implements View.OnClickListener{
    private TextView operateTk;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.operate_activity);
        initView();
    }

    private void initView() {
        mViewNeedOffset = findViewById(R.id.titleAll);
        onBack = (LinearLayout) findViewById(R.id.onBack);
        onBack.setOnClickListener(this);
        webView_title = (TextView) findViewById(R.id.webView_title);
        webView_title.setText("手机认证");
        operateTk = (TextView) findViewById(R.id.operate_tk);
        operateTk.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.onBack:
                finish();
                break;
            case R.id.operate_tk:
                intent = new Intent(context,WebActivity.class);
                intent.putExtra("url", Constants.FWXY_HTML);
                intent.putExtra("title","服务条款");
                startActivity(intent);
                break;
        }
    }

    @Override
    protected void setStatusBar() {
        StatusBarUtil.setTranslucentForImageView(this, 0, mViewNeedOffset);
    }
}
