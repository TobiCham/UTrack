package edu.utrack.database.table.tables;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.List;

import edu.utrack.data.app.AppData;
import edu.utrack.data.app.AppEvent;
import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.database.Database;
import edu.utrack.database.table.EventTable;

/**
 * Created by Tobi on 07/03/2018.
 */

public class AppEventsTable extends EventTable<AppEvent> {

    private static final String TABLE_NAME = "app_events";

    public AppEventsTable(Database database) {
        super(database);
    }

    @Override
    public String getCreateSQLData() {
        return "(`app` INTEGER, `start_time` INTEGER, `end_time` INTEGER)";
    }

    @Override
    protected void writeValue(AppEvent data) {
        String[] sqlData = {
            Integer.toString(data.getApp().getId()),
            Long.toString(data.getStartTime()),
            Long.toString(data.getEndTime())
        };
        getWritebleDB().execSQL("INSERT INTO `" + getTableName() + "` VALUES(?,?,?)", sqlData);
    }

    @Override
    protected AppEvent readValue(Cursor cursor) {
        int id = cursor.getInt(0);
        String packageName = cursor.getString(1);
        AppData data = new AppData(id, packageName);

        return new AppEvent(data, cursor.getLong(2), cursor.getLong(3));
    }

    @Override
    public List<AppEvent> getEventsBetween(long start, long end) {
        String sql = "SELECT `{a}`.*, `{u}`.`start_time`, `{u}`.`end_time` FROM `{u}` INNER JOIN `{a}` ON `{u}`.`app` = `{a}`.`id` " +
                "WHERE `{u}`.`start_time` <= ? AND `{u}`.`end_time` >= ?";
        sql = sql.replace("{u}", getTableName());
        sql = sql.replace("{a}", database.getAppsTable().getTableName());

        String[] sqlData = {
            Long.toString(end),
            Long.toString(start)
        };

        Cursor cursor = getReadableDB().rawQuery(sql, sqlData);

        List<AppEvent> events = new ArrayList<>();
        for(AppEvent appEvent: readValues(cursor)) {
            if(appEvent.getDuration(start, end) != 0) events.add(appEvent);
        }
        for(AppEvent appEvent : getToSaveData()) {
            if(appEvent.getDuration(start, end) != 0) events.add(appEvent);
        }
        return new ArrayList<>(events);
    }

    private boolean isValidEvent(AppEvent event, List<CalendarEvent> calendarEvents) {
        for(CalendarEvent cal : calendarEvents) {
            if(event.getDuration(cal) > 0) return true;
        }
        return false;
    }

    public List<AppEvent> getEvents(List<CalendarEvent> events) {
        List<AppEvent> newEvents = new ArrayList<>();

        if(!events.isEmpty()) {
            String sql = "SELECT `{a}`.*, `{u}`.`start_time`, `{u}`.`end_time` FROM `{u}` INNER JOIN `{a}` ON `{u}`.`app` = `{a}`.`id` WHERE";

            for (int i = 0; i < events.size(); i++) {
                sql += " ";
                if(i != 0) sql += "OR ";
                sql += "(`{u}`.`start_time` <= " + events.get(i).getEndTime() + " AND `{u}`.`end_time` >= " + events.get(i).getStartTime() + ")";
            }
            sql = sql.replace("{u}", getTableName());
            sql = sql.replace("{a}", database.getAppsTable().getTableName());

            Cursor cursor = getReadableDB().rawQuery(sql, null);
            newEvents.addAll(readValues(cursor));
        }
        for(AppEvent event : getToSaveData()) {
            if(isValidEvent(event, events)) newEvents.add(event);
        }
        return newEvents;
    }

    @Override
    public void deleteOlderThan(long timestamp) {
        getWritebleDB().execSQL("DELETE FROM `" + getTableName() + "` WHERE `end_time` < " + timestamp);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
