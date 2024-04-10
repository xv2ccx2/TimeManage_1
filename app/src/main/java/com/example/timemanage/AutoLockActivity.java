package com.example.timemanage;

import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.Time;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class AutoLockActivity extends AppCompatActivity {

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {                                                           //重写返回键方法
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            Toast.makeText(AutoLockActivity.this, "您点击了back键", Toast.LENGTH_SHORT).show();
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onPause() {                                                                                     //重写recent键方法
        super.onPause();
        for (int j = 0; j < 50; j++){
            ActivityManager activityManager = (ActivityManager) getApplicationContext()
                    .getSystemService(Context.ACTIVITY_SERVICE);
            activityManager.moveTaskToFront(getTaskId(), 0);
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_lock);
        HomeReceiver innerReceiver = new HomeReceiver();                                                        //注册广播
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
        registerReceiver(innerReceiver, intentFilter, Context.RECEIVER_EXPORTED);
        Calendar c = Calendar.getInstance();
        int m =  c.get(Calendar.MINUTE);
        Button button = findViewById(R.id.yes_btn);
        TextView textView = findViewById(R.id.question);
        EditText editText = findViewById(R.id.answer_edt);
        Chronometer s_c = findViewById(R.id.simple_c);

//        s_c.setFormat("");
        s_c.setBase(SystemClock.elapsedRealtime());
        s_c.setCountDown(false);
//        s_c.setFormat("计时: %s 秒");
        s_c.start();

        Context context = this;
        button.setVisibility(View.INVISIBLE);

        s_c.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
            @Override
            public void onChronometerTick(Chronometer chronometer) {
                if((SystemClock.elapsedRealtime() - s_c.getBase())/1000 == 60){
                    button.setVisibility(View.VISIBLE);
                    s_c.stop();
                    s_c.setVisibility(View.GONE);
                }
                //Log.i("Ccc", String.valueOf((SystemClock.elapsedRealtime() - s_c.getBase())/1000));
            }
        });

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(editText.getText().toString().equals("9")){
                    unregisterReceiver(innerReceiver);                                             //取消注册
                    Intent intent = new Intent(AutoLockActivity.this,MainActivity.class);
                    startActivity(intent);
                    AutoLockActivity.this.finish();
                }
            }
        });

    }
}