package com.shhb.supermoon.panda.tools;

/**
 * Created by superMoon on 2017/7/13.
 */
public class Constants {

    /** SD卡权限返回的code*/
    public static final int SD_CODE = 101;
    /** 手机权限返回的code*/
    public static final int PHONE_CODE = 102;
    /** 定位权限返回的code*/
    public static final int LOCATION_CODE = 103;
    /** 相机权限返回的code*/
    public static final int CAMERA_CODE = 104;

    /** 登录成功后的通知*/
    public static final String SENDMSG_LOGIN = "com.login";
    /** 单击分享时的通知*/
    public static final String SENDMSG_SHARE = "com.share";
    /** 显示主页面的通知*/
    public static final String SENDMSG_SHOW = "com.show";
    /** 关闭Activity*/
    public static final String SENDMSG_CLOSE = "com.close";

    /** 微信appID */
    public static final String WX_APPID = "wx993541c17e26850f";
    /** 微信secret */
    public static final String WX_SECRET = "7fc3a7ad0f4ed0e81c6c400906dfd496";
    /** QQappID */
    public static final String QQ_APPID = "1106353948";
    /** QQsecret */
    public static final String QQ_SECRET = "04UrbrTZNltgsgQQ";
    /** 微博appID */
    public static final String WB_APPID = "1471432164";
    /** 微博secret */
    public static final String WB_SECRET = "76332eeb5d5f6ec3f99a2919ae2c3e4e";

    /** 创建App文件夹的名子*/
    public static final String APP_FILE_URL = "PandaApp";

    /** 创建存放图片的地址*/
    public static final String IMAGE_PATH = "Images";

    //服务器接口地址
    public static final String REQUEST = "http://v1.pandadaiapp.com/app_user/index.php?C=";
//    public static final String REQUEST = "http://192.168.1.101:8080/dkuan/app_user/index.php/";

    //服务器HTML页面地址
    public static final String HTML_REQUEST = "http://v1.pandadaiapp.com/panda/";
//    public static final String HTML_REQUEST = "http://192.168.1.152:8020/panda/";

    //打开QQ客服的地址
    public static final String OPEN_QQ = "mqqwpa://im/chat?chat_type=wpa&uin=3457460878";

    /** 基本信息认证页面 */
    public static final String NEWS_HTML = HTML_REQUEST + "baseInfo.html";
    /** 贷款详情页面 */
    public static final String LOAN_HTML = HTML_REQUEST + "fast.html";
    /** 去提现页面 */
    public static final String POSTAL_HTML = HTML_REQUEST + "tixian.html";
    /** 注册协议页面 */
    public static final String REGISTER_HTML = HTML_REQUEST + "zcxy.html";
    /** 服务条款页面 */
    public static final String FORM_HTML = HTML_REQUEST + "zqs.html";
    /** 服务条款页面 */
    public static final String FWXY_HTML = HTML_REQUEST + "fwxy.html";

    /** 查找开化页的图片*/
    public static final String FIND_BY_IMG = REQUEST + "opening";
    /** 查找首页数据*/
    public static final String FIND_BY_HOME = REQUEST + "home";
    /** 极速贷或小额贷 */
    public static final String FIND_BY_SPEED_LOAN = REQUEST + "thirdPartyLoanAppList";
    /** 获取用户的意向贷信息 */
    public static final String FIND_BY_INTENTION = REQUEST + "getintention";
    /** 申请意向贷 */
    public static final String APPLY_LOAN = REQUEST + "addintention";
    /** 查找邀请信息*/
    public static final String FIND_BY_SHARE = REQUEST + "goinvite";
    /** 获取图形验证码*/
    public static final String GET_GRA_CODE = REQUEST + "getgracode";
    /** 获取验证码*/
    public static final String GET_VCODE = REQUEST + "vcode/sendCode";
    /** 用户注册*/
    public static final String USER_REGISTER = REQUEST + "login/register";
    /** 用户登录*/
    public static final String USER_LOGIN = REQUEST + "login/login";
    /** 微信登录*/
    public static final String WX_LOGIN = REQUEST + "wxLogin";
    /** 绑定微信*/
    public static final String BIND_WX = REQUEST + "wechatAuthorization";
    /** 个人身份证上传*/
    public static final String ID_UPLOAD = REQUEST + "idCardUpload";
    /** 检查用户是否认证过身份证件*/
    public static final String FIND_ID = REQUEST + "checkWhetherIdAuthentication";
    /** 查询用户认证了哪些东西*/
    public static final String FIND_USER_RZ = REQUEST + "userAuthenticationInformation";
    /** 查询消息通知*/
    public static final String FIND_MESSAGE = REQUEST + "mymessage";
    /** 查询我的贷款订单列表*/
    public static final String FIND_LOAN_ORDER = REQUEST + "myLoanOrders";
    /** 设置消息推送*/
    public static final String SET_PUSH = REQUEST + "setpush";
    /** 查询我的收入列表*/
    public static final String FIND_MY_INCOME = REQUEST + "myincome";
    /** 查询邀请记录*/
    public static final String FIND_INVITE_RECORD = REQUEST + "invitedToRecord";
    /** 查询提现记录*/
    public static final String FIND_WITHDRAW_RECORD = REQUEST + "withdraw";
    /** 查询是否有消息图片*/
    public static final String FIND_MSG_ICON = REQUEST + "saveUserLoginAndDeviceRecord";
}
