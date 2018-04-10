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
    public List<AppEvent> getEvents(CalendarEvent event) {
        String sql = "SELECT `{a}`.*, `{u}`.`start_time`, `{u}`.`end_time` FROM `{u}` INNER JOIN `{a}` ON `{u}`.`app` = `{a}`.`id` " +
                     "WHERE `{u}`.`start_time` <= ? AND `{u}`.`end_time` >= ?";
        sql = sql.replace("{u}", getTableName());
        sql = sql.replace("{a}", database.getAppsTable().getTableName());

        String[] sqlData = {
            Long.toString(event.getEndTime()),
            Long.toString(event.getStartTime())
        };

        Cursor cursor = getReadableDB().rawQuery(sql, sqlData);

        List<AppEvent> events = new ArrayList<>();
        for(AppEvent appEvent: readValues(cursor)) {
            if(appEvent.getDuration(event) != 0) events.add(appEvent);
        }
        for(AppEvent appEvent : getToSaveData()) {
            if(appEvent.getDuration(event) != 0) events.add(appEvent);
        }
        return new ArrayList<>(events);
    }

    //TODO Do this
    @Override
    public void deleteOlderThan(long timestamp) {

    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
