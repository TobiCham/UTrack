package edu.utrack.activity.goals;

import android.os.Bundle;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.widget.Toolbar;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.utrack.R;
import edu.utrack.activity.MonitorActivity;
import edu.utrack.activity.Reloader;
import edu.utrack.activity.ReloadingActivity;
import edu.utrack.data.app.AppData;
import edu.utrack.data.app.AppEvent;
import edu.utrack.data.calendar.CalendarData;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.goals.Goal;
import edu.utrack.goals.GoalActivityData;
import edu.utrack.goals.GoalType;

public class ActivityGoals extends MonitorActivity implements ReloadingActivity {

    private Reloader reloader;
    private List<Fragment> fragments = new ArrayList<>();

    private ViewPager pager;
    private TabLayout tabLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.goals_view);

        for(GoalType type : GoalType.values()) {
            FragmentViewGoal frag = new FragmentViewGoal();
            frag.setGoalType(type);
            frag.setArchived(false);
            fragments.add(frag);
        }
        fragments.add(new FragmentArchivedGoals());

        for(Fragment fragment : fragments) fragment.setRetainInstance(true);

        pager = findViewById(R.id.goalsPager);
        pager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            @Override
            public android.support.v4.app.Fragment getItem(int position) {
                return fragments.get(position);
            }

            @Override
            public int getCount() {
                return 3;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                if(position == 0) return "Daily";
                if(position == 1) return "Weekly";
                return "Archived";
            }
        });

        Toolbar toolbar = findViewById(R.id.goalsToolbar);
        toolbar.setTitle("Goals");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        tabLayout = findViewById(R.id.goalsTabs);
        tabLayout.setupWithViewPager(pager);

        reloader = new Reloader(this, this);
    }

    @Override
    public void onConnected() {
        reloader.reload();
    }

    @Override
    public void handleReload() {
        new Thread(() -> reloader.requestCalendarEvents((events) -> {
            if(Looper.getMainLooper().getThread() == Thread.currentThread()) new Thread(() -> reloadUI(events)).start();
            else reloadUI(events);
        }, getStartOfWeek(), System.currentTimeMillis())).start();
    }

    private void reloadUI(List<CalendarEvent> events) {
        events = new ArrayList<>(events);

        Iterator<CalendarEvent> it = events.iterator();
        while(it.hasNext()) {
            if(getEventExcluder().isEventExcluded(it.next())) it.remove();
        }

        long dayStartTime = getStartOfDay();
        List<CalendarEvent> dayEvents = new ArrayList<>();
        for(CalendarEvent event : events) {
            if(event.getStartTime() >= dayStartTime) dayEvents.add(event);
        }
        reloadUI((FragmentViewGoal) fragments.get(0), dayEvents, dayStartTime, System.currentTimeMillis());
        reloadUI((FragmentViewGoal) fragments.get(1), events, getStartOfWeek(), System.currentTimeMillis());

        runOnUiThread(() -> {
            ((TextView) findViewById(R.id.goalsTrophyPoints)).setText(getGoalManager().getTrophies() + "");
        });

        reloader.finishReload(null);
    }

    private void reloadUI(FragmentViewGoal fragment, List<CalendarEvent> events, long startTime, long endTime) {
        int screenOns = getConnection().getDatabase().getScreenEventsTable().getScreenOns(events);
        List<AppEvent> appEvents = getConnection().getDatabase().getAppEventsTable().getEvents(events);

        long totalAppTime = 0;
        Set<AppData> uniqueApps = new HashSet<>();
        for(AppEvent event : appEvents) {
            totalAppTime += event.getDuration(startTime, endTime);
            uniqueApps.add(event.getApp());
        }

        long totalEventTime = 0;
        for(CalendarEvent event : events) {
            totalEventTime += event.getDuration();
        }
        GoalActivityData data = new GoalActivityData((int) (totalAppTime / 1000L), uniqueApps.size(), screenOns, (int) (totalEventTime / 1000L));

        runOnUiThread(() -> {
            fragment.reload(getGoalManager().getGoal(fragment.getGoalType()).getObjectives(), data);
        });
    }

    private long getStartOfDay() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis();
    }

    private static long getStartOfWeek() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.DAY_OF_WEEK, 2);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        return calendar.getTimeInMillis() - 1;
    }

    public Reloader getReloader() {
        return reloader;
    }

    @Override
    public void getMenuItems(Map<Integer, Runnable> menus) {
        super.getMenuItems(menus);
        reloader.addMenuItems(menus);
    }

    @Override
    public TrackMenuType getMenuType() {
        return TrackMenuType.CUSTOM;
    }

    @Override
    public String getReloadingMessage() {
        return "Loading Goals...";
    }

    @Override
    public void setContentVisible(boolean visible) {
        findViewById(R.id.goalsPager).setVisibility(visible ? ViewPager.VISIBLE : ViewPager.INVISIBLE);
        findViewById(R.id.goalsTabs).setEnabled(visible);
        findViewById(R.id.goalsLayoutTrophies).setVisibility(visible ? LinearLayout.VISIBLE : LinearLayout.GONE);
        if(visible) findViewById(R.id.goalsTabs).setVisibility(TableLayout.VISIBLE);
    }

    @Override
    public void setMessage(String msg) {
        TextView view = findViewById(R.id.goalsMessage);
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
