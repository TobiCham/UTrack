package edu.utrack.activity.dataview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import edu.utrack.util.AppUtils;

/**
 * Created by Tobi on 29/03/2018.
 */

public class FragmentDataSummary extends DataViewFragment {

    //1 = startTime, -1 = -startTime, 2 = appname, -2 = -appname, 3 = duration, 3 = -duration
    private int sortDirection = 0;
    private List<AppEvent> appEvents = new ArrayList<>();
    private Map<String, String> appNames = new HashMap<>();

    private static final DateFormat START_DATE_FORMAT = new SimpleDateFormat("dd/MM HH:mm");

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_data_summary, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().findViewById(R.id.summaryColumnTime).setOnClickListener((v) -> changeSortDirection(1));
        getView().findViewById(R.id.summaryColumnApp).setOnClickListener((v) -> changeSortDirection(2));
        getView().findViewById(R.id.summaryColumnDuration).setOnClickListener((v) -> changeSortDirection(3));
    }

    @Override
    public void updateUI(List<AppEvent> appEvents, Map<ScreenEventType, List<ScreenEvent>> screenEvents) {
        ((TextView) getView().findViewById(R.id.summaryScreenOffTotal)).setText(Integer.toString(screenEvents.get(ScreenEventType.OFF).size()));
        ((TextView) getView().findViewById(R.id.summaryScreenOnTotal)).setText(Integer.toString(screenEvents.get(ScreenEventType.ON).size()));
        ((TextView) getView().findViewById(R.id.summaryUnlockTotal)).setText(Integer.toString(screenEvents.get(ScreenEventType.UNLOCK).size()));

        this.appEvents = appEvents;
        this.appNames.clear();
        for(AppEvent appEvent : appEvents) {
            String pName = appEvent.getApp().getPackageName();
            if(!appNames.containsKey(pName)) appNames.put(pName, AppUtils.getAppName(pName, getActivity()));
        }
        sortEvents();
        updateEvents();
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
        TableLayout tableLayout = getView().findViewById(R.id.summaryAppTable);
        if(tableLayout.getChildCount() > 1) {
            tableLayout.removeViews(1, tableLayout.getChildCount() - 1);
        }

        for(AppEvent event : appEvents) {
            TableRow row = new TableRow(getActivity());
            row.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.MATCH_PARENT, TableLayout.LayoutParams.WRAP_CONTENT));

            TextView appText = new TextView(getActivity());
            appText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

            String name = appNames.get(event.getApp().getPackageName());
            appText.setText(name);

            TextView startTimeText = new TextView(getActivity());
            startTimeText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            startTimeText.setPadding(AppUtils.getPaddingPX(getActivity(), 20), 0, 0, 0);
            startTimeText.setText(START_DATE_FORMAT.format(new Date(event.getStartTime(this.event))));

            TextView durationText = new TextView(getActivity());
            durationText.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));
            durationText.setPadding(AppUtils.getPaddingPX(getActivity(), 20), 0, 0, 0);

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
}
