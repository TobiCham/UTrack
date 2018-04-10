package edu.utrack.monitor.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import edu.utrack.data.screen.ScreenEvent;
import edu.utrack.data.screen.ScreenEventType;
import edu.utrack.monitor.MonitorService;

public class PhoneScreenListener extends BroadcastReceiver {

    private MonitorService service;

    public PhoneScreenListener(MonitorService service) {
        this.service = service;
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();

        ScreenEventType type = ScreenEventType.getByIntentAction(action);
        if(type == null) return; //This shouldn't happen

        if(!service.doesTrack()) {
            System.out.println("Received screen event but not tracking");
            return;
        }

        List<ScreenEvent> eventList = new ArrayList<>();

        eventList.add(new ScreenEvent(type));
        if(!eventList.isEmpty()) service.getDatabase().getScreenEventsTable().insertData(eventList);
    }
}
