package com.app.calendarview.hor;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

import com.app.calendarview.R;


/**
 * Created by Danfeng on 2018/5/30.
 */

public class CalendarDayRelativeLayout extends RelativeLayout {
    public CalendarDayRelativeLayout(Context context) {
        this(context, null);
    }

    public CalendarDayRelativeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void isDurationSat(boolean isnotm) {
        if (isnotm){
            this.setBackground(getResources().getDrawable(R.drawable.shape_endtime));
        }else {
            this.setBackgroundColor(Color.parseColor("#ffffff"));
        }
    }

    public void isDurationSun(boolean isnotm) {
        if (isnotm){
            this.setBackground(getResources().getDrawable(R.drawable.shape_starttime));
        }else {
            this.setBackgroundColor(Color.parseColor("#ffffff"));
        }

    }

    public void isETime(boolean etime) {
        this.setBackground(getResources().getDrawable(R.drawable.shape_calendars_bg));
    }

    public void isSTime(boolean stime) {
        this.setBackground(getResources().getDrawable(R.drawable.shape_calendare_bg));
    }
}
