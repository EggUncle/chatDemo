package com.example.egguncle.chattest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.egguncle.chattest.R;
import com.example.egguncle.chattest.model.UserEntity;
import com.example.egguncle.chattest.util.NetWorkUtil;
import com.example.egguncle.chattest.util.SPUtil;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

public class LoginActivity extends AppCompatActivity {

    private EditText edUsername;
    private EditText edPasswd;
    private Button btn;
    private Button btnToRegistered;

    //广播相关
    private LoginActivity.LoginReceiver loginReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;
    public final static String LOGIN_BROADCAST = "com.uncle.egg.LOGIN_BROADCAST";

    private final static String TAG = "LoginActivity";

    private NetWorkUtil netWorkUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initData();
        initView();
        initAction();
    }

    private void initView() {
        edUsername = (EditText) findViewById(R.id.ed_username);
        edPasswd = (EditText) findViewById(R.id.ed_passwd);
        btn = (Button) findViewById(R.id.btn_login);
        btnToRegistered = (Button) findViewById(R.id.btn_to_registered);

    }

    private void initAction() {
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName=edUsername.getText().toString();
                String passWd=edPasswd.getText().toString();
                netWorkUtil.login(userName,passWd);
                //注销的方法
                //NIMClient.getService(AuthService.class).logout();
            }
        });
        btnToRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, RegisteredActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void initData() {
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        intentFilter = new IntentFilter();
        intentFilter.addAction(LOGIN_BROADCAST);
        loginReceiver = new LoginReceiver();
        localBroadcastManager.registerReceiver(loginReceiver, intentFilter);

        netWorkUtil=new NetWorkUtil(localBroadcastManager);
    }

//    public void doLoginWithIM() {
//
//        LoginInfo info = new LoginInfo(edUsername.getText().toString().toLowerCase(), edPasswd.getText().toString().toLowerCase()); // config...
//        RequestCallback<LoginInfo> callback =
//                new RequestCallback<LoginInfo>() {
//                    @Override
//                    public void onSuccess(LoginInfo loginInfo) {
//                        Log.i(TAG, "onSuccess: is success");
//                        //将登录信息保存入sp中
//                        SPUtil spUtil=SPUtil.getInstance(LoginActivity.this);
//                        UserEntity userEntity=new UserEntity();
//                        userEntity.setUsername(loginInfo.getAccount());
//                        userEntity.setToken(loginInfo.getToken());
//                        spUtil.saveUserInfo(userEntity);
//                        finish();
////                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
////                        startActivity(intent);
//                    }
//
//                    @Override
//                    public void onFailed(int i) {
//                        Log.i(TAG, "onFailed: failed ,error :" + i);
//                    }
//
//                    @Override
//                    public void onException(Throwable throwable) {
//
//                    }
//                    // 可以在此保存LoginInfo到本地，下次启动APP做自动登录用
//                };
//        NIMClient.getService(AuthService.class).login(info)
//                .setCallback(callback);
//    }

    private class LoginReceiver extends BroadcastReceiver{
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra("success", false);
            if (success) {
                finish();
            } else {
                Toast.makeText(LoginActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
