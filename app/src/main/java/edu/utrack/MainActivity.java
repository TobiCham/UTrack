package edu.utrack;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.utrack.calendar.CalendarData;
import edu.utrack.calendar.CalendarHelper;
import edu.utrack.data.ScreenDataType;
import edu.utrack.monitor.MonitorConnection;
import edu.utrack.monitor.MonitorService;

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
            Map<ScreenDataType, Integer> map = monitorConnection.getDatabase().getScreenTable().getScreenCounts();
            List<ScreenDataType> sortedTypes = new ArrayList<>(map.keySet());
            Collections.sort(sortedTypes, (t1, t2) -> t1.getFriendlyName().compareTo(t2.getFriendlyName()));

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < sortedTypes.size(); i++) {
                if (i != 0) builder.append(", ");
                builder.append(sortedTypes.get(i).getFriendlyName()).append(": ").append(map.get(sortedTypes.get(i)));
            }
            monitorConnection.getDatabase().getScreenTable().getAllData();
            ((TextView) findViewById(R.id.dataText)).setText(builder.toString());
        }
    }

    public void clearButtonClick(View view) {
        monitorConnection.getDatabase().getScreenTable().clearTable();
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
            if(myTimetable != null) System.out.println("Mytimetable found!");
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
