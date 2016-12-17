package com.example.fredrik.barometer;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by fredrik on 2016-12-16.
 */

public class ShowDataHistory extends Activity {

    public static String RESULT ="";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub);
        Button but = (Button)findViewById(R.id.buttonExit);
        TextView tv = (TextView)findViewById(R.id.textViewSub);
        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText(RESULT);

    }

    public void onKlick(View view) {
        super.onBackPressed();
    }


}
