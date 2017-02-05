package com.example.egguncle.chattest.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.egguncle.chattest.R;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

public class LoginActivity extends AppCompatActivity {

    private EditText edUsername;
    private EditText edPasswd;
    private Button btn;
    private Button btnToRegistered;






    private final static String TAG="LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initView();
        initAction();
    }

    private void initView(){
        edUsername = (EditText) findViewById(R.id.ed_username);
        edPasswd = (EditText) findViewById(R.id.ed_passwd);
        btn = (Button) findViewById(R.id.btn_login);
        btnToRegistered = (Button) findViewById(R.id.btn_to_registered);

    }

    private void initAction(){
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
                //注销的方法
                //NIMClient.getService(AuthService.class).logout();
            }
        });
        btnToRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               Intent intent=new Intent(LoginActivity.this,RegisteredActivity.class);
                startActivity(intent);
            }
        });
    }

    public void doLogin() {
        LoginInfo info = new LoginInfo(edUsername.getText().toString().toLowerCase(),edPasswd.getText().toString().toLowerCase()); // config...
        RequestCallback<LoginInfo> callback =
                new RequestCallback<LoginInfo>() {
                    @Override
                    public void onSuccess(LoginInfo loginInfo) {
                        Log.i(TAG, "onSuccess: is success");
                        Intent intent=new Intent(LoginActivity.this,MainActivity.class);
                        startActivity(intent);
                    }

                    @Override
                    public void onFailed(int i) {
                        Log.i(TAG, "onFailed: failed ,error :" +i);
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
