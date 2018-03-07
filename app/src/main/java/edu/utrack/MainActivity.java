package edu.utrack;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utrack.calendar.CalendarData;
import edu.utrack.calendar.CalendarEvent;
import edu.utrack.calendar.CalendarHelper;
import edu.utrack.data.app.AppEvent;
import edu.utrack.data.screen.ScreenDataType;
import edu.utrack.monitor.MonitorConnection;
import edu.utrack.monitor.MonitorService;
import edu.utrack.util.AppUtils;

public class MainActivity extends AppCompatActivity {

    private MonitorConnection monitorConnection;
    private CalendarHelper calendarHelper;

    public MainActivity() {
        System.out.println("create activity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("create");

        startService(new Intent(this, MonitorService.class));
        setContentView(R.layout.activity_main);

        monitorConnection = new MonitorConnection(() -> {
            System.out.println("BOUND");
            updateUnlocks();
        });
        calendarHelper = new CalendarHelper(this);
    }

    public void updateUnlocks() {
        if (monitorConnection.isConnected()) {
            Map<ScreenDataType, Integer> map = monitorConnection.getDatabase().getScreenEventsTable().getScreenCounts();
            List<ScreenDataType> sortedTypes = new ArrayList<>(map.keySet());
            Collections.sort(sortedTypes, (t1, t2) -> t1.getFriendlyName().compareTo(t2.getFriendlyName()));

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < sortedTypes.size(); i++) {
                if (i != 0) builder.append(", ");
                builder.append(sortedTypes.get(i).getFriendlyName()).append(": ").append(map.get(sortedTypes.get(i)));
            }
            Cursor cursor = monitorConnection.getDatabase().getWritableDatabase().rawQuery("SELECT `name` FROM `sqlite_master`", null);
            while(cursor.moveToNext()) {
                System.out.println(cursor.getString(0));
            }

            monitorConnection.getDatabase().getScreenEventsTable().getAllData();
            ((TextView) findViewById(R.id.dataText)).setText(builder.toString());

            List<AppEvent> events = monitorConnection.getDatabase().getAppEventsTable().getAllData();
            Map<String, String> names = new HashMap<>();
            for(int i = 0; i < events.size(); i++) {
                AppEvent e = events.get(i);
                String packageName = e.getApp().getPackageName();
                String name = names.get(packageName);
                if(name == null) {
                    name = AppUtils.getAppName(packageName, this);
                    names.put(packageName, name);
                }
                SimpleDateFormat startFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
                SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");

                System.out.println((i + 1) + ". " + name + " - " + startFormat.format(new Date(e.getStartTime())) + " for " + timeFormat.format(new Date(e.getDuration())));
            }
        }
    }

    public void clearButtonClick(View view) {
        monitorConnection.getDatabase().getScreenEventsTable().clearTable();
        updateUnlocks();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, MonitorService.class), monitorConnection, Context.BIND_ABOVE_CLIENT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUnlocks();
        calendarHelper.requestCalendars((calendars) -> {
            CalendarData myTimetable = calendarHelper.getMyTimetableData(calendars);
            if(myTimetable != null) {
                System.out.println("Mytimetable found!");
                calendarHelper.requestEvents(myTimetable, (events) -> {
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM HH:mm");

                    for(int i = 0; i < events.size(); i++) {
                        CalendarEvent event = events.get(i);
                        System.out.println((i + 1) + ". " + event.getTitle() + "(" + event.getLocation() + "): " + format.format(new Date(event.getStartTime())) + " - " + format.format(new Date(event.getEndTime())));
                    }
                });
            }
            else {
                System.out.println("Need to select timetable:");
                for(CalendarData d : calendars) {
                    System.out.println(d.getAccountName() + ": " + d.getName());
                }
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        calendarHelper.onPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Stop!");
        unbindService(monitorConnection);
    }
}
