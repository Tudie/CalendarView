package com.app.calendarview;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {
    CalendarViewHorDialog calendarViewDialog;
    CalendarViewVerDialog calendarViewVerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        findViewById(R.id.show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarViewHorDialog calendarViewDialog = new CalendarViewHorDialog(MainActivity.this);
                calendarViewDialog.show();

            }
        });
        findViewById(R.id.showver).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CalendarViewVerDialog calendarViewVerDialog = new CalendarViewVerDialog(MainActivity.this);
                calendarViewVerDialog.show();

            }
        });
    }
}