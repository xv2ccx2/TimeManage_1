package com.example.timemanage;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String COURSE = "create table course("
            + "id integer primary key autoincrement,"
            + "name text,"
            + "startTime text,"
            + "endTime text,"
            + "week integer," +
            "category text)";

    //日程信息用于数据统计
    public static final String SCHEDULE = "create table schedule("
            + "id integer primary key autoincrement,"
            + "schedule_name text,"
//            + "schedule_startTime integer,"
//            + "schedule_endTime integer,"
            + "schedule_dateTime String,"
            + "schedule_sum_time integer)";

    public static final String QUESTION = "create table question("
            + "id integer primary key autoincrement,"
            + "content text,"
            + "answer text)";
    private Context mContext;
    public DatabaseHelper(@Nullable Context context, @Nullable String name, @Nullable SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        mContext=context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        //System.out.println(COURSE);
        db.execSQL(COURSE);
        db.execSQL(SCHEDULE);
        db.execSQL(QUESTION);
        //Toast.makeText(mContext, "Create succeed", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
