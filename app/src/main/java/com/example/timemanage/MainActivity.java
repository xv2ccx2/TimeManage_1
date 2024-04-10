package com.example.timemanage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.MenuItem;

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
        BottomNavigationView bottomNavigationItemView = findViewById(R.id.bottomnavigation);

        myDatabaseHelper = new DatabaseHelper(this,"Course.db",null,1);
        myDatabaseHelper.getWritableDatabase();
        myDatabaseHelper = new DatabaseHelper(this,"SCHEDULE.db",null,1);
        myDatabaseHelper.getWritableDatabase();
        SQLiteDatabase db = new DatabaseHelper(this,"question.db",null,1)
                .getWritableDatabase();
        new InsertQuestion(db);

        MainActivity.sqLiteDatabase = new DatabaseHelper(this,"Course.db",null,1)
                .getWritableDatabase();

        Intent intent = new Intent(MainActivity.this, AutoLockService.class);
        startService(intent);

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
    }
}
