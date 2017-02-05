package com.example.egguncle.chattest.util;

import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.example.egguncle.chattest.MyApplication;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by egguncle on 17-2-6.
 */

public class NetWorkUtil {
    private final static String TAG = "InternetUtil";

    public final static String URL_BASE = "http://192.168.1.106:8080/";

    //注册用户（网易云信 测试中
    //
    private final static String URL_REGISTERED="api/user/registered";


    /**
     * 注册帐号
     * @param userName
     * @param nickName
     * @param passwd
     */
    public static void registered(final String userName, final String nickName, final String passwd){
        String url=URL_BASE+URL_REGISTERED;

        StringRequest registeredRequest=new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.i(TAG, "onResponse: "+response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        }){
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String,String> param=new HashMap<>();
                param.put("userName",userName);
                param.put("nickName",nickName);
                //对密码进行MD5加密
                CipherUtil cipherUtil = new CipherUtil();
                String passwdByMd5 = cipherUtil.generatePassword(passwd);
                param.put("passwd",passwdByMd5);

                return param;
            }
        };
        MyApplication.getQueue().add(registeredRequest);
    }


}
