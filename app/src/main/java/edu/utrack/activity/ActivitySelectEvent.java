package edu.utrack.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.utrack.R;
import edu.utrack.activity.dataview.ActivityViewData;
import edu.utrack.activity.goals.ActivityGoals;
import edu.utrack.data.calendar.CalendarData;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.util.AppUtils;

/**
 * Created by Tobi on 24/03/2018.
 */

public class ActivitySelectEvent extends TrackActivity implements ReloadingActivity {

    private Reloader reloader;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.simple_calendar_view);

        reloader = new Reloader(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        reloader.reload();
    }

    @Override
    public TrackMenuType getMenuType() {
        return TrackMenuType.MENU;
    }

    @Override
    public void getMenuItems(Map<Integer, Runnable> menus) {
        super.getMenuItems(menus);
        reloader.addMenuItems(menus);
        menus.put(R.id.menuGoals, this::showGoals);
    }

    private void showGoals() {
        startActivity(new Intent(this, ActivityGoals.class));
    }

    private void eventClicked(TextView view, CalendarEvent event) {
        Intent intent = new Intent(this, ActivityViewData.class);
        intent.putExtra("event", new Gson().toJson(event));
        startActivity(intent);
    }

    @Override
    public void handleReload() {
        LinearLayout layout = findViewById(R.id.calendarLayoutList);
        layout.removeAllViews();

        new Thread(() -> {
            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.WEEK_OF_YEAR, -4);

            reloader.requestCalendarEvents(this::updateEvents, calendar.getTimeInMillis(), System.currentTimeMillis());
        }).start();
    }

    @Override
    public String getReloadingMessage() {
        return "Loading Events...";
    }

    private void updateEvents(List<CalendarEvent> events) {
        if(events.isEmpty()) {
            reloader.finishReload("No calendar events on that calendar!");
            return;
        }

        if(getSettings().developer) {
            events.add(0, new CalendarEvent(new CalendarData(-1, "test@test.com", "test", "test"), -1, "All of Time Event", "????", 0, Long.MAX_VALUE));
            events.add(1, new CalendarEvent(new CalendarData(-1, "test@test.com", "test", "test"), -2, "Past 24 hrs", "???", System.currentTimeMillis() - (24 * 60 * 60 * 1000), System.currentTimeMillis()));
        }

        runOnUiThread(() -> {
            LinearLayout layout = findViewById(R.id.calendarLayoutList);

            for(int i = 0; i < events.size(); i++) {
                CalendarEvent event = events.get(i);
                layout.addView(createText(event, i % 2 == 0));
            }
            reloader.finishReload("Select an event");
        });
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

    @Override
    public void setMessage(String msg) {
        ((TextView) findViewById(R.id.calendarViewMessage)).setText(msg);
    }

    @Override
    public void setContentVisible(boolean visible) { }
}
