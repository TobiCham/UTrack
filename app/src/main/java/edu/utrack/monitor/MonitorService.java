package edu.utrack.monitor;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import edu.utrack.MainActivity;
import edu.utrack.calendar.CalendarTracker;
import edu.utrack.data.app.AppData;
import edu.utrack.data.app.AppEvent;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.database.Database;
import edu.utrack.database.table.EventTable;
import edu.utrack.database.table.Table;
import edu.utrack.data.screen.ScreenEventType;
import edu.utrack.monitor.app.ActivityMonitor;
import edu.utrack.data.app.ForegroundAppInfo;
import edu.utrack.monitor.screen.PhoneScreenListener;
import edu.utrack.util.AppUtils;

/**
 * Created by Tobi on 26/02/2018.
 */

public class MonitorService extends Service {

    private static final int SAVE_TIME = 15;
    private static final int APP_CHECK_TIME = 1;

    private PhoneScreenListener listener;
    private ActivityMonitor monitor;

    private Binder binder = new Binder();
    private Database database;
    private CalendarTracker calendarTracker;

    private Thread saveThread;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        System.out.println("Service created");

        if(calendarTracker == null) {
            calendarTracker = new CalendarTracker();
        }

        if(database == null) {
            database = new Database(this);
            database.getWritableDatabase(); //Open db
        }

        if(listener == null) {
            listener = new PhoneScreenListener(this);

            IntentFilter filter = new IntentFilter();
            for(ScreenEventType type : ScreenEventType.values()) filter.addAction(type.getIntentAction());

            registerReceiver(listener, filter);
        }

        if(saveThread == null) {
            saveThread = new Thread(() -> {
                while(true) {
                    saveData();
                    try {
                        Thread.sleep(SAVE_TIME * 1000);
                    } catch (InterruptedException e) {
                        //Thread has been interrupted, stop saving
                        break;
                    }
                }
                saveData();
                database.close();
                database = null;
            });
            saveThread.start();
        }

        if(monitor == null) {
            monitor = new ActivityMonitor(this, APP_CHECK_TIME * 1000, this::activityChanged);
            monitor.start();
        }
    }

    private void activityChanged(ForegroundAppInfo from, ForegroundAppInfo to, long startTime, long time, CalendarEvent event) {
        //Gets the app data for the app which was running (or creates if doesn't already exist - that's important!)

        String fromName = from == null ? "Nothing" : AppUtils.getAppName(from.getPackageName(), this);

        System.out.println("In app " + fromName + " for " + (time / 1000) + "s");

        AppData data = database.getAppsTable().getOrCreateAppData(from.getPackageName());

        database.getAppEventsTable().insertData(new AppEvent(data, event, startTime, startTime + time));
    }

    @Override
    public void onDestroy() {
        System.out.println("Service destroyed");
        unregisterReceiver(listener);
        listener = null;

        saveThread.interrupt();
        saveThread = null;

        monitor.stop();
        monitor = null;

        calendarTracker = null;
    }

    public void saveData() {
        System.out.println("Saving Data");
        for(Table table : database.getTables()) {
            if(table instanceof EventTable) ((EventTable) table).saveCache();
        }
    }

    public CalendarTracker getCalendarTracker() {
        return calendarTracker;
    }

    public Database getDatabase() {
        return database;
    }

    public PhoneScreenListener getListener() {
        return listener;
    }

    public class Binder extends android.os.Binder {
        public MonitorService getService() {
            return MonitorService.this;
        }
    }
}
