package edu.utrack.activity;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

import edu.utrack.R;
import edu.utrack.activity.MonitorActivity;
import edu.utrack.activity.ReloadingActivity;
import edu.utrack.activity.TrackActivity;
import edu.utrack.calendar.CalendarHelper;
import edu.utrack.data.calendar.CalendarData;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.settings.AppSettings;
import edu.utrack.util.Callback;

public class Reloader {

    private TrackActivity activity;
    private ReloadingActivity reloader;

    private boolean reloading = false;

    public Reloader(TrackActivity activity, ReloadingActivity reloader) {
        this.activity = activity;
        this.reloader = reloader;
    }

    public void reload() {
        if(reloading) return;
        if(activity instanceof MonitorActivity) {
            MonitorActivity monitorActivity = (MonitorActivity) activity;
            if(monitorActivity.getConnection() == null || !monitorActivity.getConnection().isConnected()) return;
        }
        reloading = true;
        reloader.setMessage(reloader.getReloadingMessage());
        reloader.setContentVisible(false);
        activity.invalidateOptionsMenu();

        reloader.handleReload();
    }

    public void requestCalendarEvents(Callback<List<CalendarEvent>> callback, long begin, long end) {
        CalendarHelper helper = activity.getCalendarHelper();
        AppSettings settings = activity.getSettings();

        int calendarID = settings.currentCalendarID;
        if(calendarID < 0) {
            finishReload("No Calendar is selected.\nGo to the settings menu to select a calendar");
            return;
        }
        helper.requestCalendars(activity, (calendars) -> {
            if(calendars == null) {
                finishReload("You must allow this app to access your calendar!");
                return;
            }
            CalendarData data = getCalendar(calendars, calendarID);
            if(data == null) {
                finishReload("The Calendar used no longer exists. Please select a different calendar in settings.");
                settings.currentCalendarID = -1;
                settings.save();
                return;
            }
            helper.requestEvents(activity, data, begin, end, (events) -> {
                if(events == null) {
                    finishReload("You must allow this app to access your calendar!");
                    return;
                }
                Collections.sort(events, (e1, e2) -> Long.compare(e1.getStartTime(), e2.getStartTime()) * -1);
                callback.callback(events);
            });
        });
    }

    private CalendarData getCalendar(List<CalendarData> calendars, int id) {
        for(CalendarData data : calendars) {
            if (data.getDBID() == id) return data;
        }
        return null;
    }

    public void addMenuItems(Map<Integer, Runnable> menus) {
        if(reloading) menus.put(R.id.menuReloading, null);
        else menus.put(R.id.menuReload, this::reload);
    }

    public void finishReload(String errorMessage) {
        activity.runOnUiThread(() -> {
            reloading = false;
            activity.invalidateOptionsMenu();

            reloader.setMessage(errorMessage);
            if(errorMessage == null) reloader.setContentVisible(true);
        });
    }
}
