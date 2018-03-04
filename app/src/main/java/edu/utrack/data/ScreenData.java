package edu.utrack.data;

/**
 * Created by Tobi on 26/02/2018.
 */

public class ScreenData {

    private ScreenDataType type;
    private long timeStamp;

    public ScreenData(ScreenDataType type) {
        this(type, System.currentTimeMillis());
    }

    public ScreenData(ScreenDataType type, long timeStamp) {
        this.type = type;
        this.timeStamp = timeStamp;
    }

    public ScreenDataType getType() {
        return type;
    }

    public long getTimeStamp() {
        return timeStamp;
    }
}
