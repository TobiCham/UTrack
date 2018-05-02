package edu.utrack.database.table.tables;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.data.screen.ScreenEvent;
import edu.utrack.data.screen.ScreenEventType;
import edu.utrack.database.Database;
import edu.utrack.database.table.EventTable;

public class ScreenEventsTable extends EventTable<ScreenEvent> {

    private static final String TABLE_NAME = "screen_events";

    public ScreenEventsTable(Database database) {
        super(database);
    }

    @Override
    public String getCreateSQLData() {
        return "(`time` INTEGER, `type` INTEGER)";
    }

    @Override
    protected ScreenEvent readValue(Cursor cursor) {
        return new ScreenEvent(cursor.getLong(0), ScreenEventType.getByDatabaseID(cursor.getInt(1)));
    }

    @Override
    protected void writeValue(ScreenEvent data) {
        String qry = "INSERT OR IGNORE INTO `" + getTableName() + "` VALUES(" + data.getTimeStamp() + "," + data.getType().getDatabaseId() + ")";
        getWritebleDB().execSQL(qry);
    }

    @Override
    public List<ScreenEvent> getEventsBetween(long start, long end) {
        String qry = "SELECT * FROM `" + getTableName() + "` WHERE `time` >= ? AND `time` <= ?";
        List<ScreenEvent> events = readValues(getReadableDB().rawQuery(qry, new String[] { Long.toString(start), Long.toString(end) } ));

        for(ScreenEvent screenEvent : getToSaveData()) {
            if(screenEvent.getTimeStamp() >= start && screenEvent.getTimeStamp() <= end) events.add(screenEvent);
        }
        return events;
    }

    private boolean isValidEvent(ScreenEvent event, List<CalendarEvent> calendarEvents) {
        for(CalendarEvent cal : calendarEvents) {
            if(event.getTimeStamp() >= cal.getStartTime() && event.getTimeStamp() <= cal.getEndTime()) return true;
        }
        return false;
    }

    public int getScreenOns(List<CalendarEvent> events) {
        int total = 0;

        if(!events.isEmpty()) {
            String qry = "SELECT COUNT(`time`) AS `screen_ons` FROM `" + getTableName() + "` WHERE `type`=" + ScreenEventType.ON.getDatabaseId() + " AND (";

            for (int i = 0; i < events.size(); i++) {
                if(i != 0) qry += " OR ";
                CalendarEvent event = events.get(i);
                qry += "(`time` >= " + event.getStartTime() + " AND `time` <= " + event.getEndTime() + ")";
            }
            qry += ")";

            Cursor cursor = getReadableDB().rawQuery(qry, null);
            cursor.moveToNext();
            total += cursor.getInt(0);
        }
        for(ScreenEvent event : getToSaveData()) {
            if(event.getType() == ScreenEventType.ON && isValidEvent(event, events)) total++;
        }
        return total;
    }

    public Map<ScreenEventType, List<ScreenEvent>> getScreenCounts(CalendarEvent event) {
        List<ScreenEvent> events = getEvents(event);
        Map<ScreenEventType, List<ScreenEvent>> map = new HashMap<>();
        for(ScreenEventType type : ScreenEventType.values()) map.put(type, new ArrayList<>());

        for(ScreenEvent e : events) {
            map.get(e.getType()).add(e);
        }
        return map;
    }

    @Override
    public void deleteOlderThan(long timestamp) {
        getWritebleDB().execSQL("DELETE FROM `" + getTableName() + "` WHERE `time` < " + timestamp);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
