package edu.utrack.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import edu.utrack.R;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.monitor.MonitorConnection;
import edu.utrack.monitor.MonitorService;
import edu.utrack.settings.AppSettings;

public class MainActivity extends Activity {

    private MonitorConnection monitorConnection;
    private List<CalendarEvent> eventsToUpdate = new ArrayList<>();

    public static AppSettings settings;

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
        startService(new Intent(this, MonitorService.class));
//        monitorConnection = new MonitorConnection(() -> {
//            System.out.println("BOUND");
//            updateUnlocks();
//            if(!eventsToUpdate.isEmpty()) {
//                monitorConnection.getService().getCalendarTracker().updateEventsList(eventsToUpdate);
//                eventsToUpdate.clear();
//            }
//        });
        setContentView(R.layout.simple_calendar_view);
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, MonitorService.class), monitorConnection, Context.BIND_ABOVE_CLIENT);
    }

    @Override
    protected void onResume() {
        super.onResume();
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
