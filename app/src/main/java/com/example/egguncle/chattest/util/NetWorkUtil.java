package com.example.egguncle.chattest.util;

import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.egguncle.chattest.MyApplication;
import com.example.egguncle.chattest.activity.LoginActivity;
import com.example.egguncle.chattest.activity.RegisteredActivity;
import com.example.egguncle.chattest.model.LoginJson;
import com.example.egguncle.chattest.model.UserEntity;
import com.google.gson.Gson;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by egguncle on 17-2-6.
 */

public class NetWorkUtil {
    private final static String TAG = "NetWorkUtil";

    public final static String URL_BASE = "http://192.168.1.106:8080/";

    //注册用户（网易云信 测试中
    //
    private final static String URL_REGISTERED = "api/user/registered";

    //登录用的URL  POST  参数 userName passwd
    private final static String URL_LOGIN = URL_BASE + "api/user/login";

    private LocalBroadcastManager mLocalBroadcastManager;

    public NetWorkUtil() {
    }

    public NetWorkUtil(LocalBroadcastManager localBroadcastManager) {
        mLocalBroadcastManager = localBroadcastManager;
    }


    /**
     * 注册帐号
     *
     * @param userName
     * @param nickName
     * @param passwd
     */
    public void registered(final String userName, final String nickName, final String passwd) {
        final String url = URL_BASE + URL_REGISTERED;

        StringRequest registeredRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "onResponse: " + response);
                Gson gson = new Gson();
                LoginJson loginJson = gson.fromJson(response, LoginJson.class);
                if (!loginJson.isError()) {
                    Log.i(TAG, "onResponse: success");
                    //若没有错误
                    UserEntity userEntity = loginJson.getUserEntity();

                    //保存用户信息
                    SPUtil.getInstance(MyApplication.getMyContext()).saveUserInfo(userEntity);

                    String nickName = userEntity.getNickname();
                    String userName = userEntity.getUsername();
                    String token = userEntity.getToken();

                    //登录网易云信
                    doLoginWithIM(userName, token);

                    Log.i(TAG, "onResponse: " + nickName);
                    Log.i(TAG, "onResponse: " + userName);
                    Log.i(TAG, "onResponse: " + token);

                    //给RegisteredAcitvity发送广播，通知其关闭
                    Intent intent = new Intent(RegisteredActivity.REGISTERED_BROADCAST);
                    intent.putExtra("success", true);
                    mLocalBroadcastManager.sendBroadcast(intent);
                } else {
                    Log.i(TAG, "onResponse: failed");
                    //给RegisteredAcitvity发送广播，通知注册过程出现错误
                    Intent intent = new Intent(RegisteredActivity.REGISTERED_BROADCAST);
                    intent.putExtra("success", false);
                    mLocalBroadcastManager.sendBroadcast(intent);

                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("userName", userName);
                param.put("nickName", nickName);
                //对密码进行MD5加密
                CipherUtil cipherUtil = new CipherUtil();
                String passwdByMd5 = cipherUtil.generatePassword(passwd);
                param.put("passwd", passwdByMd5);

                return param;
            }
        };
        MyApplication.getQueue().add(registeredRequest);
    }

    /**
     * 登录使用的方法
     *
     * @param userName
     * @param passwd
     */
    public void login(final String userName, final String passwd) {
        StringRequest requestLogin = new StringRequest(Request.Method.POST, URL_LOGIN, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //解析数据
                Log.i(TAG, "onResponse: " + response);
                Gson gson = new Gson();
                LoginJson loginJson = gson.fromJson(response, LoginJson.class);
                if (!loginJson.isError()) {
                    UserEntity user=loginJson.getUserEntity();

                    Log.i(TAG, "onResponse: " + loginJson.getUserEntity().getUsername());
                    Log.i(TAG, "onResponse: " + loginJson.getUserEntity().getBgPath());

                    Log.i(TAG, "onResponse: " + loginJson.isError());

                    //拼接出图片的地址
                    String bgpath = URL_BASE + loginJson.getUserEntity().getBgPath();
                    String iconPath = URL_BASE + loginJson.getUserEntity().getIconPath();
                    Log.i(TAG, "onResponse: " + bgpath);
                    //将拼接出的图片地址设置给user
                    loginJson.getUserEntity().setBgPath(bgpath);
                    loginJson.getUserEntity().setIconPath(iconPath);

                    //进行网易云信的登录
                    doLoginWithIM(user.getUsername(),user.getToken());

                    Intent intent = new Intent(LoginActivity.LOGIN_BROADCAST);
                    intent.putExtra("success", true);
                    //发送广播给loginactivity的接收器
                    mLocalBroadcastManager.sendBroadcast(intent);
                } else {
                    Intent intent = new Intent(LoginActivity.LOGIN_BROADCAST);
                    intent.putExtra("success", false);
                    //发送广播给loginactivity的接收器
                    mLocalBroadcastManager.sendBroadcast(intent);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i(TAG, "onErrorResponse: " + error);
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> param = new HashMap<>();
                param.put("userName", userName);
                //对密码进行MD5加密
                CipherUtil cipherUtil = new CipherUtil();
                String passwdByMd5 = cipherUtil.generatePassword(passwd);
                param.put("passwd", passwdByMd5);

                return param;
            }
        };
        MyApplication.getQueue().add(requestLogin);
    }


    /**
     * 登录的到网易云信的方法
     *
     * @param userName
     * @param token
     */
    public static void doLoginWithIM(String userName, String token) {
        LoginInfo info = new LoginInfo(userName, token); // config...
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo loginInfo) {
                        Log.i(TAG, "onSuccess: is success");
                        //将登录信息保存入sp中
                        SPUtil spUtil = SPUtil.getInstance(MyApplication.getMyContext());
                        UserEntity userEntity = new UserEntity();
                        userEntity.setUsername(loginInfo.getAccount());
                        userEntity.setToken(loginInfo.getToken());
                        spUtil.saveUserInfo(userEntity);
//                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
//                        startActivity(intent);
                    }

                    @Override
                    public void onFailed(int i) {
                        Log.i(TAG, "onFailed: failed ,error :" + i);
                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }
                    // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
                };
        NIMClient.getService(AuthService.class).login(info)
                .setCallback(callback);
    }

}
