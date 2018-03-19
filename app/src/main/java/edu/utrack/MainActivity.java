package edu.utrack;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utrack.calendar.CalendarTracker;
import edu.utrack.data.calendar.CalendarData;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.calendar.CalendarHelper;
import edu.utrack.data.app.AppEvent;
import edu.utrack.data.screen.ScreenEventType;
import edu.utrack.database.table.Table;
import edu.utrack.monitor.MonitorConnection;
import edu.utrack.monitor.MonitorService;
import edu.utrack.settings.AppSettings;
import edu.utrack.util.AppUtils;

public class MainActivity extends AppCompatActivity {

    private MonitorConnection monitorConnection;
    private CalendarHelper calendarHelper;
    private List<CalendarEvent> eventsToUpdate = new ArrayList<>();

    public static AppSettings settings;

    public MainActivity() {
        System.out.println("create activity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(settings == null) {
            settings = new AppSettings(new File(getFilesDir(), "settings.json"));
            try {
                settings.load();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        calendarHelper = new CalendarHelper(this);

        startService(new Intent(this, MonitorService.class));
        setContentView(R.layout.activity_main);

        monitorConnection = new MonitorConnection(() -> {
            System.out.println("BOUND");
            updateUnlocks();
            if(!eventsToUpdate.isEmpty()) {
                monitorConnection.getService().getCalendarTracker().updateEventsList(eventsToUpdate);
                eventsToUpdate.clear();
            }
        });

        ((Button) findViewById(R.id.updateButton)).setOnClickListener((e) -> {
            if(monitorConnection.isConnected()) updateUnlocks();
        });
        ((Button) findViewById(R.id.clearButton)).setOnClickListener(this::clearButtonClick);
    }

    public void updateUnlocks() {
        System.out.println("UPDATE:");
        System.out.println(monitorConnection.isConnected());
        if (monitorConnection.isConnected()) {
            List<CalendarEvent> currentEvents = monitorConnection.getService().getCalendarTracker().getCurrentEvents();

            if(!currentEvents.isEmpty()) {
                CalendarEvent event = currentEvents.get(0);

                StringBuilder builder = new StringBuilder();
                builder.append(event.getTitle() + ": " + event.getLocation() + "\n");

                Map<ScreenEventType, Integer> events = monitorConnection.getDatabase().getScreenEventsTable().getScreenCounts(event);
                List<ScreenEventType> sortedTypes = new ArrayList<>(events.keySet());
                Collections.sort(sortedTypes, (t1, t2) -> t1.getFriendlyName().compareTo(t2.getFriendlyName()));

                for (int i = 0; i < sortedTypes.size(); i++) {
                    if (i != 0) builder.append(", ");
                    builder.append(sortedTypes.get(i).getFriendlyName()).append(": ").append(events.get(sortedTypes.get(i)));
                }
                builder.append("\n\nApps:\n");

                List<AppEvent> appEvents = monitorConnection.getDatabase().getAppEventsTable().getData(event);
                Map<String, String> names = new HashMap<>();

                SimpleDateFormat startFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
                SimpleDateFormat timeFormat = new SimpleDateFormat("mm:ss");

                for(AppEvent app : appEvents) {
                    String packageName = app.getApp().getPackageName();
                    String name = names.get(packageName);
                    if(name == null) {
                        name = AppUtils.getAppName(packageName, this);
                        names.put(packageName, name);
                    }
                    builder.append(startFormat.format(new Date(app.getStartTime())) + " for " + timeFormat.format(new Date(app.getDuration())) + ": " + name);
                    builder.append("\n");
                }

                ((TextView) findViewById(R.id.dataText)).setText(builder.toString());
            }
        }
    }

    public void clearButtonClick(View view) {
        for(Table table : monitorConnection.getDatabase().getTables()) {
            table.clearTable();
            table.createTable(monitorConnection.getDatabase().getWritableDatabase());
        }
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
        calendarHelper.requestCalendars(this::calendarDataReceived);
    }

    private void calendarDataReceived(List<CalendarData> calendars) {
        if(calendars == null) return;

        if(settings.currentCalendar == null || !calendars.contains(settings.currentCalendar)) {
            //TODO Pick a calendar

            CalendarData myTimetable = calendarHelper.getMyTimetableData(calendars);
            if(myTimetable != null) calendarChanged(myTimetable);

            return;
        } else handleGetEvents(settings.currentCalendar);
    }

    private void handleGetEvents(CalendarData calendar) {
//        calendarHelper.requestEvents(calendar, (events) -> {
//            if(events != null) CalendarTracker.updateEventsList(events);
//        });
        //TODO Change back
        List<CalendarEvent> list = Arrays.asList(new CalendarEvent(calendar, 1, "Test Event", "Test Building", 0, Long.MAX_VALUE));
        if(monitorConnection.isConnected()) monitorConnection.getService().getCalendarTracker().updateEventsList(list);
        else {
            eventsToUpdate.clear();
            eventsToUpdate.addAll(list);
        }

        calendarHelper.requestEvents(calendar, (events -> {
            SimpleDateFormat startFormat = new SimpleDateFormat("dd/MM HH:mm:ss");
            for(int i = 0; i < events.size(); i++) {
                CalendarEvent event = events.get(i);
                System.out.println(event.getDBID() + ". " + event.getTitle() + ", " + event.getLocation() + ": " + startFormat.format(new Date(event.getStartTime())) + " - " + startFormat.format(new Date(event.getEndTime())));
            }
        }));
    }

    private void calendarChanged(CalendarData calendar) {
        settings.currentCalendar = calendar;
        handleGetEvents(calendar);
        try {
            settings.save();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        calendarHelper.onPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(monitorConnection.isConnected()) monitorConnection.getService().saveData();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if(monitorConnection.isConnected()) monitorConnection.getService().saveData();
        unbindService(monitorConnection);
    }
}
