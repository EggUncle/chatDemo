package com.example.egguncle.chattest.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.egguncle.chattest.R;
import com.example.egguncle.chattest.util.NetWorkUtil;

public class RegisteredActivity extends AppCompatActivity {

    private EditText edRegisteredUsername;
    private EditText edRegisteredPasswd;
    private EditText edRegisteredNickname;
    private Button btnRegistered;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);
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
                NetWorkUtil.registered(userName, nickName,passWd);
            }
        });
    }


}
