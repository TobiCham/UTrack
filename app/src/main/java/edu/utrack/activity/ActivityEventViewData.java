package edu.utrack.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utrack.R;
import edu.utrack.data.app.AppEvent;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.data.screen.ScreenEvent;
import edu.utrack.data.screen.ScreenEventType;
import edu.utrack.database.Database;
import edu.utrack.monitor.MonitorConnection;
import edu.utrack.monitor.MonitorService;
import edu.utrack.util.AppUtils;

/**
 * Created by Tobi on 24/03/2018.
 */

public class ActivityEventViewData extends Activity {

    private CalendarEvent event;
    private MonitorConnection connection;

    //1 = startTime, -1 = -startTime, 2 = appname, -2 = -appname, 3 = duration, 3 = -duration
    private int sortDirection = 0;
    private List<AppEvent> appEvents = new ArrayList<>();
    private Map<String, String> appNames = new HashMap<>();

    private static final DateFormat START_DATE_FORMAT = new SimpleDateFormat("dd/MM HH:mm");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_data_view);

        event = new Gson().fromJson(getIntent().getStringExtra("event"), CalendarEvent.class);

        String description = event.getTitle() + " - " + event.getLocation() + "\n" + START_DATE_FORMAT.format(new Date(event.getStartTime())) + " - " + START_DATE_FORMAT.format(new Date(event.getEndTime()));
        ((TextView) findViewById(R.id.summaryEventInfo)).setText(description);

        findViewById(R.id.summaryButtonUpdate).setOnClickListener((v) -> updateData());
        findViewById(R.id.summaryColumnTime).setOnClickListener((v) -> changeSortDirection(1));
        findViewById(R.id.summaryColumnApp).setOnClickListener((v) -> changeSortDirection(2));
        findViewById(R.id.summaryColumnDuration).setOnClickListener((v) -> changeSortDirection(3));

        updateData();
    }

    private void changeSortDirection(int type) {
        if(Math.abs(sortDirection) == type) sortDirection *= -1;
        else {
            int multiplier = sortDirection >= 0 ? 1 : -1;
            this.sortDirection = type * multiplier;
        }
        sortEvents();
        updateEvents();
    }

    @Override
    protected void onResume() {
        super.onResume();

        connection = new MonitorConnection(this::updateData);
        startService(new Intent(this, MonitorService.class));
        bindService(new Intent(this, MonitorService.class), connection, Context.BIND_ABOVE_CLIENT);
    }

    private void updateData() {
        setMessage("Loading Data...");
        setButtonEnabled(false);
        setScrollViewEnabled(false);

        if(connection == null || !connection.isConnected()) return;

        Database db = connection.getDatabase();
        new Thread(() -> {
            List<AppEvent> appEvents = db.getAppEventsTable().getEvents(event);
            Map<ScreenEventType, List<ScreenEvent>> screenEvents = db.getScreenEventsTable().getScreenCounts(event);
            runOnUiThread(() -> updateUI(appEvents, screenEvents));
        }).start();
    }

    private void updateUI(List<AppEvent> appEvents, Map<ScreenEventType, List<ScreenEvent>> screenEvents) {
        if(hasNoEvents(appEvents, screenEvents)) {
            setMessage("No data has been found for this event.");
            setButtonEnabled(true);
            return;
        }

        //Update UI

        setMessage(null);
        setButtonEnabled(true);
        setScrollViewEnabled(true);

        ((TextView) findViewById(R.id.summaryScreenOffTotal)).setText(Integer.toString(screenEvents.get(ScreenEventType.OFF).size()));
        ((TextView) findViewById(R.id.summaryScreenOnTotal)).setText(Integer.toString(screenEvents.get(ScreenEventType.ON).size()));
        ((TextView) findViewById(R.id.summaryUnlockTotal)).setText(Integer.toString(screenEvents.get(ScreenEventType.UNLOCK).size()));

        this.appEvents = appEvents;
        this.appNames.clear();
        for(AppEvent appEvent : appEvents) {
            String pName = appEvent.getApp().getPackageName();
            if(!appNames.containsKey(pName)) appNames.put(pName, AppUtils.getAppName(pName, this));
        }
        sortEvents();
        updateEvents();
    }

    private void sortEvents() {
        int multiplier = sortDirection >= 0 ? 1 : -1;
        if(Math.abs(sortDirection) == 3) {
            Collections.sort(appEvents, (e1, e2) -> Long.compare(e1.getDuration(this.event), e2.getDuration(this.event)) * multiplier * -1);
        } else if(Math.abs(sortDirection) == 2) {
            Collections.sort(appEvents, (e1, e2) -> appNames.get(e1.getApp().getPackageName()).compareTo(appNames.get(e2.getApp().getPackageName())) * multiplier);
        } else {
            Collections.sort(appEvents, (e1, e2) -> Long.compare(e1.getStartTime(this.event), e2.getStartTime(this.event)) * multiplier);
        }
    }

    private void updateEvents() {
        TableLayout tableLayout = findViewById(R.id.summaryAppTable);
        if(tableLayout.getChildCount() > 1) {
            tableLayout.removeViews(1, tableLayout.getChildCount() - 1);
        }

        for(AppEvent event : appEvents) {
            TableRow row = new TableRow(this);
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            TextView appText = new TextView(this);
            appText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            String name = appNames.get(event.getApp().getPackageName());
            appText.setText(name);

            TextView startTimeText = new TextView(this);
            startTimeText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            startTimeText.setPadding(getPaddingPX(20), 0, 0, 0);
            startTimeText.setText(START_DATE_FORMAT.format(new Date(event.getStartTime(this.event))));

            TextView durationText = new TextView(this);
            durationText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            durationText.setPadding(getPaddingPX(20), 0, 0, 0);

            int totalSeconds = (int) (event.getDuration(this.event) / 1000);
            int seconds = totalSeconds % 60;
            int minutes = totalSeconds / 60;
            int hours = totalSeconds / (60 * 60);

            String timeText = "";
            if(hours > 0) timeText += hours + "h, ";
            if(minutes > 0) timeText += minutes + "m, ";
            timeText += seconds + "s";

            durationText.setText(timeText);

            appText.setOnClickListener((v) -> changeSortDirection(2));
            startTimeText.setOnClickListener((v) -> changeSortDirection(1));
            durationText.setOnClickListener((v) -> changeSortDirection(3));

            row.addView(appText);
            row.addView(startTimeText);
            row.addView(durationText);
            tableLayout.addView(row);
        }
    }

    private boolean hasNoEvents(List<AppEvent> appEvents, Map<ScreenEventType, List<ScreenEvent>> screenEvents) {
        if(!appEvents.isEmpty()) return false;
        for(List<ScreenEvent> list : screenEvents.values()) {
            if(!list.isEmpty()) return false;
        }
        return true;
    }

    private void setButtonEnabled(boolean enabled) {
        findViewById(R.id.summaryButtonUpdate).setEnabled(enabled);
    }

    private void setScrollViewEnabled(boolean enabled) {
        findViewById(R.id.summaryScrollView).setVisibility(enabled ? TextView.VISIBLE : TextView.INVISIBLE);
    }

    private void setMessage(String msg) {
        TextView view = findViewById(R.id.summaryInfo);
        if(msg != null) {
            view.setText(msg);
            view.setVisibility(TextView.VISIBLE);
        }
        else {
            view.setText("");
            view.setVisibility(TextView.GONE);
        }
    }

    private int getPaddingPX(int dp) {
        return (int) (dp * getResources().getDisplayMetrics().density + 0.5f);
    }

    @Override
    protected void onPause() {
        super.onPause();
        unbindService(connection);
        connection = null;
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}
