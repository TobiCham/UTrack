package edu.tracker.monitor;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import edu.tracker.data.Database;
import edu.tracker.data.ScreenDataType;
import edu.tracker.monitor.screen.PhoneScreenListener;
import edu.tracker.monitor.screen.ScreenData;

/**
 * Created by Tobi on 26/02/2018.
 */

public class MonitorService extends Service {

    private PhoneScreenListener listener;
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
        database = new Database(this);
        database.getWritableDatabase(); //Open db

        if(listener == null) {
            listener = new PhoneScreenListener(this);

            IntentFilter filter = new IntentFilter();
            for(ScreenDataType type : ScreenDataType.values()) filter.addAction(type.getIntentAction());

            registerReceiver(listener, filter);
        }
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
        });
        saveThread.start();
    }

    @Override
    public void onDestroy() {
        //TODO Save data
        unregisterReceiver(listener);
        listener = null;
        saveThread.interrupt();
    }

    private void saveData() {
        database.getTableScreen().saveCache();
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
