package edu.utrack.database.table.tables;

import android.database.Cursor;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.utrack.data.app.AppData;
import edu.utrack.data.app.AppEvent;
import edu.utrack.database.Database;
import edu.utrack.database.table.DataTable;
import edu.utrack.util.AppUtils;

/**
 * Created by Tobi on 07/03/2018.
 */

public class AppEventsTable extends DataTable<AppEvent> {

    private static final String TABLE_NAME = "app_usage";

    public AppEventsTable(Database database) {
        super(database);
    }

    @Override
    public String getCreateSQLData() {
        return "(`app` INTEGER, `start_time` INTEGER, `end_time` INTEGER)";
    }

    @Override
    protected void writeValue(AppEvent data) {
        getWritebleDB().execSQL("INSERT INTO `" + getTableName() + "` (`app`, `start_time`, `end_time`) VALUES(" + data.getApp().getId() + ", " + data.getStartTime() + ", " + data.getEndTime() + ")");
    }

    @Override
    public List<AppEvent> getAllData() {
        String sql = "SELECT `{a}`.*, `{u}`.`start_time`, `{u}`.`end_time` FROM `{u}` INNER JOIN `{a}` ON `{u}`.`app` = `{a}`.`id`";
        sql = sql.replace("{u}", getTableName());
        sql = sql.replace("{a}", database.getAppsTable().getTableName());
        Cursor cursor = getReadableDB().rawQuery(sql, new String[0]);
        Set<AppEvent> events = new HashSet<>();
        events.addAll(getToSaveData());
        while(cursor.moveToNext()) {
            events.add(readValue(cursor));
        }
        return new ArrayList<>(events);
    }

    @Override
    protected AppEvent readValue(Cursor cursor) {
        int id = cursor.getInt(0);
        String packageName = cursor.getString(1);
        AppData data = new AppData(id, packageName);

        return new AppEvent(data, cursor.getLong(2), cursor.getLong(3));
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
