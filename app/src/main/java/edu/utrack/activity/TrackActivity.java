package edu.utrack.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

import edu.utrack.BootListener;
import edu.utrack.R;
import edu.utrack.calendar.CalendarHelper;
import edu.utrack.goals.GoalManager;
import edu.utrack.monitor.MonitorService;
import edu.utrack.settings.AppSettings;
import edu.utrack.settings.EventExcluder;

/**
 * Created by Tobi on 29/03/2018.
 * Base activity which automatically starts the monitor service
 */
public abstract class TrackActivity extends AppCompatActivity {

    private Map<Integer, Runnable> menuItems = new LinkedHashMap<>();

    private static CalendarHelper calendarHelper;
    private static AppSettings settings;
    private static EventExcluder eventExcluder;
    private static GoalManager goalManager;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TrackMenuType type = getMenuType();
        if(type == TrackMenuType.NONE) return;

        if(type == TrackMenuType.BACK) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        BootListener.registerGoalAlarm(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (calendarHelper != null) {
            calendarHelper.onPermissionResult(this, requestCode, permissions, grantResults);
        }
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

    public static AppSettings createSettings(Context context) {
        if(settings == null) {
            settings = new AppSettings(new File(context.getFilesDir(), "settings.conf"));
            settings.load();
        }
        return settings;
    }

    public AppSettings getSettings() {
        return createSettings(this);
    }

    public static EventExcluder createExcluder(Context context) {
        if(eventExcluder == null) eventExcluder = new EventExcluder(new File(context.getFilesDir(), "excluded_events.dat"));
        return eventExcluder;
    }

    public EventExcluder getEventExcluder() {
        return createExcluder(this);
    }

    public CalendarHelper getCalendarHelper() {
        if(calendarHelper == null) calendarHelper = new CalendarHelper();
        return calendarHelper;
    }

    public static GoalManager createGoalManager(Context context) {
        if(goalManager == null) goalManager = new GoalManager(new File(context.getFilesDir(), "goals.dat"));
        return goalManager;
    }

    public GoalManager getGoalManager() {
        return createGoalManager(this);
    }

    public enum TrackMenuType {
        BACK,
        MENU,
        CUSTOM,
        NONE
    }
}
