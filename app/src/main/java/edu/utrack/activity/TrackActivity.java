package edu.utrack.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.utrack.R;
import edu.utrack.calendar.CalendarHelper;
import edu.utrack.monitor.MonitorService;
import edu.utrack.settings.AppSettings;
import edu.utrack.settings.EventExcluder;

/**
 * Created by Tobi on 29/03/2018.
 */

/**
 * Base activity which automatically starts the monitor service
 */
public abstract class TrackActivity extends AppCompatActivity {

    private Map<Integer, Runnable> menuItems = new LinkedHashMap<>();

    private static CalendarHelper calendarHelper;
    private static AppSettings settings;
    private static EventExcluder eventExcluder;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TrackMenuType type = getMenuType();
        if(type == TrackMenuType.NONE) return;

        if(type == TrackMenuType.BACK) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if(calendarHelper != null) calendarHelper.onPermissionResult(requestCode, permissions, grantResults);
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, MonitorService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menuItems.clear();
        getMenuItems(menuItems);

        TrackMenuType type = getMenuType();
        if(type == TrackMenuType.NONE) return false;

        getMenuInflater().inflate(R.menu.menu, menu);
        for(int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(menuItems.containsKey(menu.getItem(i).getItemId()));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        Runnable runnable = menuItems.get(item.getItemId());
        if(runnable != null) {
            runnable.run();
            return true;
        }
        else return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public abstract TrackMenuType getMenuType();

    public void getMenuItems(Map<Integer, Runnable> menus) {
        menuItems.put(R.id.menuSettings, () -> {
            Intent intent = new Intent(this, ActivitySettings.class);
            startActivity(intent);
        });
    }

    public AppSettings getSettings() {
        if(settings != null) return settings;
        settings = new AppSettings(getSettingsFile(this));
        settings.load();
        return settings;
    }

    public EventExcluder getEventExcluder() {
        if(eventExcluder == null) eventExcluder = new EventExcluder(new File(getFilesDir(), "excluded_events.dat"));
        return eventExcluder;
    }

    public CalendarHelper getCalendarHelper() {
        if(calendarHelper == null) calendarHelper = new CalendarHelper(this);
        return calendarHelper;
    }

    public static File getSettingsFile(Context context) {
        return new File(context.getFilesDir(), "settings.conf");
    }


    public enum TrackMenuType {
        BACK,
        MENU,
        CUSTOM,
        NONE
    }
}
