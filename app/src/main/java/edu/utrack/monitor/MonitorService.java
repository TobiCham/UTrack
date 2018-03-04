package edu.utrack.monitor;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import edu.utrack.data.database.Database;
import edu.utrack.data.ScreenDataType;
import edu.utrack.monitor.app.ActivityMonitor;
import edu.utrack.monitor.app.ForegroundAppInfo;
import edu.utrack.monitor.screen.PhoneScreenListener;

/**
 * Created by Tobi on 26/02/2018.
 */

public class MonitorService extends Service {

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
                        Thread.sleep(30_000);
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
            monitor = new ActivityMonitor(this, 5_000, this::activityCallback);
            monitor.start();
        }
    }

    private void activityCallback(ForegroundAppInfo app) {
        if(app == null) return;

        System.out.println("Currently running app: " + app.getLabel() + " (" + app.getPackageName() + ")");
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
        database.getScreenTable().saveCache();
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
