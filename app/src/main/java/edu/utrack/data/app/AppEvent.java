package edu.utrack.data.app;

import java.util.Objects;

import edu.utrack.data.calendar.CalendarEvent;

/**
 * Created by Tobi on 07/03/2018.
 */

public class AppEvent {

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

    public long getStartTime(CalendarEvent event) {
        if(startTime < event.getStartTime()) return event.getStartTime();
        return startTime;
    }

    public long getEndTime(CalendarEvent event) {
        if(endTime > event.getEndTime()) return event.getEndTime();
        return endTime;
    }

    public long getDuration(CalendarEvent event) {
        long time = getEndTime(event) - getStartTime(event);
        if(time < 0) return 0;
        return time;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppEvent appEvent = (AppEvent) o;
        if(startTime != appEvent.startTime || endTime != appEvent.endTime) return false;
        return Objects.equals(app, appEvent.app);
    }

    @Override
    public int hashCode() {
        int result = app.hashCode();
        result = 37 * result + (int) (startTime ^ (startTime >>> 32));
        result = 37 * result + (int) (endTime ^ (endTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AppEvent{");
        sb.append("app=").append(app);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append('}');
        return sb.toString();
    }
}
