package edu.utrack.data.screen;

import android.support.annotation.NonNull;

import java.util.Objects;

import edu.utrack.data.CalendarEventEvent;
import edu.utrack.data.calendar.CalendarEvent;

/**
 * Created by Tobi on 26/02/2018.
 */

public class ScreenEvent implements CalendarEventEvent, Comparable<ScreenEvent> {

    private ScreenEventType type;
    private CalendarEvent event;
    private long timeStamp;

    public ScreenEvent(ScreenEventType type, CalendarEvent event) {
        this(type, System.currentTimeMillis(), event);
    }

    public ScreenEvent(ScreenEventType type, long timeStamp, CalendarEvent event) {
        this.type = type;
        this.event = event;
        this.timeStamp = timeStamp;
    }

    public ScreenEventType getType() {
        return type;
    }

    @Override
    public CalendarEvent getEvent() {
        return event;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScreenEvent screenEvent = (ScreenEvent) o;
        return timeStamp == screenEvent.timeStamp && type == screenEvent.type && Objects.equals(event, screenEvent.event);
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
//        result = 53 * result + event.hashCode();
        result = 53 * result + (int) (timeStamp ^ (timeStamp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ScreenEvent{");
        sb.append("type=").append(type);
        sb.append(", event=").append(event);
        sb.append(", timeStamp=").append(timeStamp);
        sb.append('}');
        return sb.toString();
    }

    @Override
    public int compareTo(@NonNull ScreenEvent o) {
        if(equals(o)) return 0;
        int compare = Long.compare(timeStamp, o.timeStamp);
        if(compare != 0) return compare;
        return type.compareTo(o.type);
    }
}
