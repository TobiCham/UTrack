package edu.utrack.database.table.tables;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import edu.utrack.calendar.CalendarTracker;
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
        return "(`app` INTEGER, `start_time` INTEGER, `end_time` INTEGER, `event` INTEGER)";
    }

    @Override
    protected void writeValue(AppEvent data) {
        getWritebleDB().execSQL("INSERT INTO `" + getTableName() + "` VALUES(" + data.getApp().getId() + ", " + data.getStartTime() + ", " + data.getEndTime() + ", " + data.getEvent().getDBID() + ")");
    }

    @Override
    public List<AppEvent> getData(CalendarEvent event) {
        String sql = "SELECT `{a}`.*, `{u}`.`start_time`, `{u}`.`end_time` FROM `{u}` INNER JOIN `{a}` ON `{u}`.`app` = `{a}`.`id` WHERE `{u}`.`event`=" + event.getDBID();
        sql = sql.replace("{u}", getTableName());
        sql = sql.replace("{a}", database.getAppsTable().getTableName());

        Cursor cursor = getReadableDB().rawQuery(sql, new String[0]);
        Set<AppEvent> events = new HashSet<>();
        events.addAll(readValues(cursor, event));

        System.out.println("App Events: " + events.size());

        for(AppEvent e : getToSaveData()) {
            if(e.getEvent().equals(event)) events.add(e);
        }
        List<AppEvent> finalEvents = new ArrayList<>(events);
        Collections.sort(finalEvents);

        return finalEvents;
    }

    @Override
    protected AppEvent readValue(Cursor cursor, CalendarEvent event) {
        int id = cursor.getInt(0);
        String packageName = cursor.getString(1);
        AppData data = new AppData(id, packageName);

        //TODO Add to Calendar event
        return new AppEvent(data, event, cursor.getLong(2), cursor.getLong(3));
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
