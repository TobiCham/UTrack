package edu.utrack.database.table.tables;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.database.Database;
import edu.utrack.database.table.EventTable;
import edu.utrack.data.screen.ScreenEvent;
import edu.utrack.data.screen.ScreenEventType;

public class ScreenEventsTable extends EventTable<ScreenEvent> {

    private static final String TABLE_NAME = "screen_events";

    public ScreenEventsTable(Database database) {
        super(database);
    }

    @Override
    public String getCreateSQLData() {
        return "(`time` INTEGER, `type` INTEGER, `event` INTEGER)";
    }

    public Map<ScreenEventType, Integer> getScreenCounts(CalendarEvent event) {
        List<ScreenEvent> cache = new ArrayList<>(getToSaveData());
        Cursor cursor = getReadableDB().rawQuery("select distinct `type`, count(`type`) as \"count\" from `" + getTableName() + "` WHERE `event` = " + event.getDBID() + " group by `type`", new String[0]);

        Map<ScreenEventType, Integer> map = new HashMap<>();
        for(ScreenEventType type : ScreenEventType.values()) map.put(type, 0);

        while(cursor.moveToNext()) {
            ScreenEventType type = ScreenEventType.getByDatabaseID(cursor.getInt(0));
            int count = cursor.getInt(1);
            map.put(type, count);
        }
        for(ScreenEvent d : cache) {
            if(!d.getEvent().equals(event)) continue;

            Integer val = map.get(d.getType());
            if(val == null) val = 0;
            val++;
            map.put(d.getType(), val);
        }
        return map;
    }


    @Override
    protected void writeValue(ScreenEvent data) {
        getWritebleDB().execSQL("INSERT INTO `" + getTableName() + "` VALUES(" + data.getTimeStamp() + ", " + data.getType().getDatabaseId() + ", " + data.getEvent().getDBID() + ")");
    }

    @Override
    public List<ScreenEvent> getData(CalendarEvent event) {
        Cursor cursor = getReadableDB().rawQuery("select `time`, `type` FROM `" + getTableName() + "` WHERE `event` = " + event.getDBID(), new String[0]);

        List<ScreenEvent> events = readValues(cursor, event);
        for(ScreenEvent e : getToSaveData()) {
            if(e.getEvent().equals(event)) events.add(e);
        }
        Collections.sort(events);
        return events;
    }

    @Override
    protected ScreenEvent readValue(Cursor cursor, CalendarEvent event) {
        return new ScreenEvent(ScreenEventType.getByDatabaseID(cursor.getInt(0)), cursor.getLong(1), event);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
