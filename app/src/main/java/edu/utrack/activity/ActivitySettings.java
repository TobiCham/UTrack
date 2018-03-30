package edu.utrack.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.Map;

/**
 * Created by Tobi on 30/03/2018.
 */

public class ActivitySettings extends TrackActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getSupportActionBar().setTitle("Settings");
    }

    @Override
    public TrackMenuType getMenuType() {
        return TrackMenuType.BACK;
    }

    @Override
    public void getMenuItems(Map<Integer, Runnable> menus) {}
}
