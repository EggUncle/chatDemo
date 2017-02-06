package com.example.egguncle.chattest.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.egguncle.chattest.R;
import com.example.egguncle.chattest.util.NetWorkUtil;
import com.example.egguncle.chattest.util.SPUtil;

public class RegisteredActivity extends AppCompatActivity {

    private EditText edRegisteredUsername;
    private EditText edRegisteredPasswd;
    private EditText edRegisteredNickname;
    private Button btnRegistered;

    private NetWorkUtil netWorkUtil;


    //广播相关
    private RegisteredActivity.RegisteredReceiver registeredReceiver;
    private LocalBroadcastManager localBroadcastManager;
    private IntentFilter intentFilter;
    public final static String REGISTERED_BROADCAST = "com.uncle.egg.REGISTERED_BROADCAST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
        initData();
        initView();
        initAction();
    }

    private void initView() {
        edRegisteredUsername = (EditText) findViewById(R.id.ed_registered_username);
        edRegisteredPasswd = (EditText) findViewById(R.id.ed_registered_passwd);
        edRegisteredNickname = (EditText) findViewById(R.id.ed_registered_nickname);
        btnRegistered = (Button) findViewById(R.id.btn_registered);
    }

    private void initAction() {
        btnRegistered.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = edRegisteredUsername.getText().toString();
                String nickName = edRegisteredNickname.getText().toString();
                //密码不区分大小写
                String passWd = edRegisteredPasswd.getText().toString().toLowerCase();
                //发送请求
                netWorkUtil.registered(userName, nickName, passWd);
            }
        });
    }


    private void initData() {
        //广播相关
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        netWorkUtil = new NetWorkUtil(localBroadcastManager);
        intentFilter = new IntentFilter();
        intentFilter.addAction(REGISTERED_BROADCAST);

        registeredReceiver = new RegisteredActivity.RegisteredReceiver();
        localBroadcastManager.registerReceiver(registeredReceiver, intentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        localBroadcastManager.unregisterReceiver(registeredReceiver);
    }

    private class RegisteredReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            boolean success = intent.getBooleanExtra("success", false);
            if (success) {
                finish();
            } else {
                Toast.makeText(RegisteredActivity.this, "error", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
