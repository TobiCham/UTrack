package edu.tracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import edu.tracker.data.ScreenDataType;
import edu.tracker.monitor.MonitorConnection;
import edu.tracker.monitor.MonitorService;
import edu.tracker.monitor.screen.ScreenData;

public class MainActivity extends AppCompatActivity {

    private MonitorConnection monitorConnection;

    public MainActivity() {
        System.out.println("create activity");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("create");

        startService(new Intent(this, MonitorService.class));
        setContentView(R.layout.activity_main);

        monitorConnection = new MonitorConnection(() -> {
            System.out.println("BOUND");
            updateUnlocks();
        });
    }

    public void updateUnlocks() {
        if(monitorConnection.isConnected()) {
            Map<ScreenDataType, Integer> map = monitorConnection.getDatabase().getTableScreen().getScreenCounts();
            List<ScreenDataType> sortedTypes = new ArrayList<>(map.keySet());
            Collections.sort(sortedTypes, (t1, t2) -> t1.getFriendlyName().compareTo(t2.getFriendlyName()));

            StringBuilder builder = new StringBuilder();
            for(int i = 0; i < sortedTypes.size(); i++) {
                if(i != 0) builder.append(", ");
                builder.append(sortedTypes.get(i).getFriendlyName()).append(": ").append(map.get(sortedTypes.get(i)));
            }
            monitorConnection.getDatabase().getTableScreen().getAllData();
            ((TextView) findViewById(R.id.dataText)).setText(builder.toString());
        }
    }

    public void clearButtonClick(View view) {
        monitorConnection.getDatabase().getTableScreen().clearTable();
        updateUnlocks();
    }

    @Override
    protected void onStart() {
        super.onStart();
        bindService(new Intent(this, MonitorService.class), monitorConnection, Context.BIND_ABOVE_CLIENT);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateUnlocks();
    }

    @Override
    protected void onStop() {
        super.onStop();
        System.out.println("Stop!");
        unbindService(monitorConnection);
    }
}
