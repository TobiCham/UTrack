package edu.tracker.monitor;

import android.app.Service;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.support.annotation.Nullable;

import edu.tracker.monitor.screen.PhoneScreenListener;

/**
 * Created by Tobi on 26/02/2018.
 */

public class MonitorService extends Service {

    private PhoneScreenListener listener;
    private Binder binder = new Binder();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        if(listener == null) {
            listener = new PhoneScreenListener();

            IntentFilter filter = new IntentFilter();
            filter.addAction(Intent.ACTION_SCREEN_ON);
            filter.addAction(Intent.ACTION_SCREEN_OFF);
            filter.addAction(Intent.ACTION_USER_PRESENT);

            registerReceiver(listener, filter);
        }
    }

    @Override
    public void onDestroy() {
        //TODO Save data
        unregisterReceiver(listener);
        listener = null;
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
