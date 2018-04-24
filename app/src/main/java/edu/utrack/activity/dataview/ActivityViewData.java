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
import edu.utrack.activity.Reloader;
import edu.utrack.activity.ReloadingActivity;
import edu.utrack.data.app.AppEvent;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.data.screen.ScreenEvent;
import edu.utrack.data.screen.ScreenEventType;
import edu.utrack.database.Database;
import edu.utrack.settings.EventExcluder;

/**
 * Created by Tobi on 24/03/2018.
 */

public class ActivityViewData extends MonitorActivity implements ReloadingActivity {

    private CalendarEvent event;
    private Reloader reloader;

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

        ViewPager pager = findViewById(R.id.eventViewPager);
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

        Toolbar toolbar = findViewById(R.id.eventViewToolbar);
        toolbar.setTitle("Details");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        TabLayout tabLayout = findViewById(R.id.eventViewTabs);
        tabLayout.setupWithViewPager(pager);

        reloader = new Reloader(this, this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        reloader.reload();
    }

    @Override
    public void getMenuItems(Map<Integer, Runnable> menus) {
        super.getMenuItems(menus);
        reloader.addMenuItems(menus);

        menus.put(getEventExcluder().isEventExcluded(event) ? R.id.menuCross : R.id.menuCheck, this::changeExclusion);
    }

    @Override
    public TrackMenuType getMenuType() {
        return TrackMenuType.CUSTOM;
    }

    @Override
    public void onConnected() {
        reloader.reload();
    }

    @Override
    public void handleReload() {
        Database db = getConnection().getDatabase();
        if(db == null) {
            reloader.finishReload("Unable to access database. Try again");
            return;
        }
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
            Toast.makeText(this, "Included '" + event.getTitle() + "' to Goals", Toast.LENGTH_SHORT).show();
        } else {
            excluder.excludeEvent(event);
            Toast.makeText(this, "Excluded '" + event.getTitle() + "' from Goals", Toast.LENGTH_SHORT).show();
        }
        invalidateOptionsMenu();
    }

    private void updateUI(List<AppEvent> appEvents, Map<ScreenEventType, List<ScreenEvent>> screenEvents) {
        if(hasNoEvents(appEvents, screenEvents)) {
            reloader.finishReload("No data has been found for this event.");
            return;
        }
        for(DataViewFragment fragment : fragments) fragment.updateUI(appEvents, screenEvents);

        //Update UI
        reloader.finishReload(null);
    }

    private boolean hasNoEvents(List<AppEvent> appEvents, Map<ScreenEventType, List<ScreenEvent>> screenEvents) {
        if(!appEvents.isEmpty()) return false;
        for(List<ScreenEvent> list : screenEvents.values()) {
            if(!list.isEmpty()) return false;
        }
        return true;
    }

    @Override
    public void setContentVisible(boolean visible) {
        findViewById(R.id.eventViewPager).setVisibility(visible ? ViewPager.VISIBLE : ViewPager.INVISIBLE);
        findViewById(R.id.eventViewTabs).setEnabled(visible);
        if(visible) findViewById(R.id.eventViewTabs).setVisibility(TableLayout.VISIBLE);
    }

    @Override
    public String getReloadingMessage() {
        return "Loading data...";
    }

    @Override
    public void setMessage(String msg) {
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
