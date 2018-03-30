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
        setContentView(R.layout.simple_calendar_view);
    }
}
