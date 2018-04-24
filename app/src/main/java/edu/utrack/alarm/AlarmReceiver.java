package edu.utrack.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import edu.utrack.activity.TrackActivity;
import edu.utrack.calendar.CalendarHelper;
import edu.utrack.data.calendar.CalendarData;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.database.Database;
import edu.utrack.goals.GoalActivityData;
import edu.utrack.goals.GoalManager;
import edu.utrack.goals.GoalType;
import edu.utrack.goals.active.ActiveObjective;
import edu.utrack.goals.archived.ArchivedGoal;
import edu.utrack.goals.archived.ArchivedObjective;
import edu.utrack.settings.AppSettings;
import edu.utrack.settings.EventExcluder;

public class AlarmReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        AppSettings settings = TrackActivity.createSettings(context);
        EventExcluder excluder = TrackActivity.createExcluder(context);
        GoalManager goalManager = TrackActivity.createGoalManager(context);

        CalendarHelper helper = new CalendarHelper();
        List<CalendarData> calendars = helper.queryCalendars(context);
        CalendarData currentCalendar = null;
        for(CalendarData cal : calendars) {
            if(cal.getDBID() == settings.currentCalendarID && settings.currentCalendarID >= 0) currentCalendar = cal;
        }
        if(currentCalendar == null) {
            return;
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(getStartOfLastDay());
        boolean isWeekly = calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY;

        long startDay = getStartOfDay();
        long startLastDay = calendar.getTimeInMillis();

        calendar.add(Calendar.WEEK_OF_YEAR, -1);
        long startWeek = calendar.getTimeInMillis();

        List<CalendarEvent> dayEvents = helper.queryEvents(context, currentCalendar, startLastDay, startDay);
        excluder.removeExlucedEvents(dayEvents);

        List<CalendarEvent> weekEvents = new ArrayList<>();

        if(isWeekly) {
            weekEvents.addAll(helper.queryEvents(context, currentCalendar, startWeek, startDay));
            excluder.removeExlucedEvents(weekEvents);
        }

        Database db = new Database(context);

        archiveGoal(goalManager, dayEvents, db, GoalType.DAILY, startLastDay, startDay);
        if(isWeekly) {
            archiveGoal(goalManager, weekEvents, db, GoalType.WEEKLY, startWeek, startDay);
        }
        db.close();
    }

    private void archiveGoal(GoalManager goalManager, List<CalendarEvent> events, Database database, GoalType type, long start, long end) {
        events = new ArrayList<>(events);

        GoalActivityData data = GoalActivityData.create(events, database, start, end);
        int completedObjectives = 0;

        List<ArchivedObjective> objectives = new ArrayList<>();
        for(ActiveObjective objective : goalManager.getGoal(type).getObjectives()) {
            ArchivedObjective archivedObjective = new ArchivedObjective(objective, data);
            if(archivedObjective.getCompletedState() > 0) completedObjectives++;

            objectives.add(archivedObjective);
        }
        if(type == GoalType.WEEKLY) completedObjectives *= 5;

        goalManager.addArchivedGoal(new ArchivedGoal(type, objectives, start, end, data));

        if(completedObjectives != 0) {
            goalManager.setTrophies(goalManager.getTrophies() + completedObjectives);
        }
    }

    private long getStartOfDay() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.setTimeInMillis(getMidnight());

        if(hour >= 17) calendar.add(Calendar.DAY_OF_YEAR, 1);
        return calendar.getTimeInMillis();
    }

    private long getStartOfLastDay() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        calendar.setTimeInMillis(getMidnight());

        //As the alarm is inaccurate, if it goes off past midnight then the last day's events should
        //be requested (this usually should happen). However, if it goes off before midnight (early),
        //it should instead get events from today
        if(hour >= 0 && hour < 17) {
            calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - 1);
        }
        return calendar.getTimeInMillis();
    }

    private long getMidnight() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MINUTE, 0);

        return calendar.getTimeInMillis();
    }
}
