package edu.utrack.settings;

import android.util.Pair;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import edu.utrack.data.calendar.CalendarEvent;

public class EventExcluder {

    private File saveFile;

    //Each Pair contains two integers. The first represents the Calendar ID, and the second the Event ID of the disabled event
    private Set<Pair<Integer, Integer>> disabledEvents = new HashSet<>();

    public EventExcluder(File saveFile) {
        this.saveFile = saveFile;
        load();
    }

    public void includeEvent(CalendarEvent event) {
        disabledEvents.remove(createPair(event));
        save();
    }

    public void includeEvents(Collection<CalendarEvent> events) {
        for(CalendarEvent event : events) disabledEvents.remove(createPair(event));
        save();
    }

    public void excludeEvent(CalendarEvent event) {
        disabledEvents.add(createPair(event));
        save();
    }

    public void excludeEvents(Collection<CalendarEvent> events) {
        for(CalendarEvent event : events) disabledEvents.add(createPair(event));
        save();
    }

    private Pair<Integer, Integer> createPair(CalendarEvent event) {
        return new Pair(event.getCalendar().getDBID(), event.getDBID());
    }

    public boolean isEventExcluded(CalendarEvent event) {
        return disabledEvents.contains(createPair(event));
    }

    private void load() {
        if(!saveFile.exists()) {
            save();
            return;
        }
        try(DataInputStream dataIn = new DataInputStream(new FileInputStream(saveFile))) {
            int size = dataIn.readInt();
            disabledEvents.clear();

            for(int i = 0; i < size; i++) {
                int calendarID = dataIn.readInt();
                int eventID = dataIn.readInt();
                disabledEvents.add(new Pair<>(calendarID, eventID));
            }
        } catch(IOException e) {
            e.printStackTrace();
        }
    }

    private void save() {
        try(DataOutputStream dataOut = new DataOutputStream(new FileOutputStream(saveFile))) {
            dataOut.writeInt(disabledEvents.size());
            for(Pair<Integer, Integer> event : disabledEvents) {
                dataOut.writeInt(event.first);
                dataOut.writeInt(event.second);
            }
            dataOut.flush();
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
}
