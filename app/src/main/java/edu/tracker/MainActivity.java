package edu.tracker;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import edu.tracker.monitor.MonitorConnection;
import edu.tracker.monitor.MonitorService;
import edu.tracker.monitor.screen.PhoneScreenData;

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
            PhoneScreenData data = monitorConnection.getScreenData();
            String text = "Unlocks: " + data.getUnlocks() + ", Screen Ons: " + data.getScreenOn() + ", Screen Offs: " + data.getScreenOff();
            ((TextView) findViewById(R.id.dataText)).setText(text);
        }
    }

    public void clearButtonClick(View view) {
        monitorConnection.getScreenData().reset();
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
