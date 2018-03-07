package edu.utrack.monitor;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import edu.utrack.data.app.AppData;
import edu.utrack.data.app.AppEvent;
import edu.utrack.database.Database;
import edu.utrack.database.table.DataTable;
import edu.utrack.database.table.Table;
import edu.utrack.data.screen.ScreenDataType;
import edu.utrack.monitor.app.ActivityMonitor;
import edu.utrack.data.app.ForegroundAppInfo;
import edu.utrack.monitor.screen.PhoneScreenListener;

/**
 * Created by Tobi on 26/02/2018.
 */

public class MonitorService extends Service {

    private static final int SAVE_TIME = 150;
    private static final int APP_CHECK_TIME = 1;

    private PhoneScreenListener listener;
    private ActivityMonitor monitor;

    private Binder binder = new Binder();
    private Database database;

    private Thread saveThread;

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
            for(ScreenDataType type : ScreenDataType.values()) filter.addAction(type.getIntentAction());

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

    private void activityChanged(ForegroundAppInfo from, ForegroundAppInfo to, long startTime, long time) {
        from.getPackageName();

        System.out.println("Change app! " + from.getPackageName() + " -> " + (to == null ? "Nothing" : to.getPackageName()));
        System.out.println("Spent " + (time / 1000) + " seconds in the app.");

        //Gets the app data for the app which was running (or creates if doesn't already exist - thats important!)
        AppData data = database.getAppsTable().getOrCreateAppData(from.getPackageName());

        database.getAppEventsTable().insertData(new AppEvent(data, startTime, startTime + time));
    }

    @Override
    public void onDestroy() {
        unregisterReceiver(listener);
        listener = null;

        saveThread.interrupt();
        saveThread = null;

        monitor.stop();
        monitor = null;
    }

    private void saveData() {
        for(Table table : database.getTables()) {
            if(table instanceof DataTable) ((DataTable) table).saveCache();
        }
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
