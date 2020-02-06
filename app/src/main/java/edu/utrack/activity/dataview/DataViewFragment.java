package edu.utrack.activity.dataview;


import androidx.fragment.app.Fragment;
import edu.utrack.data.app.AppEvent;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.data.screen.ScreenEvent;
import edu.utrack.data.screen.ScreenEventType;

import java.util.List;
import java.util.Map;

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
