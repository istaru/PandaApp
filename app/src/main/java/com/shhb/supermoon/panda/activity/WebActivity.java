package com.shhb.supermoon.panda.activity;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceError;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.aspsine.swipetoloadlayout.OnRefreshListener;
import com.aspsine.swipetoloadlayout.SwipeToLoadLayout;
import com.jaeger.library.StatusBarUtil;
import com.shhb.supermoon.panda.R;
import com.shhb.supermoon.panda.model.JsObject;

/**
 * Created by superMoon on 2017/8/10.
 */

public class WebActivity extends BaseActivity implements View.OnClickListener, OnRefreshListener {
    private String title;
    private String url;
    private WebView webView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.web_activity);
        title = getIntent().getStringExtra("title");
        url = getIntent().getStringExtra("url");
        initView();
    }

    private void initView() {
        swipeToLoadLayout = (SwipeToLoadLayout) findViewById(R.id.swipeToLoadLayout);
        swipeToLoadLayout.setLoadMoreEnabled(false);
        swipeToLoadLayout.setOnRefreshListener(this);

        webView = (WebView) findViewById(R.id.swipe_target);
        webView.setBackgroundColor(getResources().getColor(R.color.wbv_color));
        if (TextUtils.equals("", title)) {
            findViewById(R.id.titleAll).setVisibility(View.GONE);
        } else {
            mViewNeedOffset = findViewById(R.id.titleAll);
            onBack = (LinearLayout) findViewById(R.id.onBack);
            onBack.setOnClickListener(this);
            webView_title = (TextView) findViewById(R.id.webView_title);
            webView_title.setText(title);
        }
        initWebView();
    }

    private void initWebView() {
        //精致长按事件
        webView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                return false;
            }
        });
        webView.getSettings().setJavaScriptEnabled(true);//支持JavaScript
        webView.getSettings().setAllowFileAccess(true);//允许访问文件
        webView.getSettings().setAllowFileAccessFromFileURLs(true);//通过此API可以设置是否允许通过file url加载的Javascript读取其他的本地文件
        webView.getSettings().setAllowUniversalAccessFromFileURLs(true);//通过此API可以设置是否允许通过file url加载的Javascript可以访问其他的源，包括其他的文件和http,https等其他的源
        //图片显示
        webView.getSettings().setLoadsImagesAutomatically(true);
        //自适应屏幕
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        webView.setWebChromeClient(new WebChromeClient());
        webView.addJavascriptInterface(new JsObject(webView, this), "native_android");
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                swipeToLoadLayout.setRefreshing(false);
                swipeToLoadLayout.setRefreshEnabled(false);
            }

            @Override
            public void onReceivedError(WebView view, WebResourceRequest request, WebResourceError error) {
                super.onReceivedError(view, request, error);
                swipeToLoadLayout.setRefreshing(false);
                swipeToLoadLayout.setRefreshEnabled(false);
            }

            @Override
            public void onReceivedHttpError(WebView view, WebResourceRequest request, WebResourceResponse errorResponse) {
                super.onReceivedHttpError(view, request, errorResponse);
                swipeToLoadLayout.setRefreshing(false);
                swipeToLoadLayout.setRefreshEnabled(false);
            }
        });
        webView.setScrollbarFadingEnabled(false);
        //关闭缩放
        webView.getSettings().setBuiltInZoomControls(false);
        webView.getSettings().setSupportZoom(false);
        webView.getSettings().setDisplayZoomControls(false);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(lifeCycle == 1){
            swipeToLoadLayout.post(new Runnable() {
                @Override
                public void run() {
                    swipeToLoadLayout.setRefreshing(true);
                    lifeCycle = 1;
                }
            });
        }
    }

    @Override
    public String getJson() {
        return getIntent().getStringExtra("data");
    }

    @Override
    public void onRefresh() {
        webView.post(new Runnable() {
            @Override
            public void run() {
                webView.loadUrl(url);
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (webView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.onResume();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if (webView != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            webView.onPause();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (webView != null) {
            webView.destroy();
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.onBack:
                finish();
                break;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if(webView.canGoBack()){
                webView.goBack();
                String webViewUrl = webView.getUrl();
                webViewUrl = webViewUrl.substring(webViewUrl.lastIndexOf("/")+1);
                if(TextUtils.equals(webViewUrl,"fast.html")){
                    swipeToLoadLayout.setRefreshEnabled(true);
                }
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void setStatusBar() {
        if (TextUtils.equals("", title)) {
            StatusBarUtil.setTranslucentForImageView(this, 0, swipeToLoadLayout);
        } else {
            StatusBarUtil.setTranslucentForImageView(this, 0, mViewNeedOffset);
        }
    }
}
