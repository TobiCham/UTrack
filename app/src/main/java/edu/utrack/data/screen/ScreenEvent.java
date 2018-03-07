package edu.utrack.data.screen;

/**
 * Created by Tobi on 26/02/2018.
 */

public class ScreenEvent {

    private ScreenDataType type;
    private long timeStamp;

    public ScreenEvent(ScreenDataType type) {
        this(type, System.currentTimeMillis());
    }

    public ScreenEvent(ScreenDataType type, long timeStamp) {
        this.type = type;
        this.timeStamp = timeStamp;
    }

    public ScreenDataType getType() {
        return type;
    }

    public long getTimeStamp() {
        return timeStamp;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ScreenEvent event = (ScreenEvent) o;
        return type == event.type && timeStamp == event.timeStamp;
    }

    @Override
    public int hashCode() {
        return (47 * type.hashCode()) + (int) (timeStamp ^ (timeStamp >>> 32));
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("ScreenEvent{");
        sb.append("type=").append(type);
        sb.append(", timeStamp=").append(timeStamp);
        sb.append('}');
        return sb.toString();
    }
}
