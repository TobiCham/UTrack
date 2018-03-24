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
    public List<ScreenEvent> getEvents(CalendarEvent event) {
        long startDate = event.getStartTime();
        long endDate = event.getEndTime();

        String qry = "SELECT * FROM `" + getTableName() + "` WHERE `time` >= ? AND `time` <= ?";
        List<ScreenEvent> events = readValues(getReadableDB().rawQuery(qry, new String[] { Long.toString(startDate), Long.toString(endDate) } ));

        for(ScreenEvent screenEvent : getToSaveData()) {
            if(screenEvent.getTimeStamp() >= startDate && screenEvent.getTimeStamp() <= endDate) events.add(screenEvent);
        }
        return events;
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
    public String getTableName() {
        return TABLE_NAME;
    }
}
