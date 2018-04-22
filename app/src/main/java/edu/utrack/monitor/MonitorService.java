package edu.utrack.monitor;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import edu.utrack.activity.TrackActivity;
import edu.utrack.data.app.AppData;
import edu.utrack.data.app.AppEvent;
import edu.utrack.data.app.ForegroundAppInfo;
import edu.utrack.data.screen.ScreenEventType;
import edu.utrack.database.Database;
import edu.utrack.database.table.EventTable;
import edu.utrack.database.table.Table;
import edu.utrack.monitor.app.ActivityMonitor;
import edu.utrack.monitor.screen.PhoneScreenListener;
import edu.utrack.settings.AppSettings;
import edu.utrack.settings.HistorySettingType;

/**
 * Created by Tobi on 26/02/2018.
 */

public class MonitorService extends Service {

    private static final int SAVE_TIME = 10;
    private static final int APP_CHECK_TIME = 1;

    private Binder binder = new Binder();
    private AppSettings settings;

    private PhoneScreenListener listener;
    private ActivityMonitor monitor;
    private Thread saveThread;
    private Database database;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
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
            });
            saveThread.start();
        }
        if(monitor == null) {
            monitor = new ActivityMonitor(this, APP_CHECK_TIME * 1000, this::activityChanged);
            monitor.start();
        }
    }

    private AppSettings getSettings() {
        if(settings == null) {
            settings = new AppSettings(TrackActivity.getSettingsFile(this));
            settings.load();
        }
        return settings;
    }

    public void refreshSettings() {
        settings = null;
    }


    public boolean doesTrack() {
        return getSettings().tracks;
    }

    private void activityChanged(ForegroundAppInfo from, ForegroundAppInfo to, long startTime, long time) {
        if(database == null) return;

        //Gets the app data for the app which was running (or creates if doesn't already exist - that's important!)
        AppData data = database.getAppsTable().getOrCreateAppData(from.getPackageName());
        database.getAppEventsTable().insertData(new AppEvent(data, startTime, startTime + time));
    }

    @Override
    public void onDestroy() {
        if(listener != null) unregisterReceiver(listener);
        listener = null;

        if(monitor != null) monitor.stop();
        monitor = null;

        if(saveThread != null) {
            saveThread.interrupt();
        }
        saveThread = null;

        saveData();
        database.close();
        database = null;
    }

    public void saveData() {
        if(database == null) return;

        boolean saved = false;
        for(Table table : database.getTables()) {
            if(table instanceof EventTable) {
                if(!((EventTable) table).getToSaveData().isEmpty()) saved = true;
                ((EventTable) table).saveCache();
            }
        }

        //Time since last deleted old data
        long timeElapsed = System.currentTimeMillis() - getSettings().lastDeletionTime;

        //One day
        if(timeElapsed > 24 * 60 * 60 * 1000) {
            deleteOldData();
        }
    }

    public void deleteOldData() {
        if(database == null) return;

        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println("Deleting old data...");
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");
        System.out.println("AAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAAA");

        HistorySettingType historySettingType = getSettings().historySetting;
        if(historySettingType == null) return;

        long historyDuration = historySettingType.getDays() * 24 * 60 * 60 * 1000;
        long startTime = System.currentTimeMillis() - historyDuration;

        for(Table table : database.getTables()) {
            if(table instanceof EventTable) ((EventTable) table).deleteOlderThan(startTime);
        }
        getSettings().lastDeletionTime = System.currentTimeMillis();
        getSettings().save();
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
