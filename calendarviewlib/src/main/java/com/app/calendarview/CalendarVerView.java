package com.app.calendarview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.app.calendarview.ver.CalendarCallback;
import com.app.calendarview.ver.CalendarSelectUpdateCallback;
import com.app.calendarview.ver.DayTimeEntity;
import com.app.calendarview.ver.MonthTimeEntity;
import com.app.calendarview.ver.OuterRecycleAdapter;
import com.app.calendarview.ver.Util;

import java.util.Calendar;
import java.util.Map;

public class CalendarVerView extends LinearLayout {

    private Context context;

    private RecyclerView recyclerView;
    private OuterRecycleAdapter outAdapter;

    private TypedArray typedArray;
    private boolean showstarttime;
    private boolean pasttime;
    private boolean mul;

    Calendar startCalendar;
    Calendar endCalendar;
    Calendar startCalendarDate;
    Calendar endCalendarDate;
    DayTimeEntity startDayTime;
    DayTimeEntity endDayTime;
    GridLayoutManager layoutManager;
    private CalendarCallback calendarCallback;
    private CalendarSelectUpdateCallback multCallback = new CalendarSelectUpdateCallback() {
        @Override
        public void updateMultView() {
            CalendarVerView.this.updateMultView();
        }

        @Override
        public void refreshLocate(int position) {
            try {
                if (layoutManager != null) {
                    layoutManager.scrollToPositionWithOffset(position, 0);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    };

    public CalendarVerView(Context context) {
        this(context, null);
    }

    public CalendarVerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CalendarVerView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context=context;
        initCalendar();
        initAttrs(attrs);
        initView(context);
        initAdapter();
        addListener();
    }

    private void initCalendar() {
        Calendar calendar = Calendar.getInstance();
        startDayTime = new DayTimeEntity(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 0, -1, -1);
        endDayTime = new DayTimeEntity(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), 0, -1, -1);
        startCalendarDate = Calendar.getInstance();
        startCalendarDate.add(Calendar.YEAR, -70);
        startCalendarDate.set(Calendar.DATE, 1);
        endCalendarDate = Calendar.getInstance();
        endCalendarDate.add(Calendar.YEAR, +30);
        endCalendarDate.set(Calendar.DATE, 1);
        endCalendarDate.add(Calendar.MONTH, 12);
        endCalendarDate.set(Calendar.DATE, 1);
        endCalendarDate.add(Calendar.MONTH, 1);
        endCalendarDate.add(Calendar.DATE, -1);
        startCalendar = Calendar.getInstance();
        endCalendar = Calendar.getInstance();
        initStartEndCalendar();
    }

    private void initStartEndCalendar() {
        startCalendar.setTimeInMillis(startCalendarDate.getTimeInMillis());
        endCalendar.setTimeInMillis(endCalendarDate.getTimeInMillis());

        startCalendarDate.set(Calendar.HOUR_OF_DAY, 0);
        endCalendarDate.set(Calendar.HOUR_OF_DAY, 0);
        startCalendarDate.set(Calendar.MINUTE, 0);
        endCalendarDate.set(Calendar.MINUTE, 0);
        startCalendarDate.set(Calendar.SECOND, 0);
        endCalendarDate.set(Calendar.SECOND, 0);
        startCalendarDate.set(Calendar.MILLISECOND, 0);
        endCalendarDate.set(Calendar.MILLISECOND, 0);
    }

    private void initView(Context context) {
        this.context = context;
        setOrientation(LinearLayout.VERTICAL);
        LayoutInflater.from(context).inflate(R.layout.layout_view_calendarver_select, this, true);
        int color = getResources().getColor(R.color.text_color);
        ((TextView) findViewById(R.id.calendar_title)).setTextColor(typedArray.getColor(R.styleable.CalendarView_titleColor, getResources().getColor(R.color.title_color)));
        ((TextView) findViewById(R.id.calendar_title)).setTextSize((int) typedArray.getDimension(R.styleable.CalendarView_titleSize, 18));
        String surestr = typedArray.getString(R.styleable.CalendarView_suretext);
        String cancelstr = typedArray.getString(R.styleable.CalendarView_canceltext);
        setBackgroundColor(color);
        recyclerView = findViewById(R.id.recycleView);
        findViewById(R.id.calendar_title_ll).setVisibility(INVISIBLE);
        if (surestr != null)
            ((TextView)findViewById(R.id.calendar_cancel)).setText(cancelstr);
        if (cancelstr != null)
            ((TextView) findViewById(R.id.calendar_sure)).setText(surestr);
        ((TextView) findViewById(R.id.calendar_sure)).setTextColor(typedArray.getColor(R.styleable.CalendarView_sureColor, Color.parseColor("#1298FF")));
        ((TextView) findViewById(R.id.calendar_cancel)).setTextColor(typedArray.getColor(R.styleable.CalendarView_cancelColor, Color.parseColor("#666666")));

    }

    private void initAdapter() {


        layoutManager =
                new GridLayoutManager(getContext(),
                        7,
                        GridLayoutManager.VERTICAL,
                        false
                );
        layoutManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
            @Override
            public int getSpanSize(int position) {
                if (outAdapter != null && outAdapter.getMap() != null) {
                    Map<Integer, MonthTimeEntity> map = outAdapter.getMap();
                    if (map.containsKey(position))
                        return 7;
                    else
                        return 1;
                } else {
                    return 1;
                }
            }
        });
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new SpaceItemDecoration());
//        typedArray.getColor(R.styleable.CalendarView_textgColor, Color.parseColor("#cccccc"));
        outAdapter = new OuterRecycleAdapter(Util.getTotalCount(startCalendar, endCalendar), mul,
                startCalendarDate, endCalendarDate,
                startDayTime, endDayTime,pasttime);
        outAdapter.setUpdateMultCallback(multCallback);
        recyclerView.setAdapter(outAdapter);
        outAdapter.scrollToPosition();
        startDayTime.day = 0;
        endDayTime.day = 0;
        startDayTime.listPosition = -1;
        startDayTime.monthPosition = -1;
        endDayTime.listPosition = -1;
        endDayTime.monthPosition = -1;
        if (outAdapter != null)
            outAdapter.notifyDataSetChanged();
    }

    private void initAttrs(AttributeSet attrs) {
        if (attrs != null) {
            typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);
            mul = typedArray.getBoolean(R.styleable.CalendarView_mul, false);
            updateViewVisibility();
            pasttime = typedArray.getBoolean(R.styleable.CalendarView_pasttime, false);
            showstarttime = typedArray.getBoolean(R.styleable.CalendarView_showstarttime, false);
            updateDayTimeEntity();
            typedArray.recycle();
        }
    }

    private void updateDayTimeEntity() {
        if (!showstarttime) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            if (calendar.getTimeInMillis() <= endCalendarDate.getTimeInMillis()) {
                endDayTime.year = calendar.get(Calendar.YEAR);
                endDayTime.month = calendar.get(Calendar.MONTH);
                endDayTime.day = calendar.get(Calendar.DAY_OF_MONTH);
            } else {
                endDayTime.year = endCalendarDate.get(Calendar.YEAR);
                endDayTime.month = endCalendarDate.get(Calendar.MONTH);
                endDayTime.day = endCalendarDate.get(Calendar.DAY_OF_MONTH);
            }
        } else {
            endDayTime.year = startCalendarDate.get(Calendar.YEAR);
            endDayTime.month = startCalendarDate.get(Calendar.MONTH);
            endDayTime.day = startCalendarDate.get(Calendar.DAY_OF_MONTH);
        }

        if (endDayTime.day != 0 && (!mul)) {
            startDayTime.year = endDayTime.year;
            startDayTime.month = endDayTime.month;
            startDayTime.day = endDayTime.day;
        }

        updateMultView();
    }

    private void updateMultView() {
        if (mul) {
            if (endDayTime.day != 0) {
                Calendar calendar = Calendar.getInstance();
                int year = calendar.get(Calendar.YEAR);
                int month = calendar.get(Calendar.MONTH);
                int day = calendar.get(Calendar.DATE);
                String value = endDayTime.year + "-" + Util.fillZero(endDayTime.month + 1) + "-" + Util.fillZero(endDayTime.day);
                if ((year == endDayTime.year) && (month == endDayTime.month) && (day == endDayTime.day)) {
                    int color = ContextCompat.getColor(getContext(), R.color.text_colored);
//                    Util.setKeywords(value + "", rightTime, "", color);

                } else {
                }
            } else {
            }
        }
    }

    public void setistitleTimeShow(boolean type) {
        updateViewVisibility();
    }

    public void setCalendarRange(Calendar startCalendar, Calendar endCalendar, DayTimeEntity startDayTime, DayTimeEntity endDayTime) {
        if (startCalendar == null || endCalendar == null)
            throw new IllegalStateException("传入的日历是不能为空的");
        else if (endCalendar.getTimeInMillis() < startCalendar.getTimeInMillis())
            throw new IllegalStateException("结束日期不能早于开始日期");
        else {
            this.startCalendarDate.setTimeInMillis(startCalendar.getTimeInMillis());
            this.endCalendarDate.setTimeInMillis(endCalendar.getTimeInMillis());
            initStartEndCalendar();
            setStartEndTime(startDayTime, endDayTime);
            if (outAdapter != null)
                outAdapter.setData(Util.getTotalCount(startCalendar, endCalendar));
        }
    }

    public void setStartEndTime(DayTimeEntity startDayTime, DayTimeEntity endDayTime) {
        if (startDayTime == null || endDayTime == null) {
            updateDayTimeEntity();
        } else {
            if (startDayTime != null) {
                this.startDayTime.day = startDayTime.day;
                this.startDayTime.year = startDayTime.year;
                this.startDayTime.monthPosition = -1;
                this.startDayTime.month = startDayTime.month;
                this.startDayTime.listPosition = -1;
            }

            if (endDayTime != null) {
                this.endDayTime.day = endDayTime.day;
                this.endDayTime.year = endDayTime.year;
                this.endDayTime.monthPosition = -1;
                this.endDayTime.month = endDayTime.month;
                this.endDayTime.listPosition = -1;
            }
        }

        updateMultView();
        if (outAdapter != null) {
            outAdapter.notifyDataSetChanged();
            outAdapter.scrollToLocation();
        }
    }

    private void updateViewVisibility() {
    }

    private void addListener() {

        findViewById(R.id.calendar_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarCallback != null) {
                    calendarCallback.callbackdata(null, null);
                }
            }
        });

        findViewById(R.id.calendar_sure).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarCallback != null) {
                    if (!mul) {
                        if (!("00".equals(startDayTime.getStartTime().substring(startDayTime.getStartTime().length() - 2)))) {
                            calendarCallback.callbackdata(Util.stringToDate(startDayTime.getStartTime()), Util.stringToDate(startDayTime.getStartTime()));
                        }
                    } else {

                        if (!("00".equals(startDayTime.getStartTime().substring(startDayTime.getStartTime().length() - 2))) && !("00".equals(endDayTime.getEndTime().substring(endDayTime.getEndTime().length() - 2)))) {
                            calendarCallback.callbackdata(Util.stringToDate(startDayTime.getStartTime()), Util.stringToDate(endDayTime.getEndTime()));
                        }
                    }

                }
            }
        });
    }

    public void setCalendarCallback(CalendarCallback calendarCallback) {
        this.calendarCallback = calendarCallback;
    }

    class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        public SpaceItemDecoration() {

        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = 10;
            int position = parent.getChildLayoutPosition(view);
            if (outAdapter != null && outAdapter.getMap() != null) {
                Map<Integer, MonthTimeEntity> map = outAdapter.getMap();
                if (map.containsKey(position))
                    outRect.top = 20;
                else
                    outRect.top = 0;
            } else {
                outRect.top = 0;
            }
        }
    }
}
