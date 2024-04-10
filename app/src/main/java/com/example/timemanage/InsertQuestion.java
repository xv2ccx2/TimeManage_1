package com.example.timemanage;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class InsertQuestion {
    SQLiteDatabase sqLiteDatabase;
    public InsertQuestion(SQLiteDatabase database){
        this.sqLiteDatabase = database;
        this.insert();
    }

    private void insert(){
//        Map<String,String> qs = new HashMap<>();
//        qs.put("曲面z=x+2y+ln(1+x^2+y^2)在点(0,0,0)处的切平面方程为_","x+2y-z=0");
        Cursor cursor = sqLiteDatabase.query("question", null, null, null, null, null, null);
        int count = cursor.getCount();
        ContentValues values = new ContentValues();
//        Set<String> key = qs.keySet();
//        Collection<String> entry = qs.values();
        values.put("id", count+1);
        values.put("content", "曲面z=x+2y+ln(1+x^2+y^2)在点(0,0,0)处的切平面方程为_");
        values.put("answer", "x+2y-z=0");
        sqLiteDatabase.insert("question",null,values);
    }

}
