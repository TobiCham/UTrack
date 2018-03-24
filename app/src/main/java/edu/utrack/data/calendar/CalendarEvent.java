package edu.utrack.data.calendar;

import java.util.Objects;

/**
 * Created by Tobi on 07/03/2018.
 */

public class CalendarEvent {

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarEvent event = (CalendarEvent) o;

        if(id != event.id || start != event.start || end != event.end) return false;
        return Objects.equals(calendar, event.calendar) && Objects.equals(title, event.title) && Objects.equals(location, event.location);
    }

    @Override
    public int hashCode() {
        int result = calendar.hashCode();
        result = 47 * result + id;
        result = 47 * result + title.hashCode();
        result = 47 * result + location.hashCode();
        result = 47 * result + (int) (start ^ (start >>> 32));
        result = 47 * result + (int) (end ^ (end >>> 32));
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CalendarEvent{");
        sb.append("calendar=").append(calendar);
        sb.append(", id=").append(id);
        sb.append(", title='").append(title).append('\'');
        sb.append(", location='").append(location).append('\'');
        sb.append(", start=").append(start);
        sb.append(", end=").append(end);
        sb.append('}');
        return sb.toString();
    }
}
