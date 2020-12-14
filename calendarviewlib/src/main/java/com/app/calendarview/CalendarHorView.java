package com.app.calendarview;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.app.calendarview.hor.CalendarDayRelativeLayout;
import com.app.calendarview.hor.CalendarDayTextView;
import com.app.calendarview.hor.DateUtil;
import com.app.calendarview.ver.CalendarCallback;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


public class CalendarHorView extends LinearLayout {
    private TextView title;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;

    private Calendar curDate = Calendar.getInstance();
    //从服务器获取的日期
    private Date dateFromServer;

    //外层主recyclerview的adapter
    private MainRvAdapter mainAdapter;
    private List<CalendarCell> months = new ArrayList<>();
    private Context context;

    //相关属性
    private TypedArray typedArray;
    private int titleColor;
    private int titleSize;

    private int textcolor;
    private int textg;
    private int todayColor;
    private int maxSelect = 12 * 20;
    private int index = 0;
    private int year;
    private boolean mul, pasttime,showstarttime;

    private List<String> titles = new ArrayList<>();

    //点击的开始时间与结束时间
    public Date sDateTime;
    public Date eDateTime;
    private boolean isSelectingSTime = true;

    private CalendarCallback calendarCallback;

    private HashMap<Integer, SubRvAdapter> allAdapters = new HashMap<>();

    public CalendarHorView(Context context) {
        this(context, null);
    }

