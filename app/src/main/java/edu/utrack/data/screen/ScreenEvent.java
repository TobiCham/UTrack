package edu.utrack.data.screen;

import android.support.annotation.NonNull;

import edu.utrack.util.DataClass;

/**
 * Created by Tobi on 26/02/2018.
 */

public class ScreenEvent extends DataClass implements Comparable<ScreenEvent> {

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
    protected Object[] getFields() {
        return new Object[] {type, timeStamp};
    }

    @Override
    protected String[] getFieldNames() {
        return new String[] {"type", "time"};
    }

    @Override
    public int compareTo(@NonNull ScreenEvent o) {
        if(equals(o)) return 0;
        int compare = Long.compare(timeStamp, o.timeStamp);
        if(compare != 0) return compare;
        return type.compareTo(o.type);
    }
}
