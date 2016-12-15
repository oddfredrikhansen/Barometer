package com.example.fredrik.barometer;

import android.app.Activity;
import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.PopupMenu;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.color.holo_green_dark;

public class MainActivity extends AppCompatActivity  implements SensorEventListener{

    private Sensor mPressure;
    private SensorManager mSensorManager;
    private int previous=0;
    Integer counter=11;
    Map<String, Integer> pressures = new HashMap<String, Integer>();
    MySqlHandler mDbHelper = new MySqlHandler(this); //Instantiera en databas

    LineGraphSeries<DataPoint> grafData = new LineGraphSeries<>(new DataPoint[] {
            //new DataPoint(counter, bar),
            new DataPoint(0, 1013),
            new DataPoint(1, 1017),
            new DataPoint(2, 1015),
            new DataPoint(3, 1019),
            new DataPoint(4, 1011),
            new DataPoint(5, 1018),
            new DataPoint(6, 1019),
            new DataPoint(7, 1025),
            new DataPoint(8, 1020),
            new DataPoint(9, 1023),
            new DataPoint(10, 1018)
//            new DataPoint(11, 1025),
//            new DataPoint(12, 1015),
//            new DataPoint(13, 1025),
//            new DataPoint(14, 1027),
//            new DataPoint(15, 1025),
//            new DataPoint(16, 1010),
//            new DataPoint(17, 1025)
    });
    //Map<Date, Integer> pressures = new HashMap<Date, Integer>();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // show menu when menu button is pressed
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.my_tab_menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // display a message when a button was pressed
        String message = "";
        if (item.getItemId() == R.id.wtf) {
            message = "A little bit more to the right!";
        }
        else if (item.getItemId() == R.id.cleardatabase) {
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            //mDbHelper.getLastIndex(db);
            mDbHelper.onUpgrade(db,1,2);
            message = "Database erased";
        }
        else if (item.getItemId() == R.id.cleargraph) {
            clearGraphValues();
            message = "Graph erased";
        }
        else if (item.getItemId() == R.id.about) {
            message = "Fredrik Hansen made this";
        }
        else {
            message = "Something went wrong here!?";
        }
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
        return true;
    }

    /**
     * Tar bort värdena som visas på grafen.
     * Databasen behåller sina värden
     */
    private void clearGraphValues(){
        Log.d("ClearGraphValues", " GRAPH ");
        pressures.clear();
        GraphView graph = (GraphView) findViewById(R.id.graph);
        graph.removeAllSeries();
        grafData = new LineGraphSeries<>(new DataPoint[] {});
        previous=0;
    }
    public void onClick(View view) {
        mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

        Button button = (Button) findViewById(R.id.button);
        if (button.getText().toString().contains("Start")) {
            Log.d("onClick", " Started");
            this.onResume();
            button.setText("Stop");

            button.setBackgroundColor(Color.RED);
        }

        else {
            Log.d("onClick", " Stopped");
            this.onPause();
            button.setText("Start");
            button.setBackgroundColor(Color.GREEN);
        }
    }


    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_UI);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    /**
     * Metod att avrunda decimaltalet till närmsta heltal
     * @param f
     * @return
     */
    private int round(float f){
        float absNumber = Math.abs(f);
        int i = (int) absNumber;
        float result = absNumber - (float) i;
        if(result<0.5){
            return f<0 ? -i : i;
        }else{
            return f<0 ? -(i+1) : i+1;
        }
    }
    public void onSensorChanged(SensorEvent event) {

        float pressure_value;
//        float height = 0.0f;
        int bar=0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm");
        String currentDateandTime = sdf.format(new Date());


        TextView tv = (TextView) findViewById(R.id.textView);
        TextView tv3 = (TextView) findViewById(R.id.textView3);
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        if (Sensor.TYPE_PRESSURE == event.sensor.getType()) {

            pressure_value = event.values[0];
            bar = round(pressure_value);
            if(bar!=previous){
                previous=bar;

                GraphView graph = (GraphView) findViewById(R.id.graph);
                graph.getViewport().setScrollable(true); // enables horizontal scrolling
                graph.getViewport().setScrollableY(false); // enables vertical scrolling
                graph.getViewport().setScalable(true); // enables horizontal zooming and scrolling
                graph.getViewport().setScalableY(true); // enables vertical zooming and scrolling

                graph.getViewport().setMaxX(20d);
                graph.setTitle("mBar/hPa");

                grafData.appendData(new DataPoint(counter, bar),true,counter);
                graph.addSeries(grafData);

                if(bar<970) {
                    tv3.setText("Lågtryck, Stormigt");
                    tv.setTextColor(Color.BLUE);
                    iv.setBackgroundResource(R.drawable.storm);
                }
                else if(bar<990) {
                    tv3.setText("Lågtryck, Regn");
                    tv.setTextColor(Color.LTGRAY);
                    iv.setBackgroundResource(R.drawable.rain1);
                }
                else if(bar<1010) {
                    tv3.setText("Väderomslag");
                    tv.setTextColor(Color.BLACK);
                    iv.setBackgroundResource(R.drawable.cloud1);
                }
                else if(bar<1030) {
                    tv3.setText("Högtryck, Stabilt väder");
                    tv.setTextColor(Color.rgb(40,150,20));
                    iv.setBackgroundResource(R.drawable.clear1);
                }
                else {
                    tv3.setText("Högtryck, Torka");
                    tv.setTextColor(Color.RED);
                    iv.setBackgroundResource(R.drawable.sun1);
                }

                //Sätt det nya värdet i textview fönstret
                tv.setText(" "+bar +" ");
                pressures.put(currentDateandTime,bar);
                //Spara resultaten till databasen
                SQLiteDatabase db = mDbHelper.getWritableDatabase();
                ContentValues values = new ContentValues();
                values.put(MySqlHandler.ID, counter);
                values.put(MySqlHandler.DATE, currentDateandTime);
                values.put(MySqlHandler.MEASUREMENT, bar);
                // Insert the new row, returning the primary key value of the new row
                try {
                    Log.d("Insert rows :", "" + db.insertWithOnConflict(MySqlHandler.TABLENAME, null, values, SQLiteDatabase.CONFLICT_REPLACE));
                }catch(Exception e) {Log.e("SQL error: ", "insert fail! "+e);}


                Log.d("Event match new value :"," bar: "+bar+ ", actual pressure: "+pressure_value+" DB: "+db.getPageSize());
                counter++;
            }
            //height = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure_value);
            Log.d("Event :", " bar: " + pressure_value+ "    "+bar+"\t"+ currentDateandTime+" ");
        }

    }
}


