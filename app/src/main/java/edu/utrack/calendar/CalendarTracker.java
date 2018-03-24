package edu.utrack.calendar;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utrack.data.calendar.CalendarEvent;

/**
 * Created by Tobi on 09/03/2018.
 */

public class CalendarTracker {

    private List<CalendarEvent> events = new ArrayList<>();
    private Map<Integer, CalendarEvent> idsMap = new HashMap<>();

    public void updateEventsList(List<CalendarEvent> events) {
        this.events.clear();
        this.idsMap.clear();

        this.events.addAll(events);
        for(CalendarEvent event : events) idsMap.put(event.getDBID(), event);
    }

    public List<CalendarEvent> getCurrentEvents() {
        List<CalendarEvent> eventsList = new ArrayList<>();

        long time = System.currentTimeMillis();
        for(CalendarEvent event : this.events) {
            if(time >= event.getStartTime() && time <= event.getEndTime()) eventsList.add(event);
        }

        return eventsList;
    }

    public CalendarEvent getEvent(int id) {
        return idsMap.get(id);
    }

    public List<CalendarEvent> getEvents() {
        return events;
    }
}
