# CalendarView


1、CalendarViewHorDialog 横向单个或者两个时间选择DIALOG<br />
2、CalendarViewVerDialog 纵向单个或者两个时间选择DIALOG<br />

3、CalendarHorView 横向单个或者两个时间选择自定义View<br />
4、CalendarVerView 纵向单个或者两个时间选择自定义View<br />

mul 是否多选 showstarttime展示从开始位置展示 否则从当前时间展示 pasttime 当前时间之前是否可选<br />

例如：
 1、 CalendarViewHorDialog calendarViewDialog=new CalendarViewHorDialog(MainActivity.this);
     calendarViewDialog.show();<br />
 2、 CalendarViewVerDialog calendarViewVerDialog = new CalendarViewVerDialog(MainActivity.this);
     calendarViewVerDialog.show();<br />
	 
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