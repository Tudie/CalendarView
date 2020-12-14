package com.app.calendarview.ver;

import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.calendarview.R;

import java.util.Calendar;


public class InnerViewHolder extends RecyclerView.ViewHolder {


    private View leftView;
    private View rightView;
    private TextView date;
    private View dot;


    Calendar startCalendarDate;
    Calendar endCalendarDate;
    Calendar tempCalendar;
    Calendar todayCalendar;
    DayTimeEntity startDayTime;
    DayTimeEntity endDayTime;
    Boolean pasttime;

    public InnerViewHolder(View itemView, Calendar startCalendarDate, Calendar endCalendarDate, DayTimeEntity startDayTime, DayTimeEntity endDayTime, boolean pasttime) {
        super(itemView);
        leftView = itemView.findViewById(R.id.left_view);
        rightView = itemView.findViewById(R.id.right_view);
        date = itemView.findViewById(R.id.date);
        dot = itemView.findViewById(R.id.dot);
        todayCalendar = Calendar.getInstance();
        tempCalendar = Calendar.getInstance();
        setCalendarZero(todayCalendar);
        setCalendarZero(tempCalendar);
        this.startCalendarDate = startCalendarDate;
        this.endCalendarDate = endCalendarDate;
        this.startDayTime = startDayTime;
        this.endDayTime = endDayTime;
        this.pasttime = pasttime;
    }

