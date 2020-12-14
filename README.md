# CalendarView


1、CalendarViewHorDialog 横向单个或者两个时间选择DIALOG
2、CalendarViewVerDialog 纵向单个或者两个时间选择DIALOG

3、CalendarHorView 横向单个或者两个时间选择自定义View
4、CalendarVerView 纵向单个或者两个时间选择自定义View

mul 是否多选 showstarttime展示从开始位置展示 否则从当前时间展示 pasttime 当前时间之前是否可选

例如：
 1、 CalendarViewHorDialog calendarViewDialog=new CalendarViewHorDialog(MainActivity.this);
     calendarViewDialog.show();
 2、 CalendarViewVerDialog calendarViewVerDialog = new CalendarViewVerDialog(MainActivity.this);
     calendarViewVerDialog.show();
	 
	 3、
	   <com.app.calendarview.CalendarVerView
        android:id="@+id/dc_calendar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        app:mul="true"
        app:showstarttime="false"
        app:pasttime="true" />
	4、
	  <com.app.calendarview.CalendarHorView
        android:id="@+id/dc_calendar"
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:background="@android:color/white"
        app:mul="false"
        app:showstarttime="true"
        app:year="30"
        android:paddingBottom="10dp"
        app:pasttime="false" />