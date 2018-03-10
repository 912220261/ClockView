package com.frank.myviewdemo;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.frank.myviewdemo.view.ClockView;

public class MainActivity extends AppCompatActivity {
    private ClockView clockView;
    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        textView = findViewById(R.id.text);
        clockView = findViewById(R.id.clock_view);
        clockView.setClockTouchListener(new ClockView.ClockTouchListener() {
            @Override
            public void getClockText(String num) {
                textView.setText(num);
            }
        });
    }
}
