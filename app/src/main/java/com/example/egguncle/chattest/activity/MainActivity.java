package com.example.egguncle.chattest.activity;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.egguncle.chattest.R;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private LinearLayout activityMain;
    private TextView text;
    private EditText edMessage;
    private Button btnSend;
    private EditText edUsername;

    private final static String TAG = "MainActivity";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
        initAction();
        initData();
    }

    private void initView() {
        activityMain = (LinearLayout) findViewById(R.id.activity_main);
        text = (TextView) findViewById(R.id.text);
        edMessage = (EditText) findViewById(R.id.ed_message);
        btnSend = (Button) findViewById(R.id.btn_send);
        edUsername = (EditText) findViewById(R.id.ed_username);
    }

    private void initData() {
        Observer<List<IMMessage>> incomingMessageObserver =
                new Observer<List<IMMessage>>() {
                    @Override
                    public void onEvent(List<IMMessage> messages) {
                        // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
                        text.append(messages.get(0).getContent());
                    }
                };
        NIMClient.getService(MsgServiceObserve.class)
                .observeReceiveMessage(incomingMessageObserver, true);
    }

    private void initAction() {
        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String userName = edUsername.getText().toString();
                String userMessage = edMessage.getText().toString();
                // 创建文本消息
                IMMessage message = MessageBuilder.createTextMessage(
                        userName, // 聊天对象的 ID，如果是单聊，为用户帐号，如果是群聊，为群组 ID
                        SessionTypeEnum.P2P, // 聊天类型，单聊或群组
                        userMessage // 文本内容
                );
                // 发送消息。如果需要关心发送结果，可设置回调函数。发送完成时，会收到回调。如果失败，会有具体的错误码。
                //false表示发送消息，true表示重发
                NIMClient.getService(MsgService.class).sendMessage(message, false).setCallback(new RequestCallback<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i(TAG, "onSuccess: ");
                    }

                    @Override
                    public void onFailed(int i) {
                        Log.i(TAG, "onFailed: error code is " + i);
                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }
                });

            }
        });
    }
}
