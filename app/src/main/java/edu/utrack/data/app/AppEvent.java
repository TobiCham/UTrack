package edu.utrack.data.app;

import android.support.annotation.NonNull;

import java.util.Objects;

import edu.utrack.data.CalendarEventEvent;
import edu.utrack.data.calendar.CalendarEvent;

/**
 * Created by Tobi on 07/03/2018.
 */

public class AppEvent implements CalendarEventEvent, Comparable<AppEvent> {

    private AppData app;
    private CalendarEvent event;
    private long startTime, endTime;

    public AppEvent(AppData app, CalendarEvent event, long startTime, long endTime) {
        this.app = app;
        this.event = event;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public AppData getApp() {
        return app;
    }

    @Override
    public CalendarEvent getEvent() {
        return event;
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppEvent appEvent = (AppEvent) o;
        if(startTime != appEvent.startTime || endTime != appEvent.endTime) return false;
        return Objects.equals(app, appEvent.app) && Objects.equals(event, appEvent.event);
    }

    @Override
    public int hashCode() {
        int result = app.hashCode();
//        result = 37 * result + event.hashCode();
        result = 37 * result + (int) (startTime ^ (startTime >>> 32));
        result = 37 * result + (int) (endTime ^ (endTime >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("AppEvent{");
        sb.append("app=").append(app);
        sb.append(", event=").append(event);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(@NonNull AppEvent o) {
        if(equals(this)) return 0;
        int compare = Long.compare(endTime, o.endTime);
        if(compare != 0) return compare;
        return app.compareTo(o.app);
    }
}
