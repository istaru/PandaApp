//package com.shhb.supermoon.panda.wxapi;
//
//import android.app.Activity;
//import android.content.Intent;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.shhb.supermoon.panda.application.MainApplication;
//import com.shhb.supermoon.panda.model.WXLoginEvent;
//import com.tencent.mm.opensdk.modelbase.BaseReq;
//import com.tencent.mm.opensdk.modelbase.BaseResp;
//import com.tencent.mm.opensdk.modelmsg.SendAuth;
//import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
//
//import org.greenrobot.eventbus.EventBus;
//
//import static com.shhb.supermoon.panda.application.MainApplication.iwxapi;
//
///**
// * Created by superMoon on 2017/8/10.
// */
//
//public class WXEntryActivity1 extends Activity implements IWXAPIEventHandler {
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        MainApplication.addActivity(this);
//        iwxapi.handleIntent(this.getIntent(), this);
//    }
//
//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        setIntent(intent);
//        iwxapi.handleIntent(intent, this);
//    }
//
//    @Override
//    public void onReq(BaseReq baseReq) {
//        Log.e("什么openID", baseReq.openId);
//    }
//
//    /***请求微信的相应码**/
//    @Override
//    public void onResp(BaseResp baseResp) {
//        String result = "";
//        if(baseResp.getType() == 1){
//            result = "登录";
//        } else {
//            result = "分享";
//        }
//        switch (baseResp.errCode) {
//            case BaseResp.ErrCode.ERR_OK:
//                EventBus.getDefault().post(new WXLoginEvent(result + "成功", ((SendAuth.Resp) baseResp).code));
//                break;
//            case BaseResp.ErrCode.ERR_SENT_FAILED:
//                EventBus.getDefault().post(new WXLoginEvent(result + "失败", ""));
//                break;
//            case BaseResp.ErrCode.ERR_USER_CANCEL:
//                EventBus.getDefault().post(new WXLoginEvent("取消" + result, ""));
//                break;
//            case BaseResp.ErrCode.ERR_AUTH_DENIED:
//                EventBus.getDefault().post(new WXLoginEvent("拒绝" + result, ""));
//                break;
//        }
//        finish();
//    }
//}
