package edu.utrack.data.app;

import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.util.DataClass;

/**
 * Created by Tobi on 07/03/2018.
 */

public class AppEvent extends DataClass {

    private AppData app;
    private long startTime, endTime;

    public AppEvent(AppData app, long startTime, long endTime) {
        this.app = app;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public AppData getApp() {
        return app;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getDuration() {
        return getEndTime() - getStartTime();
    }

    public long getStartTime(long begin, long end) {
        if(startTime < begin) return begin;
        return startTime;
    }

    public long getEndTime(long begin, long end) {
        if(endTime > end) return end;
        return endTime;
    }

    public long getDuration(long begin, long end) {
        long time = getEndTime(begin, end) - getStartTime(begin, end);
        if(time < 0) return 0;
        return time;
    }

    public long getStartTime(CalendarEvent event) {
        return getStartTime(event.getStartTime(), event.getEndTime());
    }

    public long getEndTime(CalendarEvent event) {
        return getEndTime(event.getStartTime(), event.getEndTime());
    }

    public long getDuration(CalendarEvent event) {
        return getDuration(event.getStartTime(), event.getEndTime());
    }

    @Override
    protected String[] getFieldNames() {
        return new String[] {"app", "startTime", "endTime"};
    }

    @Override
    protected Object[] getFields() {
        return new Object[] {app, startTime, endTime};
    }
}
