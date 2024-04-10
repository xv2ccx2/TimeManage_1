package com.example.timemanage;

import android.annotation.SuppressLint;
import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;
import android.util.Log;

import java.time.LocalDateTime;
import java.util.Calendar;
import java.util.Timer;

public class AutoLockService extends Service {
    SQLiteDatabase sqLiteDatabase =  MainActivity.sqLiteDatabase;

    public void startMyActivity(){
        Intent intent = new Intent(this, AutoLockActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
    }
    public AutoLockService() {

    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Calendar calendar = Calendar.getInstance();
        int week_now = calendar.get(Calendar.DAY_OF_WEEK);
        int hour =  calendar.get(Calendar.HOUR_OF_DAY);
        int m =  calendar.get(Calendar.MINUTE);
        String query = "SELECT * FROM course where category = '学习' and week = "+week_now;
        Cursor cursor = sqLiteDatabase.rawQuery(query,null);
        int count = cursor.getCount();
        Log.i("TTT", String.valueOf(count));
        if(count != 0){
            int[] sh = new int[count];
            int[] sm = new int[count];
            int[] eh = new int[count];
            int[] em = new int[count];
            int i = 0;
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                @SuppressLint("Range") String endTime = cursor.getString(cursor.getColumnIndex("endTime"));
                @SuppressLint("Range") int week = cursor.getInt(cursor.getColumnIndex("week"));
                String week_string = switch (week) {
                    case 1 -> "周一";
                    case 2 -> "周二";
                    case 3 -> "周三";
                    case 4 -> "周四";
                    case 5 -> "周五";
                    case 6 -> "周六";
                    case 7 -> "周日";
                    default -> "周一";
                };
                int sh24 = Integer.parseInt(startTime.split(":")[0]);
                sh[i] = sh24;
                sm[i] = Integer.parseInt(startTime.split(":")[1]);

                sh24 = Integer.parseInt(endTime.split(":")[0]);
                eh[i] = sh24;
                em[i] = Integer.parseInt(endTime.split(":")[1]);

                Log.i("TGA1", String.valueOf(sh[i]));
                Log.i("TGA2", String.valueOf(sm[i]));
                Log.i("TGA3", String.valueOf(eh[i]));
                Log.i("TGA4", String.valueOf(em[i]));
                i++;
            }
            int closeTime = sh[0] - hour,index = 0;
            for (int j = 0; j < count;j++){
                if(hour - sh[j] < 0){
                    if(closeTime > sh[j] - hour){
                        closeTime = sh[j] - hour;
                        index = j;
                    }
                }
            }
            int closeM = 0;
            for (int j = 0; j < count;j++){
                if(hour == sh[j]){
                   closeM = sh[j] - m;
                   index = j;
                   j = count;
                }
            }
            for (int j = 0; j < count;j++){
                if(hour == sh[j]){
                    if(sm[j] - m >= 0){
                        if(Math.abs(closeM) > sm[j] - m){
                            closeM = sh[j] - m;
                            index = j;
                        }
                    }
                }
            }

            Log.i("TGA5", String.valueOf(index));
            Log.i("TGA6", String.valueOf(hour));
            Log.i("TGA7", String.valueOf(m));

            Log.i("TGA9", String.valueOf(sh[index]));
            Log.i("TGA10", String.valueOf(sm[index]));

            if(hour == sh[index] && m == sm[index]){
                this.startMyActivity();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}