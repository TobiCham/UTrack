package edu.utrack.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.utrack.R;
import edu.utrack.activity.dataview.ActivityViewData;
import edu.utrack.data.calendar.CalendarData;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.settings.AppSettings;
import edu.utrack.util.AppUtils;

/**
 * Created by Tobi on 24/03/2018.
 */

public class ActivitySelectEvent extends TrackActivity {

    private boolean reloading = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_calendar_view);
    }

    @Override
    protected void onResume() {
        super.onResume();

        updateCalendar();
    }

    @Override
    public TrackMenuType getMenuType() {
        return TrackMenuType.MENU;
    }

    @Override
    public void getMenuItems(Map<Integer, Runnable> menus) {
        super.getMenuItems(menus);

        if(reloading) menus.put(R.id.menuReloading, null);
        else menus.put(R.id.menuReload, this::updateCalendar);
    }

    private void eventClicked(TextView view, CalendarEvent event) {
        Intent intent = new Intent(this, ActivityViewData.class);
        intent.putExtra("event", new Gson().toJson(event));
        startActivity(intent);
    }

    private void updateCalendar() {
        if(reloading) return;
        reloading = true;

        AppSettings settings = getSettings();
        int calendarID = settings.currentCalendarID;

        LinearLayout layout = findViewById(R.id.calendarLayoutList);
        layout.removeAllViews();

        if(calendarID < 0) {
            finishReload("No Calendar is selected.\nGo to the settings menu to select a calendar");
            return;
        }

        setMessage("Loading Events...");
        invalidateOptionsMenu();

        ((LinearLayout) findViewById(R.id.calendarLayoutList)).removeAllViews();

        new Thread(() -> getCalendarHelper().requestCalendars(this::onGetCalendars)).start();
    }

    private void onGetCalendars(List<CalendarData> calendars) {
        if(calendars == null) {
            finishReload("You must allow this app to access your calendar!");
            return;
        }
        int id = getSettings().currentCalendarID;
        for(CalendarData data : calendars) {
            if(data.getDBID() == id) {
                getCalendarHelper().requestEvents(data, this::onGetEvents);
                return;
            }
        }
        finishReload("The Calendar used no longer exists. Please select a different calendar in settings.");

        getSettings().currentCalendarID = -1;
        getSettings().save();
    }

    private void onGetEvents(List<CalendarEvent> events) {
        if(events == null) {
            finishReload("You must allow this app to access your calendar events!");
            return;
        }
        List<CalendarEvent> newEvents = new ArrayList<>();
        for(CalendarEvent event : events) {
            if(event.getStartTime() <= System.currentTimeMillis()) newEvents.add(event);
        }
        Collections.sort(newEvents, (e1, e2) -> Long.compare(e1.getStartTime(), e2.getStartTime()) * -1);

        if(getSettings().developer) {
            newEvents.add(0, new CalendarEvent(new CalendarData(-1, "test@test.com", "test", "test"), -1, "All of Time Event", "????", 0, Long.MAX_VALUE));
            newEvents.add(1, new CalendarEvent(new CalendarData(-1, "test@test.com", "test", "test"), -2, "Past 24 hrs", "???", System.currentTimeMillis() - (24 * 60 * 60 * 1000), System.currentTimeMillis()));
        }

        runOnUiThread(() -> updateEvents(newEvents));
    }

    private void updateEvents(List<CalendarEvent> events) {
        if(events.isEmpty()) {
            finishReload("No calendar events on that calendar!");
            return;
        }
        LinearLayout layout = findViewById(R.id.calendarLayoutList);

        for(int i = 0; i < events.size(); i++) {
            CalendarEvent event = events.get(i);
            layout.addView(createText(event, i % 2 == 0));
        }
        finishReload("Select an event");
    }

    private void finishReload(String message) {
        setMessage(message);
        reloading = false;
        invalidateOptionsMenu();
    }

    private TextView createText(CalendarEvent event, boolean dark) {
        SimpleDateFormat startFormat = new SimpleDateFormat("dd/MM HH:mm");

        TextView view = new TextView(this);
        String txt = event.getTitle() + ": " + event.getLocation();
        txt += "\n" + startFormat.format(new Date(event.getStartTime())) + " - " + startFormat.format(new Date(event.getEndTime()));
        view.setText(txt);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.setPadding(AppUtils.getPaddingPX(this, 5), AppUtils.getPaddingPX(this, 8), AppUtils.getPaddingPX(this, 8), 0);
        setTextBackground(dark, view, event);

        view.setOnClickListener((e) -> eventClicked(view, event));
        return view;
    }

    private void setTextBackground(boolean dark, TextView view, CalendarEvent event) {
        boolean excluded = getEventExcluder().isEventExcluded(event);
        if(dark) {
            view.setBackgroundColor(Color.parseColor(excluded ? "#FFCECE" : "#CCCCFF"));
        } else {
            view.setBackgroundColor(Color.parseColor(excluded ? "#FFEFEF" : "#EEEEFF"));
        }
    }

    private void setMessage(String msg) {
        ((TextView) findViewById(R.id.calendarViewMessage)).setText(msg);
    }
}
