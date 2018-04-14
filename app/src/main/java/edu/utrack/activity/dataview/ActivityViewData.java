package edu.utrack.activity.dataview;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import edu.utrack.R;
import edu.utrack.activity.MonitorActivity;
import edu.utrack.data.app.AppEvent;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.data.screen.ScreenEvent;
import edu.utrack.data.screen.ScreenEventType;
import edu.utrack.database.Database;
import edu.utrack.settings.EventExcluder;

/**
 * Created by Tobi on 24/03/2018.
 */

public class ActivityViewData extends MonitorActivity {

    private CalendarEvent event;
    private ViewPager pager;
    private TabLayout tabLayout;

    private boolean reloading = false;

    private List<DataViewFragment> fragments = new ArrayList<>();

    private static final DateFormat START_DATE_FORMAT = new SimpleDateFormat("dd/MM HH:mm");

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_data_view);

        event = new Gson().fromJson(getIntent().getStringExtra("event"), CalendarEvent.class);

        String description = event.getTitle() + " - " + event.getLocation() + "\n" + START_DATE_FORMAT.format(new Date(event.getStartTime())) + " - " + START_DATE_FORMAT.format(new Date(event.getEndTime()));
        ((TextView) findViewById(R.id.eventViewInfo)).setText(description);

        fragments.add(new FragmentDataSummary());
        fragments.add(new FragmentDataGraphs());

        for(DataViewFragment fragment : fragments) fragment.setEvent(event);

        pager = findViewById(R.id.eventViewPager);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return 2;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if(position == 0) return "Summary";
                return "Graphs";
            }
        });

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = findViewById(R.id.eventViewTabs);
        tabLayout.setupWithViewPager(pager);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateData();
    }

    @Override
    public void getMenuItems(Map<Integer, Runnable> menus) {
        super.getMenuItems(menus);

        if(reloading) menus.put(R.id.menuReloading, null);
        else menus.put(R.id.menuReload, this::updateData);

        menus.put(getEventExcluder().isEventExcluded(event) ? R.id.menuCross : R.id.menuCheck, this::changeExclusion);
    }

    @Override
    public TrackMenuType getMenuType() {
        return TrackMenuType.CUSTOM;
    }

    @Override
    public void onConnected() {
        updateData();
    }

    private void updateData() {

        if(getConnection() == null || !getConnection().isConnected()) return;

        if(reloading) return;
        reloading = true;

        setMessage("Loading Data...");
        setHistoricButtonEnabled(false);
        setContentVisible(false);
        invalidateOptionsMenu();

        Database db = getConnection().getDatabase();
        new Thread(() -> {
            List<AppEvent> appEvents = db.getAppEventsTable().getEvents(event);
            Map<ScreenEventType, List<ScreenEvent>> screenEvents = db.getScreenEventsTable().getScreenCounts(event);

            runOnUiThread(() -> updateUI(appEvents, screenEvents));
        }).start();
    }

    private void changeExclusion() {
        if(event == null) return;

        EventExcluder excluder = getEventExcluder();
        if(excluder.isEventExcluded(event)) {
            excluder.includeEvent(event);
            Toast.makeText(this, "Included '" + event.getTitle() + "' from historic data", Toast.LENGTH_SHORT).show();
        } else {
            excluder.excludeEvent(event);
            Toast.makeText(this, "Excluded '" + event.getTitle() + "' from historic data", Toast.LENGTH_SHORT).show();
        }
        invalidateOptionsMenu();
    }

    private void updateUI(List<AppEvent> appEvents, Map<ScreenEventType, List<ScreenEvent>> screenEvents) {
        System.out.println("update UI!!!");
        if(hasNoEvents(appEvents, screenEvents)) {
            finishReload("No data has been found for this event.");
            return;
        }
        //Update UI
        setContentVisible(true);
        finishReload(null);

        for(DataViewFragment fragment : fragments) fragment.updateUI(appEvents, screenEvents);
        setHistoricButtonEnabled(true);
    }

    private boolean hasNoEvents(List<AppEvent> appEvents, Map<ScreenEventType, List<ScreenEvent>> screenEvents) {
        if(!appEvents.isEmpty()) return false;
        for(List<ScreenEvent> list : screenEvents.values()) {
            if(!list.isEmpty()) return false;
        }
        return true;
    }

    private void setHistoricButtonEnabled(boolean enabled) {
        findViewById(R.id.eventViewButtonHistoricData).setEnabled(enabled);
    }

    private void setContentVisible(boolean visible) {
        findViewById(R.id.eventViewPager).setVisibility(visible ? ViewPager.VISIBLE : ViewPager.INVISIBLE);
        findViewById(R.id.eventViewTabs).setEnabled(visible);
        if(visible) findViewById(R.id.eventViewTabs).setVisibility(TableLayout.VISIBLE);
    }

    private void finishReload(String message) {
        setMessage(message);
        reloading = false;
        invalidateOptionsMenu();
    }

    private void setMessage(String msg) {
        TextView view = findViewById(R.id.eventViewMessage);
        if(msg != null) {
            view.setText(msg);
            view.setVisibility(TextView.VISIBLE);
        }
        else {
            view.setText("");
            view.setVisibility(TextView.GONE);
        }
    }
}
