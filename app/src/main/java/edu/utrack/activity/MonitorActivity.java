package edu.utrack.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;

import edu.utrack.database.table.EventTable;
import edu.utrack.database.table.Table;
import edu.utrack.monitor.MonitorConnection;
import edu.utrack.monitor.MonitorService;
import edu.utrack.settings.AppSettings;
import edu.utrack.settings.HistorySettingType;

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

        //Time since last deleted old data
        long timeElapsed = System.currentTimeMillis() - settings.lastDeletionTime;

        //One day
        if(timeElapsed > 24 * 60 * 60 * 1000) {
            deleteOldData();
        }

        onConnected();
    }

    public void deleteOldData() {

        System.out.println("Deleting old data...");
        HistorySettingType historySettingType = getSettings().historySetting;
        if(historySettingType == null) return;

        long historyDuration = historySettingType.getDays() * 24 * 60 * 60 * 1000;
        long startTime = System.currentTimeMillis() - historyDuration;

        for(Table table : connection.getDatabase().getTables()) {
            if(table instanceof EventTable) ((EventTable) table).deleteOlderThan(startTime);
        }
        getSettings().lastDeletionTime = System.currentTimeMillis();
        getSettings().save();
    }

    public MonitorConnection getConnection() {
        return connection;
    }
}
