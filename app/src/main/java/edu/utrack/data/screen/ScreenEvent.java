package edu.utrack.data.screen;

import android.support.annotation.NonNull;

/**
 * Created by Tobi on 26/02/2018.
 */

public class ScreenEvent implements Comparable<ScreenEvent> {

    private ScreenEventType type;
    private long timeStamp;

    public ScreenEvent(ScreenEventType type) {
        this(System.currentTimeMillis(), type);
    }

    public ScreenEvent(long timeStamp, ScreenEventType type) {
        this.type = type;
        this.timeStamp = timeStamp;
    }

    public ScreenEventType getType() {
        return type;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScreenEvent screenEvent = (ScreenEvent) o;
        return timeStamp == screenEvent.timeStamp && type == screenEvent.type;
    }

    @Override
    public int hashCode() {
        int result = type.hashCode();
        result = 53 * result + (int) (timeStamp ^ (timeStamp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ScreenEvent{");
        sb.append("type=").append(type);
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
