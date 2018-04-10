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
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utrack.data.calendar.CalendarData;
import edu.utrack.data.calendar.CalendarEvent;

/**
 * Created by Tobi on 06/03/2018.
 */

public class CalendarHelper {

    private Activity activity;

    private int callbackCounter;
    private Map<Integer, Pair<CalendarCallback, Object>> callbacks = new HashMap<>();

    //7 weeks
    private static final long TIME_DIFFERENCE = 4L * 7L * 24L * 60L * 60L * 1000L;

    public CalendarHelper(Activity activity) {
        this.activity = activity;
    }

    public synchronized void requestCalendars(CalendarDataCallback callback) {
        if (checkPermission(callback, null)) {
            callback.onReceived(queryCalendars());
        }
    }

    public synchronized void requestEvents(CalendarData calendar, CalendarEventCallback callback) {
       if(checkPermission(callback, new Object[] {calendar, TIME_DIFFERENCE, TIME_DIFFERENCE})) {
           callback.onReceived(queryEvents(calendar, TIME_DIFFERENCE, TIME_DIFFERENCE));
       }
    }

    public CalendarData getMyTimetableData(List<CalendarData> list) {
        for(CalendarData d : list) {
            if(d.getName().toLowerCase().startsWith("university of bath personal timetable")) return d;
        }
        return null;
    }

    /**
     * Be careful using this method - if the app does not have permission, an exception will be thrown
     * @return a list of all calendars
     */
    public List<CalendarData> queryCalendars() {
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
        cursor.close();
        return data;
    }

    /**
     * Be careful using this method - if the app does not have permission, an exception will be thrown
     * @param begin Minimum time to start getting events from - as an offset from the current time
     * @param end Maximum time to get events from - as an offset from the current time
     * @return A list of Calendar events in the specified time frame
     */
    public List<CalendarEvent> queryEvents(CalendarData calendar, long begin, long end) {
        ContentResolver contentResolver = activity.getContentResolver();
        Uri uri = CalendarContract.Events.CONTENT_URI;

        String[] qry = {
            CalendarContract.Events._ID,
            CalendarContract.Events.TITLE,
            CalendarContract.Events.EVENT_LOCATION,
            CalendarContract.Events.DTSTART,
            CalendarContract.Events.DTEND
        };

        long currentTime = System.currentTimeMillis();
        long startTime = begin >= 0 ? currentTime - begin : 0;
        long endTime = end >= 0 ? currentTime + end : Long.MAX_VALUE;

        String selection = "(" + CalendarContract.Events.CALENDAR_ID + "=? AND " + CalendarContract.Events.DTSTART + " >= ? AND " + CalendarContract.Events.DTEND + " <= ?)";
        @SuppressLint("MissingPermission")
        Cursor cursor = contentResolver.query(uri, qry, selection, new String[] {calendar.getDBID() + "", startTime + "", endTime + ""}, null);
        List<CalendarEvent> events = new ArrayList<>();

        while(cursor.moveToNext()) {
            events.add(new CalendarEvent(calendar, cursor.getInt(0), cursor.getString(1), cursor.getString(2), cursor.getLong(3), cursor.getLong(4)));
        }
        Collections.sort(events, (e1, e2) -> Long.compare(e1.getStartTime(), e2.getStartTime()));
        cursor.close();
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
            if(granted) {
                Object[] arr = (Object[]) data;
                ((CalendarEventCallback) callback).onReceived(queryEvents((CalendarData) arr[0], (long) arr[1], (long) arr[2]));
            }
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
