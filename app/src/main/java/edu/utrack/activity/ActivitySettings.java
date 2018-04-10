package edu.utrack.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.Map;

/**
 * Created by Tobi on 30/03/2018.
 */

public class ActivitySettings extends MonitorActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Settings");
        getFragmentManager().beginTransaction().replace(android.R.id.content, new SettingsFragment()).commit();
    }

    @Override
    public void onConnected() {}

    @Override
    public void onDisconnected() {
        if(getConnection() != null) {
            getConnection().getService().refreshSettings();
        } else {
            new Exception("CONNECTION IS NULL RIP").printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("SETTINGS STOP");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public TrackMenuType getMenuType() {
        return TrackMenuType.BACK;
    }

    @Override
    public void getMenuItems(Map<Integer, Runnable> menus) {}
}
