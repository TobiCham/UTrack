package edu.utrack.data.calendar;

import edu.utrack.util.DataClass;

/**
 * Created by Tobi on 07/03/2018.
 */

public class CalendarEvent extends DataClass {

    private CalendarData calendar;
    private int id;
    private String title, location;
    private long start, end;

    public CalendarEvent(CalendarData calendar, int id, String title, String location, long start, long end) {
        this.calendar = calendar;
        this.id = id;
        this.title = title;
        this.location = location;
        this.start = start;
        this.end = end;
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

    public long getDuration() {
        long time = end - start;
        if(time < 0) return 0;
        return time;
    }

    @Override
    protected String[] getFieldNames() {
        return new String[] {"id", "calendar", "title", "location", "start", "end"};
    }

    @Override
    protected Object[] getFields() {
        return new Object[] {id, calendar, title, location, start, end};
    }
}
