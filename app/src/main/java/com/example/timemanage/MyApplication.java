package com.example.timemanage;

import android.app.Application;
import android.content.Intent;

import com.example.timemanage.popup.FloatingImageDisplayService;

public class MyApplication extends Application {

    public void startHome(){
        Intent intent = new Intent(this, MainActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivity(intent);
    }


}
