package com.example.timemanage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class MainActivity extends AppCompatActivity {
    private Fragment fragment_main;
    private Fragment fragment_lock;
    private Fragment fragment_statistic;

    static SQLiteDatabase sqLiteDatabase;

    private DatabaseHelper myDatabaseHelper;
    private BottomNavigationView bottomNavigationItemView;

    static Intent autoLock;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragment_main = new MainFragment();
        fragment_lock = new LockFragment();
        fragment_statistic = new StatisticFragment();
        fragmentTransaction.add(R.id.fragment_layout,fragment_main);
        fragmentTransaction.commit();
        bottomNavigationItemView = findViewById(R.id.bottomnavigation);

        myDatabaseHelper = new DatabaseHelper(this,"Course.db",null,1);
        myDatabaseHelper.getWritableDatabase();
        myDatabaseHelper = new DatabaseHelper(this,"SCHEDULE.db",null,1);
        myDatabaseHelper.getWritableDatabase();
        SQLiteDatabase db = new DatabaseHelper(this,"question.db",null,1)
                .getWritableDatabase();
        new InsertQuestion(db);

        MainActivity.sqLiteDatabase = new DatabaseHelper(this,"Course.db",null,1)
                .getWritableDatabase();

        autoLock = new Intent(MainActivity.this, AutoLockService.class);
        startService(autoLock);

        bottomNavigationItemView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                switch (menuItem.getItemId()){
                    case R.id.item_tab1:
                        fragmentTransaction.replace(R.id.fragment_layout, fragment_main);
                        break;
                    case R.id.item_tab2:
                        fragmentTransaction.replace(R.id.fragment_layout, fragment_lock);
                        //跳转计时
//                        fragmentTransaction.addToBackStack(null);
//                        fragmentTransaction.commit();
                        break;
                    case R.id.item_tab3:
                        fragmentTransaction.replace(R.id.fragment_layout, fragment_statistic);
//                        Intent serviceIntent = new Intent(getBaseContext(), AutoLockService.class);
                        break;
                }
                fragmentTransaction.commit();
                return true;
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                checkTopPermission();
            }
        },2000);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(fragment_lock!=null) {
            ((LockFragment) fragment_lock).checkIsBack();
        }

    }

    public void checkTopPermission(){
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权悬浮窗权限", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 1);
        }
    }

    public void showNav(){
        bottomNavigationItemView.setVisibility(View.VISIBLE);
    }
    public void hideNav(){
        bottomNavigationItemView.setVisibility(View.GONE);

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 1) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == 2) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
