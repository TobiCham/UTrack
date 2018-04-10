package edu.utrack.activity;

import android.content.Context;
import android.content.Intent;

import edu.utrack.monitor.MonitorConnection;
import edu.utrack.monitor.MonitorService;
import edu.utrack.settings.AppSettings;

/**
 * Created by Tobi on 29/03/2018.
 */

/**
 * You should extend this class if you wish to use any data from the database, or interact with the monitor service in any way
 */
public abstract class MonitorActivity extends TrackActivity {

    private MonitorConnection connection;

    public abstract void onConnected();

    public void onDisconnected() {}

    @Override
    protected void onResume() {
        super.onResume();

        connection = new MonitorConnection(this::internalConnected);
        bindService(new Intent(this, MonitorService.class), connection, Context.BIND_ABOVE_CLIENT);
    }

    @Override
    protected void onPause() {
        super.onPause();

        onDisconnected();
        unbindService(connection);

        connection = null;
    }

    private void internalConnected() {
        AppSettings settings = getSettings();



        onConnected();
    }



    public MonitorConnection getConnection() {
        return connection;
    }
}
