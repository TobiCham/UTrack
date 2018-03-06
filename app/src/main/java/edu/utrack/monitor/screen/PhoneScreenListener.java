package edu.utrack.monitor.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import edu.utrack.MainActivity;
import edu.utrack.data.ScreenData;
import edu.utrack.data.ScreenDataType;
import edu.utrack.monitor.MonitorService;

public class PhoneScreenListener extends BroadcastReceiver {

    private MonitorService service;

    public PhoneScreenListener(MonitorService service) {
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        ScreenDataType type = ScreenDataType.getByIntentAction(action);
        if(type == null) return; //This shouldn't happen

        service.getDatabase().getScreenTable().insertData(new ScreenData(type));
        if(context instanceof MainActivity) ((MainActivity) context).updateUnlocks();
    }
}