package edu.utrack.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import edu.utrack.monitor.MonitorConnection;
import edu.utrack.monitor.MonitorService;

/**
 * Created by Tobi on 29/03/2018.
 */

/**
 * You should extend this class if you wish to use any data from the database, or interact with the monitor service in any way
 */
public abstract class MonitorActivity extends TrackActivity {

    private MonitorConnection connection;

    public abstract void onConnected();

    @Override
    protected void onResume() {
        super.onResume();

        connection = new MonitorConnection(this::onConnected);
        bindService(new Intent(this, MonitorService.class), connection, Context.BIND_ABOVE_CLIENT);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(connection);
        connection = null;
    }

    public MonitorConnection getConnection() {
        return connection;
    }
}
