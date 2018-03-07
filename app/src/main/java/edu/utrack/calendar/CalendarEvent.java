package edu.utrack.calendar;

/**
 * Created by Tobi on 07/03/2018.
 */

public class CalendarEvent {

    private CalendarData calendar;
    private int id;
    private String title, location;
    private long start, end;
    private boolean allDay;

    public CalendarEvent(CalendarData calendar, int id, String title, String location, long start, long end, boolean allDay) {
        this.calendar = calendar;
        this.id = id;
        this.title = title;
        this.location = location;
        this.start = start;
        this.end = end;
        this.allDay = allDay;
    }

    public CalendarData getCalendar() {
        return calendar;
    }

    public int getDBID() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getLocation() {
        return location;
    }

    public long getStartTime() {
        return start;
    }

    public long getEndTime() {
        return end;
    }

    public boolean isAllDay() {
        return allDay;
    }
}
