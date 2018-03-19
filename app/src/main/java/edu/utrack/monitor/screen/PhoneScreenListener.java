package edu.utrack.monitor.screen;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.List;

import edu.utrack.MainActivity;
import edu.utrack.calendar.CalendarTracker;
import edu.utrack.data.calendar.CalendarEvent;
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

        List<ScreenEvent> eventList = new ArrayList<>();

        for(CalendarEvent event : service.getCalendarTracker().getCurrentEvents()) {
            eventList.add(new ScreenEvent(type, event));
        }
        if(!eventList.isEmpty()) service.getDatabase().getScreenEventsTable().insertData(eventList);

        System.out.println("Screen Event: " + type + " (" + action + ")");

        if(context instanceof MainActivity) ((MainActivity) context).updateUnlocks();
    }
}
