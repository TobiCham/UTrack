package edu.utrack.data.app;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppEvent appEvent = (AppEvent) o;
        return startTime == appEvent.startTime && endTime == appEvent.endTime && app.equals(appEvent.app);
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
        StringBuilder sb = new StringBuilder("AppEvent{");
        sb.append("app=").append(app);
        sb.append(", startTime=").append(startTime);
        sb.append(", endTime=").append(endTime);
        sb.append('}');
        return sb.toString();
    }
}
