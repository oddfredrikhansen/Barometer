package com.example.fredrik.barometer;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by fredrik on 2016-12-14.
 */

public class MySqlHandler extends SQLiteOpenHelper {
    public static final String  DBNAME      ="Barran.db";
    public static final int     VERSION     = 1;
    public static final String  TABLENAME   ="MYTABLE";
    public static final String  ID          ="ID";
    public static final String  DATE        ="DATE";
    public static final String  MEASUREMENT ="MEASUREMENT";

    public MySqlHandler(Context context){
        super(context, DBNAME,null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private void createDB(SQLiteDatabase db){
        db.execSQL("create table "+TABLENAME +"("+ID+"integer primery key, "+DATE+" date, "+MEASUREMENT+" values"+");");
    }
}