    private void setCalendarZero(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    public void doBindData(DayTimeEntity entity) {
        tempCalendar.set(Calendar.YEAR, entity.year);
        tempCalendar.set(Calendar.MONTH, entity.month);
        tempCalendar.set(Calendar.DATE, entity.day);
        if (entity.day == 0) {
            responseDayIsZero(entity);
        } else if ((tempCalendar.getTimeInMillis() >= startCalendarDate.getTimeInMillis())
                && (tempCalendar.getTimeInMillis() <= endCalendarDate.getTimeInMillis())) {
            responseInner(entity);
        } else {
            responseOuter(entity);
        }
    }

    private void responseInner(DayTimeEntity dayTimeEntity) {
        itemView.setEnabled(true);
        if (tempCalendar.getTimeInMillis() == todayCalendar.getTimeInMillis()) {
            setSelectItemBg(dayTimeEntity, true);
        } else {
            setSelectItemBg(dayTimeEntity, false);
        }
    }

    private void responseDayIsZero(DayTimeEntity dayTimeEntity) {
        updateDayIsZeroView();
        boolean flag = (startDayTime.day != 0) && (endDayTime.day != 0);
        boolean value = (startDayTime.year != endDayTime.year)
                || (startDayTime.month != endDayTime.month)
                || (startDayTime.day != endDayTime.day);
        boolean temp = (dayTimeEntity.listPosition > startDayTime.listPosition) && (dayTimeEntity.listPosition < endDayTime.listPosition);
        if (flag && value && temp) {
            int color = ContextCompat.getColor(itemView.getContext(), R.color.text_bg_sel);
            rightView.setBackgroundColor(color);
            leftView.setBackgroundColor(color);
        } else {
            int color = ContextCompat.getColor(itemView.getContext(), R.color.white);
            rightView.setBackgroundColor(color);
            leftView.setBackgroundColor(color);
        }
    }

    private void responseOuter(DayTimeEntity dayTimeEntity) {
        itemView.setEnabled(false);
        int color = ContextCompat.getColor(itemView.getContext(), R.color.text_bg_seled);
        leftView.setBackgroundColor(color);
        rightView.setBackgroundColor(color);
        int textColor = ContextCompat.getColor(itemView.getContext(), R.color.text_color);
        date.setTextColor(textColor);
        date.setBackgroundColor(Color.TRANSPARENT);
        if (tempCalendar.getTimeInMillis() == todayCalendar.getTimeInMillis()) {
            date.setText(Util.fillZero(dayTimeEntity.day));
            dot.setVisibility(View.VISIBLE);
            dot.setBackgroundResource(R.drawable.round_gray);
        } else {
            if (pasttime) {
                if (tempCalendar.getTimeInMillis() < todayCalendar.getTimeInMillis()) {
                    date.setTextColor(Color.parseColor("#cccccc"));
                } else {
                    date.setTextColor(Color.parseColor("#333333"));
                }
            } else {
                date.setTextColor(Color.parseColor("#333333"));
            }

            date.setText(Util.fillZero(dayTimeEntity.day));
            dot.setVisibility(View.GONE);
        }
    }

    private void updateDayIsZeroView() {
        date.setText("");
        dot.setVisibility(View.GONE);
        itemView.setEnabled(false);
        date.setBackgroundColor(Color.TRANSPARENT);
    }

    private void setSelectItemBg(DayTimeEntity entity, boolean isToday) {
        if (startDayTime.day == 0 && endDayTime.day == 0) {
            unselectStartAndEndTIme(entity, isToday);
        } else {
            boolean flag = (startDayTime.year == endDayTime.year)
                    && (startDayTime.month == endDayTime.month)
                    && (startDayTime.day == endDayTime.day);
            if (flag) {
                updateDateBg(entity, startDayTime, isToday);
            } else if (startDayTime.day != 0 && endDayTime.day == 0) {
                updateDateBg(entity, startDayTime, isToday);
            } else if (startDayTime.day == 0 && endDayTime.day != 0) {
                updateDateBg(entity, endDayTime, isToday);
            } else if (startDayTime.day != 0 && endDayTime.day != 0) {
                date.setText(Util.fillZero(entity.day));
                responseToRange(entity, isToday);
            }
        }
    }

    private void responseToRange(DayTimeEntity entity, boolean isToday) {
        if ((startDayTime.listPosition >= 0) && (startDayTime.listPosition == entity.listPosition)) {
            updateDateBg(entity, startDayTime, isToday);
            int color = ContextCompat.getColor(itemView.getContext(), R.color.date_duration_bg);
            rightView.setBackgroundColor(color);
            color = ContextCompat.getColor(itemView.getContext(), R.color.white);
            leftView.setBackgroundColor(color);
        } else if ((startDayTime.listPosition >= 0)
                && (endDayTime.listPosition >= 0)
                && (entity.listPosition > startDayTime.listPosition)
                && (entity.listPosition < endDayTime.listPosition)) {
            updateDateBg(entity, startDayTime, isToday);
            int color = ContextCompat.getColor(itemView.getContext(), R.color.date_duration_bg);
            rightView.setBackgroundColor(color);
            leftView.setBackgroundColor(color);
            date.setBackgroundColor(Color.TRANSPARENT);
        } else if ((endDayTime.listPosition >= 0) && (endDayTime.listPosition == entity.listPosition)) {
            updateDateBg(entity, endDayTime, isToday);
            int color = ContextCompat.getColor(itemView.getContext(), R.color.date_duration_bg);
            leftView.setBackgroundColor(color);
            color = ContextCompat.getColor(itemView.getContext(), R.color.white);
            rightView.setBackgroundColor(color);
        } else {
            updateDateBg(entity, startDayTime, isToday);
        }
    }

    private void unselectStartAndEndTIme(DayTimeEntity entity, boolean isToday) {
        int color;
        if (isToday) {
            date.setTextColor(ContextCompat.getColor(itemView.getContext(), R.color.date_today));
            dot.setVisibility(View.VISIBLE);
            dot.setBackgroundResource(R.drawable.round_today);
        } else {
            if (pasttime) {
                if (tempCalendar.getTimeInMillis() < todayCalendar.getTimeInMillis()) {
                    date.setTextColor(Color.parseColor("#cccccc"));
                } else {
                    date.setTextColor(Color.parseColor("#333333"));
                }
            } else {
                date.setTextColor(Color.parseColor("#333333"));
            }
            dot.setVisibility(View.GONE);
        }
        date.setText(Util.fillZero(entity.day));
        date.setBackgroundColor(Color.TRANSPARENT);
        color = ContextCompat.getColor(itemView.getContext(), R.color.white);
        leftView.setBackgroundColor(color);
        rightView.setBackgroundColor(color);
    }

    private void updateDateBg(DayTimeEntity entity, DayTimeEntity tempTimeEntity, boolean isToday) {
        int color = ContextCompat.getColor(itemView.getContext(), R.color.white);
        leftView.setBackgroundColor(color);
        rightView.setBackgroundColor(color);
        date.setText(Util.fillZero(entity.day));
        boolean flag;
        flag = (tempTimeEntity.year == entity.year) && (tempTimeEntity.month == entity.month) && (tempTimeEntity.day == entity.day);
        if (flag) {
            date.setBackgroundResource(R.drawable.round_select);
            date.setTextColor(Color.WHITE);
            dot.setVisibility(View.GONE);
        } else if (isToday) {
            date.setBackgroundColor(Color.TRANSPARENT);
            color = ContextCompat.getColor(itemView.getContext(), R.color.date_today);
            date.setTextColor(color);
            dot.setVisibility(View.VISIBLE);
        } else {
            if (pasttime) {
                if (tempCalendar.getTimeInMillis() < todayCalendar.getTimeInMillis()) {
                    date.setTextColor(Color.parseColor("#cccccc"));
                } else {
                    date.setTextColor(Color.parseColor("#333333"));
                }
            } else {
                date.setTextColor(Color.parseColor("#333333"));
            }
            date.setBackgroundColor(Color.TRANSPARENT);
            dot.setVisibility(View.GONE);
        }
    }
}
