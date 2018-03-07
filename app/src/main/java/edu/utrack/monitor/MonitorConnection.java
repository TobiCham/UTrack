package edu.utrack.monitor;

import android.content.ComponentName;
import android.content.ServiceConnection;
import android.os.IBinder;

import edu.utrack.database.Database;

/**
 * Created by Tobi on 26/02/2018.
 */

public class MonitorConnection implements ServiceConnection {

    private MonitorService service;
    private Runnable onConnect;

    public MonitorConnection() {}
    public MonitorConnection(Runnable onConnect) {
        this.onConnect = onConnect;
    }

    public boolean isConnected() {

        System.out.println(service);
        if(service != null) System.out.println(service.getListener());
        else System.out.println("null");

        return service != null;
    }

    public MonitorService getService() {
        return service;
    }

    public Database getDatabase() {
        return service.getDatabase();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        service = ((MonitorService.Binder) iBinder).getService();

        if(onConnect != null) onConnect.run();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        service = null;
    }
}
