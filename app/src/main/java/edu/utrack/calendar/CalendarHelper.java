package edu.utrack.calendar;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.CalendarContract;
import android.support.v4.app.ActivityCompat;
import android.util.Pair;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tobi on 06/03/2018.
 */

public class CalendarHelper {

    private Activity activity;

    private int callbackCounter;
    private Map<Integer, Pair<CalendarCallback, Object>> callbacks = new HashMap<>();

    public CalendarHelper(Activity activity) {
        this.activity = activity;
    }

    public synchronized void requestCalendars(CalendarDataCallback callback) {
        if (checkPermission(callback, null)) {
            callback.onReceived(queryCalendars());
        }
    }

    public synchronized void requestEvents(CalendarData calendar, CalendarEventCallback callback) {
       if(checkPermission(callback, calendar)) {
           callback.onReceived(queryEvents(calendar));
       }
    }

    public CalendarData getMyTimetableData(List<CalendarData> list) {
        for(CalendarData d : list) {
            if(d.getName().toLowerCase().startsWith("university of bath personal timetable")) return d;
        }
        return null;
    }

    private List<CalendarData> queryCalendars() {
        ContentResolver contentResolver = activity.getContentResolver();
        Uri uri = CalendarContract.Calendars.CONTENT_URI;
        String[] qry = {
            CalendarContract.Calendars._ID,
            CalendarContract.Calendars.ACCOUNT_NAME,
            CalendarContract.Calendars.CALENDAR_DISPLAY_NAME,
            CalendarContract.Calendars.OWNER_ACCOUNT
        };

        @SuppressLint("MissingPermission")
        Cursor cursor = contentResolver.query(uri, qry, "", new String[0], null);
        List<CalendarData> data = new ArrayList<>();

        while(cursor.moveToNext()) {
            data.add(new CalendarData(cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getString(3)));
        }
        return data;
    }

    private List<CalendarEvent> queryEvents(CalendarData calendar) {
        ContentResolver contentResolver = activity.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        String[] qry = {
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND,
            CalendarContract.Events.ALL_DAY
        };

        long currentTime = System.currentTimeMillis();
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.WEEK_OF_MONTH, 4);
        long expireTime = cal.getTimeInMillis();

        String selection = "(" + CalendarContract.Events.CALENDAR_ID + "=? AND " + CalendarContract.Events.DTEND + " > ? AND " + CalendarContract.Events.DTEND + " < ?)";
        @SuppressLint("MissingPermission")
        Cursor cursor = contentResolver.query(uri, qry, selection, new String[] {calendar.getDBID() + "", currentTime + "", expireTime + ""}, null);
        List<CalendarEvent> events = new ArrayList<>();

        while(cursor.moveToNext()) {
            events.add(new CalendarEvent(calendar, cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getLong(4), cursor.getInt(5) == 1));
        }
        Collections.sort(events, (e1, e2) -> Long.compare(e1.getStartTime(), e2.getStartTime()));
        return events;
    }

    public void onPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        boolean granted = grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED;

        Pair<CalendarCallback, Object> pair = callbacks.get(requestCode);
        if(pair == null) return;
        callbacks.remove(requestCode);

        CalendarCallback callback = pair.first;
        Object data = pair.second;

        if(callback instanceof CalendarDataCallback) {
            if(granted) ((CalendarDataCallback) callback).onReceived(queryCalendars());
            else ((CalendarDataCallback) callback).onReceived(null);
        }
        if(callback instanceof CalendarEventCallback) {
            if(granted) ((CalendarEventCallback) callback).onReceived(queryEvents((CalendarData) data));
            else ((CalendarEventCallback) callback).onReceived(null);
        }
    }

    private boolean checkPermission(CalendarCallback callback, Object data) {
        if(Build.VERSION.SDK_INT < 23) return true;
        if(ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) == PackageManager.PERMISSION_GRANTED) return true;

        callbacks.put(callbackCounter, new Pair(callback, data));
        activity.requestPermissions(new String[] {Manifest.permission.READ_CALENDAR}, callbackCounter);
        callbackCounter++;
        return false;
    }

    private interface CalendarCallback {}

    @FunctionalInterface
    public interface CalendarDataCallback extends CalendarCallback {
        void onReceived(List<CalendarData> data);
    }
    @FunctionalInterface
    public interface CalendarEventCallback extends CalendarCallback {
        void onReceived(List<CalendarEvent> events);
    }
}
