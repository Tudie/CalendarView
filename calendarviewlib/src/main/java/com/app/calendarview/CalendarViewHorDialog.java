package com.app.calendarview;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.app.calendarview.ver.CalendarCallback;

import java.util.Date;

public class CalendarViewHorDialog extends Dialog {
    private CalendarHorView dc_calendar;
    private Context context;

    public CalendarViewHorDialog(@NonNull Context context) {
        super(context,R.style.CalendarDialog);
        this.context=context;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_calendarhor);
        dc_calendar=this.findViewById(R.id.dc_calendar);
        WindowManager.LayoutParams lp = this.getWindow().getAttributes();
        lp.width = WindowManager.LayoutParams.MATCH_PARENT;
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT;
        this.getWindow().setAttributes(lp);
        this.getWindow().getAttributes().gravity = Gravity.BOTTOM;

        initData();

    }

    private void initData(){
        dc_calendar.setCalendarCallback(new CalendarCallback() {
            @Override
            public void callbackdata(Date startdata, Date enddata) {
                if (startdata==null){
                    dismiss();
                    return;
                }
                Toast.makeText(context, startdata + "///" + enddata, Toast.LENGTH_SHORT).show();

            }
        });
    }
}
