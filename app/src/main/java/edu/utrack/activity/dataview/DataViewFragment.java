package edu.utrack.activity.dataview;

import android.support.v4.app.Fragment;

import java.util.List;
import java.util.Map;

import edu.utrack.data.app.AppEvent;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.data.screen.ScreenEvent;
import edu.utrack.data.screen.ScreenEventType;

/**
 * Created by Tobi on 29/03/2018.
 */

public abstract class DataViewFragment extends Fragment {

    protected CalendarEvent event;

    public void setEvent(CalendarEvent event) {
        this.event = event;
    }

    public abstract void updateUI(List<AppEvent> appEvents, Map<ScreenEventType, List<ScreenEvent>> screenEvents);
}
