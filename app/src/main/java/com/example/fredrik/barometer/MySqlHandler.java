package com.example.fredrik.barometer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.File;

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

    public static final String CREATE_DB ="CREATE TABLE " +TABLENAME + " ("+ID+" ID, "+ DATE + " DATE, " + MEASUREMENT + " MEASUREMENT"+" );";
    public static final String DELETE_DB ="DROP TABLE IF EXISTS " + TABLENAME;;
    public static final String LAST_ID = "SELECT ID FROM "+TABLENAME+" ORDER BY ID DESC LIMIT 1";

    public MySqlHandler(Context context){
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDB(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d("onUpgrade","Upgrade of database table");
        try {
            db.execSQL(DELETE_DB);
            onCreate(db);
        }catch(Exception e){
            Log.e("Error","Database upgrade failed"+e);}
    }
    public void getLastIndex(SQLiteDatabase db){
        Cursor cursor = db.rawQuery(LAST_ID ,null);
        Log.d("SQL ", "LAST INDEX "+cursor.getColumnCount());
    }

    public void deleteDatabaseTable(SQLiteDatabase db){
        Log.d("deleteDatabaseTable","Deletion of database table");
        try {
            db.execSQL(DELETE_DB);
        }catch(Exception e){
            Log.e("Error","Database deletion failed"+e);}
    }

    private void createDB(SQLiteDatabase db){
        db.execSQL(CREATE_DB);
    }
}
