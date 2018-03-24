package edu.utrack.activity;

import android.app.Activity;
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

import edu.utrack.R;
import edu.utrack.calendar.CalendarHelper;
import edu.utrack.data.calendar.CalendarData;
import edu.utrack.data.calendar.CalendarEvent;

/**
 * Created by Tobi on 24/03/2018.
 */

public class ActivitySelectEvent extends Activity {

    private CalendarHelper calendarHelper;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_calendar_view);

        calendarHelper = new CalendarHelper(this);
        updateCalendar();

        findViewById(R.id.calendarButtonRefreshEvents).setOnClickListener((e) -> updateCalendar());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        calendarHelper.onPermissionResult(requestCode, permissions, grantResults);
    }

    private void eventClicked(TextView view, CalendarEvent event) {
        Intent intent = new Intent(this, ActivityEventViewData.class);
        intent.putExtra("event", new Gson().toJson(event));
        startActivity(intent);
    }

    private void updateCalendar() {
        setMessage("Loading Events...");
        setButtonEnabled(false);

        ((LinearLayout) findViewById(R.id.calendarLayoutList)).removeAllViews();

        new Thread(() -> calendarHelper.requestCalendars(this::onGetCalendars)).start();
    }

    private void onGetCalendars(List<CalendarData> calendars) {
        if(calendars == null) {
            runOnUiThread(() -> {
                setMessage("You must allow this app to access your calendar!");
                setButtonEnabled(true);
            });
            return;
        }
        CalendarData myTimetable = calendarHelper.getMyTimetableData(calendars);
        if(myTimetable != null) {
            calendarHelper.requestEvents(myTimetable, this::onGetEvents);
        }
    }

    private void onGetEvents(List<CalendarEvent> events) {
        if(events == null) {
            runOnUiThread(() -> {
                setMessage("You must allow this app to access your calendar events!");
                setButtonEnabled(true);
            });
            return;
        }
        List<CalendarEvent> newEvents = new ArrayList<>();
        for(CalendarEvent event : events) {
            if(event.getStartTime() <= System.currentTimeMillis()) newEvents.add(event);
        }
        //TODO Remove
        Collections.sort(newEvents, (e1, e2) -> Long.compare(e1.getStartTime(), e2.getStartTime()) * -1);

        newEvents.add(0, new CalendarEvent(null, -1, "All of Time Event", "????", 0, Long.MAX_VALUE));

        runOnUiThread(() -> updateEvents(newEvents));
    }

    private void updateEvents(List<CalendarEvent> events) {
        setButtonEnabled(true);

        if(events.isEmpty()) {
            setMessage("No calendar events to show!");
            return;
        }
        LinearLayout layout = findViewById(R.id.calendarLayoutList);
        layout.removeAllViews();

        for(int i = 0; i < events.size(); i++) {
            CalendarEvent event = events.get(i);
            layout.addView(createText(event, i % 2 == 0));
        }
        setMessage("Select an event");
    }

    private TextView createText(CalendarEvent event, boolean dark) {
        SimpleDateFormat startFormat = new SimpleDateFormat("dd/MM HH:mm");

        TextView view = new TextView(this);
        String txt = event.getTitle() + ": " + event.getLocation();
        txt += "\n" + startFormat.format(new Date(event.getStartTime())) + ", " + startFormat.format(new Date(event.getEndTime()));
        view.setText(txt);
        view.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        view.setPadding(getPaddingPX(5), getPaddingPX(8), getPaddingPX(8), 0);
        view.setBackgroundColor(Color.parseColor(dark ? "#CCCCFF" : "#EEEEFF"));

        view.setOnClickListener((e) -> eventClicked(view, event));
        return view;
    }

    private int getPaddingPX(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    private void setButtonEnabled(boolean enabled) {
        findViewById(R.id.calendarButtonRefreshEvents).setEnabled(enabled);
    }

    private void setMessage(String msg) {
        ((TextView) findViewById(R.id.calendarViewMessage)).setText(msg);
    }
}