    public CalendarHorView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }


    public CalendarHorView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarView);

        titleColor = typedArray.getColor(R.styleable.CalendarView_titleColor, getResources().getColor(R.color.title_color));
        titleSize = (int) typedArray.getDimension(R.styleable.CalendarView_titleSize, 18);

        textcolor = typedArray.getColor(R.styleable.CalendarView_textColor, Color.parseColor("#484848"));
        textg = typedArray.getColor(R.styleable.CalendarView_textgColor, Color.parseColor("#cccccc"));

        todayColor = typedArray.getColor(R.styleable.CalendarView_todayColor, Color.BLUE);

        showstarttime = typedArray.getBoolean(R.styleable.CalendarView_showstarttime, false);
        pasttime = typedArray.getBoolean(R.styleable.CalendarView_pasttime, false);
        mul = typedArray.getBoolean(R.styleable.CalendarView_mul, false);

        year = typedArray.getInteger(R.styleable.CalendarView_year, 20);
        typedArray.recycle();
        this.context = context;
        initTime(context, "");
        init(context);
    }

    //该方法用于设置从服务器获取的时间，如果没有从服务器获取的时间将使用手机本地时间
    private void initTime(Context context, String time) {
        if (!time.equals("")) {
            curDate = DateUtil.strToCalendar(time, "yyyy-MM-dd");
            curDate.add(Calendar.YEAR, -year);
            curDate.set(Calendar.DATE, 1);
            dateFromServer = DateUtil.strToDate(time, "yyyy-MM-dd");
        } else {
            curDate = Calendar.getInstance();
            curDate.add(Calendar.YEAR, -year);
            curDate.set(Calendar.DATE, 1);
            dateFromServer = new Date();
        }
        maxSelect = year * 12 + 20 * 12;
    }

    private void init(Context context) {
        bindView(context);
        bindEvent();
    }


    private void bindView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_calendarhorview, this, false);
        view.findViewById(R.id.calendar_title_ll).setVisibility(VISIBLE);
        title = (TextView) view.findViewById(R.id.calendar_title);
        title.setTextColor(titleColor);
        title.setTextSize(titleSize);
        String surestr = typedArray.getString(R.styleable.CalendarView_suretext);
        String cancelstr = typedArray.getString(R.styleable.CalendarView_canceltext);
        if (surestr != null)
            ((TextView) view.findViewById(R.id.calendar_cancel)).setText(cancelstr);
        if (cancelstr != null)
            ((TextView) view.findViewById(R.id.calendar_sure)).setText(surestr);
        ((TextView) view.findViewById(R.id.calendar_sure)).setTextColor(typedArray.getColor(R.styleable.CalendarView_sureColor, Color.parseColor("#1298FF")));
        ((TextView) view.findViewById(R.id.calendar_cancel)).setTextColor(typedArray.getColor(R.styleable.CalendarView_cancelColor, Color.parseColor("#666666")));
        recyclerView = (RecyclerView) view.findViewById(R.id.calendar_rv);
        linearLayoutManager = new LinearLayoutManager(this.context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(linearLayoutManager);
        PagerSnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(recyclerView);
        view.findViewById(R.id.calendar_back_iv).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                index--;
                recyclerView.smoothScrollToPosition(index);
            }
        });
        view.findViewById(R.id.calendar_next_iv).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                index++;
                recyclerView.smoothScrollToPosition(index);
            }
        });
        view.findViewById(R.id.calendar_cancel).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarCallback != null)
                    calendarCallback.callbackdata(null, null);
            }
        });
        view.findViewById(R.id.calendar_sure).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (calendarCallback != null) {
                    if (mul) {
                        if (sDateTime != null && eDateTime != null)
                            calendarCallback.callbackdata(sDateTime, eDateTime);
                    } else {
                        if (sDateTime != null)
                            calendarCallback.callbackdata(sDateTime, sDateTime);
                    }
                }

            }
        });
        addView(view);
    }

    private void bindEvent() {
        renderCalendar("");
    }

    public void renderCalendar(String time) {
        months.clear();
        initTime(context, time);
        for (int i = 0; i < maxSelect; i++) {
            ArrayList<Date> cells = new ArrayList<>();
            if (i != 0) {
                curDate.add(Calendar.MONTH, 1);//后推一个月
            } else {
                curDate.add(Calendar.MONTH, 0);//当前月
            }
            Calendar calendar = (Calendar) curDate.clone();
            //将日历设置到当月第一天
            calendar.set(Calendar.DAY_OF_MONTH, 1);
            //获得当月第一天是星期几，如果是星期一则返回1此时1-1=0证明上个月没有多余天数
            int prevDays = calendar.get(Calendar.DAY_OF_WEEK) - 1;
            //将calendar在1号的基础上向前推prevdays天。
            calendar.add(Calendar.DAY_OF_MONTH, -prevDays);
            //最大行数是6*7也就是，1号正好是星期六时的情况
            int maxCellcount = 6 * 7;
            while (cells.size() < maxCellcount) {
                cells.add(calendar.getTime());
                //日期后移一天
                calendar.add(calendar.DAY_OF_MONTH, 1);
            }
            if (!showstarttime){
                if (cells.size() > 15 && matchSameMonth(cells.get(15), dateFromServer, 2)) {
                    recyclerView.getLayoutManager().scrollToPosition(i);
                    index = i;
                }
            }

            months.add(new CalendarCell(months.size(), cells));
        }

        for (int i = 0; i < months.size(); i++) {
            //title格式
            String title = (months.get(i).getCells().get(20).getYear() + 1900) +
                    "年" +
                    (months.get(i).getCells().get(20).getMonth() + 1) + "月";
            titles.add(title);
        }
        title.setText(titles.get(index));
        //只限定3个月，因此模拟给3个数值即可
        mainAdapter = new MainRvAdapter();
        recyclerView.setAdapter(mainAdapter);

        //recyclerview 的滚动监听
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                index = linearLayoutManager.findLastVisibleItemPosition();
                title.setText(titles.get(index));
                super.onScrollStateChanged(recyclerView, newState);
            }
        });
    }

    /**
     * 最外层水平recyclerview的adapter
     */
    private class MainRvAdapter extends RecyclerView.Adapter<MainRvAdapter.NormalTextViewHolder> {
        private LayoutInflater mInflater;

        public MainRvAdapter() {
            this.mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public NormalTextViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new NormalTextViewHolder(mInflater.inflate(R.layout.layout_calendarhorview_item, parent, false));
        }

        @Override
        public void onBindViewHolder(@NonNull NormalTextViewHolder holder, int position) {
            if (holder.appoint_calendarview_item_rv.getLayoutManager() == null) {
                //RecyclerView不能都使用同一个LayoutManager
                GridLayoutManager manager = new GridLayoutManager(context, 7);
                //recyclerview嵌套高度不固定（wrap_content）时必须setAutoMeasureEnabled(true)，否则测量时控件高度为0
                manager.setAutoMeasureEnabled(true);
                holder.appoint_calendarview_item_rv.setLayoutManager(manager);
            }
            final CalendarCell item = months.get(position);
            SubRvAdapter subRvAdapter = null;
            if (allAdapters.get(position) == null) {
                subRvAdapter = new SubRvAdapter(item.getCells(), new CalendarOnClickListener() {
                    @Override
                    public void OnClick(Date date, int index) {
                        int day = date.getDay();
                        if (!mul) {
                            if (pasttime) {
                                if (timeCompare(dateFromServer, date) >= 2) {
                                    selectSDate(item.getCells().get(index));
                                }
                            } else {
                                selectSDate(item.getCells().get(index));
                            }

                        } else {
                            if (pasttime) {
                                if (timeCompare(dateFromServer, date) >= 2) {
                                    //可点击数据
                                    if (isSelectingSTime) {
                                        //正在选择开始时间
                                        selectSDate(item.getCells().get(index));
                                    } else {
                                        //正在选择结束时间
                                        selectEDate(item.getCells().get(index));
                                    }
                                }
                            } else {
                                //可点击数据
                                if (isSelectingSTime) {
                                    //正在选择开始时间
                                    selectSDate(item.getCells().get(index));
                                } else {
                                    //正在选择结束时间
                                    selectEDate(item.getCells().get(index));
                                }
                            }

                        }
                        Iterator iterator = allAdapters.entrySet().iterator();
                        while (iterator.hasNext()) {
                            Map.Entry entry = (Map.Entry) iterator.next();
                            ((SubRvAdapter) entry.getValue()).notifyDataSetChanged();
                        }
                    }
                });
                allAdapters.put(position, subRvAdapter);
                holder.appoint_calendarview_item_rv.setAdapter(subRvAdapter);
            } else {
                subRvAdapter = allAdapters.get(position);
                holder.appoint_calendarview_item_rv.setAdapter(subRvAdapter);
            }

        }

        @Override
        public int getItemCount() {
            return months.size();
        }

        public class NormalTextViewHolder extends RecyclerView.ViewHolder {
            RecyclerView appoint_calendarview_item_rv;

            NormalTextViewHolder(View view) {
                super(view);
                appoint_calendarview_item_rv = (RecyclerView) view.findViewById(R.id.appoint_calendarview_item_rv);

            }
        }
    }

    public void selectSDate(Date date) {
        if (sDateTime != null && eDateTime != null) {
            sDateTime = date;

        } else {
            sDateTime = date;
        }
        eDateTime = null;
        isSelectingSTime = false;
    }

    public void selectEDate(Date date) {
        if (sDateTime != null) {
            if (date.getTime() > sDateTime.getTime()) {
                eDateTime = date;
                isSelectingSTime = true;
            }
        }

    }

    public int timeCompare(Date date1, Date date2) {
        int i = 0;
        try {
            // 1 结束时间小于开始时间 2 开始时间与结束时间相同 3 结束时间大于开始时间
            if (date2.getTime() < date1.getTime()) {
                //结束时间小于开始时间
                i = 1;
            } else if (date2.getTime() == date1.getTime()) {
                //开始时间与结束时间相同
                i = 2;
            } else if (date2.getTime() > date1.getTime()) {
                //结束时间大于开始时间
                i = 3;
            }
            if (i < 2) {
                if (matchSameMonth(date2, date1, 1)) {
                    i = 2;
                }
            }
        } catch (Exception e) {

        }
        return i;
    }


    private class SubRvAdapter extends RecyclerView.Adapter<SubRvAdapter.BaseViewHolder> {
        private ArrayList<Date> cells;
        private LayoutInflater mInflater;
        private CalendarOnClickListener onClickListener;

        public SubRvAdapter(ArrayList<Date> cells, CalendarOnClickListener onClickListener) {
            this.cells = cells;
            this.onClickListener = onClickListener;
            this.mInflater = LayoutInflater.from(context);
        }

        @NonNull
        @Override
        public BaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new BaseViewHolder(mInflater.inflate(R.layout.layout_calendarhor_day, parent, false));
        }

        @Override
        public int getItemCount() {
            return cells.size();
        }

        @Override
        public void onBindViewHolder(@NonNull BaseViewHolder holder, int position) {
            holder.setIsRecyclable(false);//不让recyclerview进行复用，复用会出问题
            final Date date = cells.get(position);
            int day = date.getDate();
            holder.calendar_day_v.setVisibility(INVISIBLE);
            //设置文本
            holder.calendar_day_tv.setText(String.valueOf(day));
            //设置颜色
            if (timeCompare(dateFromServer, date) >= 2) {
                //可选时间
                holder.calendar_day_tv.setTextColor(textcolor);
            } else {
                //不可选时间
                if (!pasttime) {
                    holder.calendar_day_tv.setTextColor(textcolor);
                } else {
                    holder.calendar_day_tv.setTextColor(textg);
                }

            }
            if (eDateTime != null && date.getTime() == eDateTime.getTime()) {
                //结束时间
                holder.calendar_day_tv.isETime(true);
                holder.calendar_day_rl.isETime(true);
            }
            if (sDateTime != null && date.getTime() == sDateTime.getTime()) {
                //开始时间
                if (eDateTime != null) {
                    holder.calendar_day_tv.isSTime(true);
                    holder.calendar_day_rl.isSTime(true);
                } else {
                    holder.calendar_day_tv.isSTime(true);
                }
            }
            if (sDateTime != null && eDateTime != null && date.getTime() > sDateTime.getTime() && date.getTime() < eDateTime.getTime()) {
                if (date.getDay() == 6) {
                    holder.calendar_day_rl.isDurationSat(true);
                } else if (date.getDay() == 0) {
                    holder.calendar_day_rl.isDurationSun(true);
                } else {
                    holder.calendar_day_rl.setBackgroundColor(getResources().getColor(R.color.date_duration_bg));
                }
            }

            if (matchSameMonth(date, dateFromServer, 1)) {
                if (!(eDateTime != null && date.getTime() == eDateTime.getTime()) && !(sDateTime != null && date.getTime() == sDateTime.getTime())) {
                    holder.calendar_day_tv.setToday(true);
                    holder.calendar_day_v.setVisibility(VISIBLE);
                }
            }
            final int index = position;
            holder.calendar_day_rl.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickListener.OnClick(date, index);
                }
            });
        }


        public class BaseViewHolder extends RecyclerView.ViewHolder {
            CalendarDayRelativeLayout calendar_day_rl;
            CalendarDayTextView calendar_day_tv;
            View calendar_day_v;

            BaseViewHolder(View view) {
                super(view);
                calendar_day_rl = (CalendarDayRelativeLayout) view.findViewById(R.id.calendar_day_rl);
                calendar_day_tv = (CalendarDayTextView) view.findViewById(R.id.calendar_day_tv);
                calendar_day_v = (View) view.findViewById(R.id.calendar_day_v);

            }
        }
    }

    public static boolean matchSameMonth(Date date, Date date2, Integer state) {
        if (null == state) {
            return false;
        } else if (1 == state) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
            String str1 = simpleDateFormat.format(date);
            String str2 = simpleDateFormat.format(date2);
            if (str1.equals(str2)) {
                return true;
            }

        } else if (2 == state) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM");
            String str1 = simpleDateFormat.format(date);
            String str2 = simpleDateFormat.format(date2);
            if (str1.equals(str2)) {
                return true;
            }

        } else if (3 == state) {
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
            String str1 = simpleDateFormat.format(date);
            String str2 = simpleDateFormat.format(date2);
            if (str1.equals(str2)) {
                return true;
            }

        }
        return false;
    }


    private class CalendarCell {
        private int position;
        ArrayList<Date> cells;

        public CalendarCell(int position, ArrayList<Date> cells) {
            this.position = position;
            this.cells = cells;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public ArrayList<Date> getCells() {
            return cells;
        }

        public void setCells(ArrayList<Date> cells) {
            this.cells = cells;
        }
    }


    public interface CalendarOnClickListener {
        void OnClick(Date date, int index);
    }

    public void setCalendarCallback(CalendarCallback calendarCallback) {
        this.calendarCallback = calendarCallback;
    }
}
