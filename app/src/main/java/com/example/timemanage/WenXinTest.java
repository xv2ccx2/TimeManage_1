package com.example.timemanage;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.json.JSONException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import static java.security.AccessController.getContext;

//此activity主要用来实现聊天界面
public class WenXinTest extends Activity {

    private EditText et_chat;
    private Button btn_send;
    private ChatlistAdapter chatAdapter;
    private List<Chatlist> mDatas;

    private RecyclerView rc_chatlist;
    final int MESSAGE_UPDATE_VIEW = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wen_xin_test);
        init();
        //聊天信息
        mDatas = new ArrayList<Chatlist>();
        Chatlist C1;
        C1=new Chatlist("ABC：","Hello,world!");
        mDatas.add(C1);
        Chatlist C2;
        C2=new Chatlist("DEF：","This is a new app.");
        mDatas.add(C2);


        Toolbar toolbar = findViewById(R.id.toolbar_wenxin);
        //点击返回,返回mainActivity
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(WenXinTest.this,MainActivity.class);
                startActivity(intent);
                WenXinTest.this.finish();
            }
        });

        //可以通过数据库插入数据

        chatAdapter=new ChatlistAdapter(this,mDatas);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this );
        rc_chatlist.setLayoutManager(layoutManager);
        //如果可以确定每个item的高度是固定的，设置这个选项可以提高性能
        rc_chatlist.setHasFixedSize(true);
        //创建并设置Adapter
        rc_chatlist.setAdapter(chatAdapter);


        //点击btn_send发送聊天信息
        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //用户的提问
                String user_ask=et_chat.getText().toString();//获取输入框里的信息
                Chatlist C3;
                C3=new Chatlist("User：",user_ask);
                mDatas.add(C3);

                chatAdapter.ResetChatlistAdapter(mDatas);
                rc_chatlist.setAdapter(chatAdapter);


                //文心一言的回答（以下才是用到WenXin.java的地方）
                new Thread(new Runnable(){
                    @Override
                    public void run() {
                        //请求详情
                        Chatlist C4;

                        try {
                            WenXin wx=new WenXin();

                            C4=new Chatlist("WenXin：",wx.GetAnswer(user_ask));
                        } catch (IOException | JSONException e) {
                            throw new RuntimeException(e);
                        } finally {
                        }
                        mDatas.add(C4);
                        chatAdapter.ResetChatlistAdapter(mDatas);

                        Message msg = new Message();
                        msg.what = MESSAGE_UPDATE_VIEW;
                        WenXinTest.this.gHandler.sendMessage(msg);
                    }
                }).start();
                /*为什么要弄new Thread...这样呢？
                因为像这种网络请求往往有延迟，需要新开一个进程去处理，与下面的gHandler相对应
                当app收到来自文心一言的回答后，就去通知gHandler更新界面，把回答的段落显示出来
                */
                et_chat.setText("");
            }
        });
    }

    private void init(){//执行一些初始化操作
        btn_send=findViewById(R.id.btn_send);
        et_chat=findViewById(R.id.et_chat);
        rc_chatlist=(RecyclerView) findViewById(R.id.rc_chatlist);
    }


    public Handler gHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MESSAGE_UPDATE_VIEW) {
                rc_chatlist.setAdapter(chatAdapter);//更新对话界面
            }
        }
    };

}