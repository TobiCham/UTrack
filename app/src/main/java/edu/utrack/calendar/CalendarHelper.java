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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Tobi on 06/03/2018.
 */

public class CalendarHelper {

    private Activity activity;

    private int counter;
    private Map<Integer, CalendarDataCallback> requests = new HashMap<>();

    public CalendarHelper(Activity activity) {
        this.activity = activity;
    }

    public synchronized void requestCalendars(CalendarDataCallback callback) {
        if (Build.VERSION.SDK_INT >= 23 && ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_CALENDAR) != PackageManager.PERMISSION_GRANTED) {
            requests.put(counter, callback);
            activity.requestPermissions(new String[] {Manifest.permission.READ_CALENDAR}, counter);
            counter++;
        } else {
            callback.onReceived(queryCalendars());
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
            data.add(new CalendarData(cursor.getString(1), cursor.getString(2), cursor.getString(3)));
        }
        return data;
    }

    public void onPermissionResult(int requestCode, String[] permissions, int[] grantResults) {
        CalendarDataCallback callback = requests.get(requestCode);
        if(callback == null) return;

        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            callback.onReceived(queryCalendars());
        } else {
            callback.onReceived(null);
        }
    }

    @FunctionalInterface
    public interface CalendarDataCallback {
        void onReceived(List<CalendarData> data);
    }
}
