package edu.utrack.goals;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.utrack.data.app.AppData;
import edu.utrack.data.app.AppEvent;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.database.Database;
import edu.utrack.util.DataClass;

public class GoalActivityData extends DataClass {

    private int totalAppTime, uniqueApps, screenOns, totalEventTime;
    private double pcntAppTime = -1, screenOnsPerHour = -1;

    /**
     * @param totalAppTime in seconds
     * @param totalEventTime in seconds
     */
    public GoalActivityData(int totalAppTime, int uniqueApps, int screenOns, int totalEventTime) {
        this.totalAppTime = totalAppTime;
        this.uniqueApps = uniqueApps;
        this.screenOns = screenOns;
        this.totalEventTime = totalEventTime;

        if(totalEventTime != 0) {
            pcntAppTime = (totalAppTime / (double) totalEventTime) * 100;
            screenOnsPerHour = (screenOns / (totalEventTime / 3600.0));
        }
    }

    public static GoalActivityData create(List<CalendarEvent> events, List<AppEvent> appEvents, int screenOns, long start, long end) {
        long totalAppTime = 0;
        Set<AppData> uniqueApps = new HashSet<>();
        for(AppEvent event : appEvents) {
            totalAppTime += event.getDuration(start, end);
            uniqueApps.add(event.getApp());
        }

        long totalEventTime = 0;
        for(CalendarEvent event : events) {
            totalEventTime += event.getDuration();
        }
        return new GoalActivityData((int) (totalAppTime / 1000L), uniqueApps.size(), screenOns, (int) (totalEventTime / 1000L));
    }

    public static GoalActivityData create(List<CalendarEvent> events, Database database, long start, long end) {
        int screenOns = database.getScreenEventsTable().getScreenOns(events);
        List<AppEvent> appEvents = database.getAppEventsTable().getEvents(events);

        return create(events, appEvents, screenOns, start, end);
    }

    /**
     * @return Total time spent in apps, in seconds
     */
    public int getTotalAppTime() {
        return totalAppTime;
    }

    /**
     * @return Percentage time of totalAppTime / totalEventTime. Returns -1 if there is no total event time
     */
    public double getPercentageAppTime() {
        return pcntAppTime;
    }

    public int getUniqueApps() {
        return uniqueApps;
    }

    public int getScreenOns() {
        return screenOns;
    }

    /**
     * @return Number of screen ons per hour of total event time. Returns -1 if there is no total event time
     */
    public double getScreenOnsPerHour() {
        return screenOnsPerHour;
    }

    /**
     * @return Total time of all events for this goal, in seconds
     */
    public int getTotalEventTime() {
        return totalEventTime;
    }

    @Override
    protected String[] getFieldNames() {
        return new String[] {"appTime", "pctAppTime", "uniqueApps", "screenOns", "screenOnsPerHr", "eventTime"};
    }

    @Override
    protected Object[] getFields() {
        return new Object[] {totalAppTime, pcntAppTime, uniqueApps, screenOns, screenOnsPerHour, totalEventTime};
    }
}
