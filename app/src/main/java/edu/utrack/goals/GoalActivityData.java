package edu.utrack.goals;

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
