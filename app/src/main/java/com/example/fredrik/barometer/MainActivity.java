package com.example.fredrik.barometer;

import android.app.Activity;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.SystemClock;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.R.color.holo_green_dark;

public class MainActivity extends Activity implements SensorEventListener{

//    private final SensorManager mSensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
//    private final Sensor mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);

//    private TextView tv;
    private Sensor mPressure;
    private SensorManager mSensorManager;
    private int previous=0;
    Integer counter=11;
    Map<String, Integer> pressures = new HashMap<String, Integer>();
    MySqlHandler helper = new MySqlHandler(this); //Instantiera en databas

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
//        tv = (TextView) findViewById(R.id.textView);
        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
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
        mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
    }

    protected void onPause() {
        super.onPause();
        mSensorManager.unregisterListener(this);
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public void onSensorChanged(SensorEvent event) {

        float pressure_value;
        float height = 0.0f;
        int bar=0;
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd HH:mm");
        String currentDateandTime = sdf.format(new Date());
//        Date timestamp = new Date(currentDateandTime);
//
        Calendar calendar = Calendar.getInstance();
        Date d1 = calendar.getTime();

        TextView tv = (TextView) findViewById(R.id.textView);
        TextView tv3 = (TextView) findViewById(R.id.textView3);
        ImageView iv = (ImageView) findViewById(R.id.imageView);
        if (Sensor.TYPE_PRESSURE == event.sensor.getType()) {

            pressure_value = event.values[0];
            bar = (int)pressure_value;
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


                tv.setText(" "+bar +" ");
                pressures.put(currentDateandTime,bar);

                Log.d("Event :"," bar: "+pressures.values()+ " : " + pressure_value+ "    "+bar +"   "+pressures.size()+"  :  "+pressures.values().toString());
                counter++;
            }
            //height = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure_value);


            Log.d("Event :", " bar: " + pressure_value+ "    "+bar+"\t"+ currentDateandTime);

        }

    }
}

//    public Sensor getDefaultSensor(int type) {
//        // TODO: need to be smarter, for now, just return the 1st sensor
//        List<Sensor> l = getSensorList(type);
//        boolean wakeUpSensor = false;
//        // For the following sensor types, return a wake-up sensor. These types are by default
//        // defined as wake-up sensors. For the rest of the SDK defined sensor types return a
//        // non_wake-up version.
//        if (type == Sensor.TYPE_PROXIMITY || type == Sensor.TYPE_SIGNIFICANT_MOTION ||
//                type == Sensor.TYPE_TILT_DETECTOR || type == Sensor.TYPE_WAKE_GESTURE ||
//                type == Sensor.TYPE_GLANCE_GESTURE || type == Sensor.TYPE_PICK_UP_GESTURE) {
//            wakeUpSensor = true;
//        }
//
//        for (Sensor sensor : l) {
//            if (sensor.isWakeUpSensor() == wakeUpSensor) return sensor;
//        }
//        return null;
//    }

//    class SensorActivity extends Activity implements SensorEventListener {
//        private final SensorManager mSensorManager;
//        private final Sensor mPressure;
//
//        public SensorActivity() {
//            mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
//            mPressure = mSensorManager.getDefaultSensor(Sensor.TYPE_PRESSURE);
//        }
//
//        protected void onResume() {
//            super.onResume();
//            mSensorManager.registerListener(this, mPressure, SensorManager.SENSOR_DELAY_NORMAL);
//        }
//
//        protected void onPause() {
//            super.onPause();
//            mSensorManager.unregisterListener(this);
//        }
//
//        public void onAccuracyChanged(Sensor sensor, int accuracy) {
//        }
//
//        public void onSensorChanged(SensorEvent event) {
//            float pressure_value;
//            float height = 0.0f;
//            TextView tv = (TextView) findViewById(R.id.textView);
//            if (Sensor.TYPE_PRESSURE == event.sensor.getType()) {
//                Log.d("Event :", " " + event + " " + event.values[0]);
//                pressure_value = event.values[0];
//                height = SensorManager.getAltitude(SensorManager.PRESSURE_STANDARD_ATMOSPHERE, pressure_value);
//                tv.setText("" + height);
//            }
//        }
//    }
