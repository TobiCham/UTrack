package edu.utrack.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;

import java.util.LinkedHashMap;
import java.util.Map;

import edu.utrack.R;
import edu.utrack.monitor.MonitorService;

/**
 * Created by Tobi on 29/03/2018.
 */

/**
 * Base activity which automatically starts the monitor service
 */
public abstract class TrackActivity extends AppCompatActivity {

    private Map<Integer, Runnable> menuItems = new LinkedHashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        TrackMenuType type = getMenuType();
        if(type == TrackMenuType.NONE) return;

        this.menuItems.clear();
        getMenuItems(menuItems);

        if(type == TrackMenuType.BACK) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        startService(new Intent(this, MonitorService.class));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        TrackMenuType type = getMenuType();
        if(type == TrackMenuType.NONE) return false;

        getMenuInflater().inflate(R.menu.menu, menu);
        for(int i = 0; i < menu.size(); i++) {
            menu.getItem(i).setVisible(menuItems.containsKey(menu.getItem(i).getItemId()));
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        System.out.println("selected");
        if(item.getItemId() == android.R.id.home) {
            this.finish();
            return true;
        }
        Runnable runnable = menuItems.get(item.getItemId());
        if(runnable != null) {
            runnable.run();
            return true;
        }
        else return false;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    public abstract TrackMenuType getMenuType();

    public void getMenuItems(Map<Integer, Runnable> menus) {
        menuItems.put(R.id.menuSettings, () -> {
            Intent intent = new Intent(this, ActivitySettings.class);
            startActivity(intent);
        });
    }

    public enum TrackMenuType {
        BACK,
        MENU,
        CUSTOM,
        NONE
    }
}
