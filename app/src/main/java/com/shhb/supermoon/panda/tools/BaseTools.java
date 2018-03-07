package com.shhb.supermoon.panda.tools;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Base64;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import com.alibaba.fastjson.JSONObject;
import com.shhb.supermoon.panda.activity.LoginActivity;
import com.shhb.supermoon.panda.ciphertext.AES;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.Response;

/**
 * Created by superMoon on 2017/7/31.
 */

public class BaseTools {

    /**
     * 是否启动加密
     */
    private static boolean isEncryt = false;

    /**
     * 获取屏幕的宽度
     */
    public final static int getWindowsWidth(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    /**
     * 获取屏幕的高度
     */
    public final static int getWindowsHeight(Activity activity) {
        DisplayMetrics dm = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.heightPixels;
    }

    /**
     * px转化为dp
     */
    public final static float pxChangeDp(Context context, float pxValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * dp转化为px
     */
    public final static float dpChangePx(Context context, float dpValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public final static int dp2px(Context context, float value) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, value, context.getResources().getDisplayMetrics());
    }

    /**
     * bitmap转为base64
     *
     * @param bitmap
     * @return
     */
    public static String bitmapToBase64(Bitmap bitmap) {
        String result = null;
        ByteArrayOutputStream baos = null;
        try {
            if (bitmap != null) {
                baos = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                baos.flush();
                baos.close();
                byte[] bitmapBytes = baos.toByteArray();
                result = Base64.encodeToString(bitmapBytes, Base64.DEFAULT);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (baos != null) {
                    baos.flush();
                    baos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    /**
     * base64转为bitmap
     * @param base64Data
     * @return
     */
    public static Bitmap base64ToBitmap(String base64Data) {
        Bitmap bitmap = null;
        try {
            byte[] bitmapArray;
            bitmapArray = Base64.decode(base64Data.split(",")[1], Base64.DEFAULT);
            bitmap = BitmapFactory.decodeByteArray(bitmapArray, 0, bitmapArray.length);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    /**
     * 文件转base64字符串
     *
     * @param file
     * @return
     */
    public static String fileToBase64(File file) {
        String base64 = null;
        InputStream in = null;
        try {
            in = new FileInputStream(file);
            byte[] bytes = new byte[in.available()];
            int length = in.read(bytes);
            base64 = Base64.encodeToString(bytes, 0, length, Base64.DEFAULT);
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return base64;
    }

    /**
     * base64字符串转文件
     *
     * @param base64
     * @return
     */
    public static File base64ToFile(String base64) {
        File file = null;
        String fileName = "/Petssions/record/testFile.amr";
        FileOutputStream out = null;
        try {
            // 解码，然后将字节转换为文件
            file = new File(Environment.getExternalStorageDirectory(), fileName);
            if (!file.exists())
                file.createNewFile();
            byte[] bytes = Base64.decode(base64, Base64.DEFAULT);// 将字符串转换为byte数组
            ByteArrayInputStream in = new ByteArrayInputStream(bytes);
            byte[] buffer = new byte[1024];
            out = new FileOutputStream(file);
            int bytesum = 0;
            int byteread = 0;
            while ((byteread = in.read(buffer)) != -1) {
                bytesum += byteread;
                out.write(buffer, 0, byteread); // 文件写操作
            }
        } catch (IOException ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return file;
    }

    /**
     * 打开输入法
     */
    public final static void openInput(final EditText view) {
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
            }
        }, 500);
    }

    /**
     * 关闭输入法
     */
    public final static void closeInput(Activity context, EditText view) {
        InputMethodManager inputMethodManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(context.getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    /**
     * 设置添加屏幕的背景透明度
     *
     * @param bgAlpha 屏幕透明度0.0-1.0 1表示完全不透明
     */
    public static void setBackgroundAlpha(Activity context, float bgAlpha) {
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlpha;
        context.getWindow().setAttributes(lp);
    }

    /**
     * 创建文件夹
     *
     * @param fileName 文件夹的名称
     * @return
     */
    public static File makeFile(String fileName) {
        File appDir = new File(Environment.getExternalStorageDirectory(), Constants.APP_FILE_URL + "/" + fileName);
        if (!appDir.exists()) {
            appDir.mkdir();
        }
        return appDir;
    }

    /**
     * 验证是否为数字
     *
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (!TextUtils.equals("", str)) {
            Pattern pattern = Pattern.compile("[0-9]*");
            Matcher isNum = pattern.matcher(str);
            if (!isNum.matches()) {
                return false;
            }
            return true;
        } else {
            return false;
        }
    }

    /**
     * 处理第一步数据
     *
     * @param context
     * @param response
     * @param methodName 方法名 打印日志用
     * @return
     */
    public static JSONObject jsonObject(Activity context, Response response, String methodName) {
        JSONObject jsonObject = null;
        try {
            String json = response.body().string();
            if (TextUtils.equals("个人身份上传", methodName) || TextUtils.equals("获取图形验证码", methodName)) {
                jsonObject = JSONObject.parseObject(json);
            } else {
                jsonObject = JSONObject.parseObject(decryptJson(json));
            }
            Log.e(methodName, jsonObject.toString());
            int status = jsonObject.getInteger("status");
            if (401 == status) {
                context.startActivity(new Intent(context, LoginActivity.class));
            } else if (402 == status) {
                PrefShared.removeData(context, "phoneNum");
                PrefShared.removeData(context, "userId");
                PrefShared.removeData(context, "push");
                PrefShared.removeData(context, "weChat");
                PrefShared.removeData(context, "userImg");
                PrefShared.removeData(context, "userName");
                PrefShared.removeData(context, "userGrade");
                PrefShared.removeData(context, "userPrice");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return jsonObject;
    }

    /**
     * 加密方法
     *
     * @param data
     * @return
     */
    public static String encodeJson(String data) {
        if (isEncryt == true) {
            long secret = AES.get10Random();
            String content = AES.encrypt(data, AES.md5(AES.longMinusNum(secret + "")));
            JSONObject jsonEncrypt = new JSONObject();
            jsonEncrypt.put("secret", secret);
            jsonEncrypt.put("content", content);
            return jsonEncrypt.toString();
        } else {
            return data;
        }
    }

    /**
     * 获取参数并解密
     *
     * @param json
     * @return
     */
    public static String decryptJson(String json) {
        if (isEncryt == true) {
            String result = "";
            try {
                JSONObject jsonObject = JSONObject.parseObject(json);
                String secret = jsonObject.getString("secret");
                String content = jsonObject.getString("content");
                result = AES.decrypt(content, AES.md5(AES.longMinusNum(secret)));
            } catch (Exception e) {
                result = json;
                e.printStackTrace();
            }
            return result;
        }
        return json;
    }
}
