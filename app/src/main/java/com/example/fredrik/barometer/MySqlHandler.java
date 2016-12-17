package com.example.fredrik.barometer;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by Fredrik Hansen on 2016-12-10.
 *
 */

public class MySqlHandler extends SQLiteOpenHelper {
    protected static String LAST_DB_VALUE ="";
    protected static final String  DBNAME      ="Barran.db";
    protected static final int     VERSION     = 1;
    protected static final String  TABLENAME   ="MYTABLE";
    protected static final String  ID          ="ID";
    protected static final String  DATE        ="DATE";
    protected static final String  MEASUREMENT ="MEASUREMENT";

    protected static final String CREATE_DB ="CREATE TABLE " +TABLENAME + " ("+ID+" ID, "+ DATE + " DATE, " + MEASUREMENT + " MEASUREMENT"+" );";
    protected static final String DELETE_DB ="DROP TABLE IF EXISTS " + TABLENAME;
    private static final String LAST_ID = "SELECT ID FROM "+TABLENAME+" ORDER BY ID DESC LIMIT 1";
    private static final String GETALL = "SELECT ID FROM "+TABLENAME;


    public MySqlHandler(Context context){
        super(context, DBNAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        createDB(db);
        Log.d("PATH for database :",db.getPath());
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


    protected String getAll(SQLiteDatabase db){
        String query = "SELECT * from MYTABLE order by ROWID";
        String content="";
        Cursor cursor = db.rawQuery(query,null);
        int i=0;
        List<String> fileName = new ArrayList<>();
        if (cursor.moveToFirst()&& cursor!=null){
            fileName.add(cursor.getString(cursor.getColumnIndex(ID)));
            while(cursor.moveToNext()){
                content = " "+content + "\n "+ " \t" + cursor.getString(cursor.getColumnIndex(DATE))+" \t   "+cursor.getString(cursor.getColumnIndex(MEASUREMENT));
                //Log.d("getAll ","while: "+" "+cursor.getString(cursor.getColumnIndex(ID))+" "+cursor.getString(cursor.getColumnIndex(DATE))+" "+cursor.getString(cursor.getColumnIndex(MEASUREMENT))+" "+(i++));
                fileName.add(cursor.getString(cursor.getColumnIndex(DATE)));
                LAST_DB_VALUE=cursor.getString(cursor.getColumnIndex(MEASUREMENT));
            }
        }
        Log.d("getAll "," Count: "+"# ");
        return content;
    }
    /**
     * Hämta sista inlagda elementet i databasen
     * @param db in
     */
    public int getLastIndex(SQLiteDatabase db){
        int lastId=1;
        String query = "SELECT ROWID from MYTABLE order by ROWID DESC limit 1";
        Cursor c = db.rawQuery(query,null);
        if (c != null && c.moveToFirst()) {
            lastId = c.getInt(0); //Nollan är kolumn index
        }
        Log.d("PRINT LAST INDEX ","Count: "+"# "+lastId+" VALUE :"+LAST_DB_VALUE);
        return lastId;
    }



    private void createDB(SQLiteDatabase db){
        db.execSQL(CREATE_DB);
    }
}
