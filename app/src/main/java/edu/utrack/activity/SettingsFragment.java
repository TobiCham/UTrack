package edu.utrack.activity;

import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.preference.SwitchPreference;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import edu.utrack.data.calendar.CalendarData;
import edu.utrack.settings.AppSettings;
import edu.utrack.settings.HistorySettingType;

public class SettingsFragment extends PreferenceFragment {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        PreferenceScreen screen = getPreferenceManager().createPreferenceScreen(getActivity());

        PreferenceCategory categoryGeneral = new PreferenceCategory(getActivity());
        categoryGeneral.setTitle("General");
        screen.addPreference(categoryGeneral);

        AppSettings settings = ((TrackActivity) getActivity()).getSettings();

        String calendarSummary = "Select a Calendar to use. All events on this calendar will be tracked\n\n%s";

        ListPreference preferenceCalendar = new ListPreference(getActivity());
        CharSequence[] initialCalendarEntries = {"None"};
        preferenceCalendar.setEntryValues(initialCalendarEntries);
        preferenceCalendar.setEntries(initialCalendarEntries);
        preferenceCalendar.setValueIndex(0);
        preferenceCalendar.setTitle("Calendar");
        preferenceCalendar.setSummary(calendarSummary.replace("%s", "None"));

        List<CalendarData> loadedCalendars = new ArrayList<>();
        ((TrackActivity) getActivity()).getCalendarHelper().requestCalendars((calendars) -> {
            if(calendars == null) return;
            CharSequence[] newEntries = new CharSequence[calendars.size() + 1];
            newEntries[0] = "None";

            int selectedID = -1;
            for(int i = 0; i < calendars.size(); i++) {
                CalendarData data = calendars.get(i);
                newEntries[i + 1] = data.getName();
                if(settings.currentCalendarID == data.getDBID()) selectedID = i;
            }
            loadedCalendars.clear();
            loadedCalendars.addAll(calendars);
            preferenceCalendar.setEntryValues(newEntries);
            preferenceCalendar.setEntries(newEntries);

            if(selectedID >= 0) {
                CalendarData data = calendars.get(selectedID);
                preferenceCalendar.setSummary(calendarSummary.replace("%s", data.getName()));
                preferenceCalendar.setValueIndex(selectedID + 1);
            }
        });
        preferenceCalendar.setOnPreferenceChangeListener((p, v) -> {
            if(v.equals("None")) {
                preferenceCalendar.setSummary(calendarSummary.replace("%s", "None"));
                settings.currentCalendarID = -1;
                onSettingChanged(settings);
                return true;
            }
            for(CalendarData data : loadedCalendars) {
                if(data.getName().equals(v.toString())) {
                    preferenceCalendar.setSummary(calendarSummary.replace("%s", data.getName()));
                    settings.currentCalendarID = data.getDBID();
                    onSettingChanged(settings);
                    return true;
                }
            }
            return false;
        });

        String historySummary = "How long should data be kept before being automatically deleted\n\n%s";
        ListPreference preferenceHistory = new ListPreference(getActivity());
        preferenceHistory.setTitle("Data Retention");
        preferenceHistory.setSummary(historySummary.replace("%s", settings.historySetting.getDescription()));
        preferenceHistory.setEntries(HistorySettingType.getOptions());
        preferenceHistory.setEntryValues(HistorySettingType.getOptions());
        preferenceHistory.setValueIndex(settings.historySetting.getIndex());
        preferenceHistory.setOnPreferenceChangeListener((p, v) -> {
            settings.historySetting = HistorySettingType.fromName(v.toString());
            onSettingChanged(settings);
            p.setSummary(historySummary.replace("%s", settings.historySetting.getDescription()));
            return true;
        });

        SwitchPreference preferenceTracking = new SwitchPreference(getActivity());
        preferenceTracking.setChecked(settings.tracks);
        preferenceTracking.setTitle("Tracking");
        preferenceTracking.setSummary("Disable tracking with caution - it will render the app useless for all future events");
        preferenceTracking.setOnPreferenceChangeListener((p, v) -> {
            settings.tracks = (boolean) v;
            onSettingChanged(settings);
            return true;
        });

        categoryGeneral.addPreference(preferenceCalendar);
        categoryGeneral.addPreference(preferenceHistory);
        categoryGeneral.addPreference(preferenceTracking);

        setPreferenceScreen(screen);
    }

    private void onSettingChanged(AppSettings settings) {
        settings.save();
        MonitorActivity activity = (MonitorActivity) getActivity();
        if(activity.getConnection() != null) activity.getConnection().getService().refreshSettings();
    }
}
