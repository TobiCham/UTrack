package edu.utrack.database.table.tables;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.HashSet;
import java.util.Set;

import edu.utrack.data.app.AppData;
import edu.utrack.database.Database;
import edu.utrack.database.table.Table;

/**
 * Created by Tobi on 07/03/2018.
 */

public class AppsTable extends Table {

    private static final String TABLE_NAME = "apps";
    private static final int LOAD_LIMIT = 50;

    private final Set<AppData> cache = new HashSet<>();

    public AppsTable(Database database) {
        super(database);
    }

    @Override
    public String getCreateSQLData() {
        return "(`id` INTEGER PRIMARY KEY AUTOINCREMENT, `package` TEXT UNIQUE)";
    }

    public void loadTopApps(SQLiteDatabase db) {
        cache.clear();
        String sql = "SELECT DISTINCT `{u}`.`app`, `{a}`.`package`, COUNT(`{u}`.`app`) as `count` "
                   + "FROM `{u}` INNER JOIN `{a}` ON `{a}`.`id` = `{u}`.`app` "
                   + "GROUP BY `{u}`.`app` ORDER BY COUNT(`{u}`.`app`) DESC LIMIT " + LOAD_LIMIT;
        sql = sql.replace("{u}", database.getAppEventsTable().getTableName());
        sql = sql.replace("{a}", getTableName());

        Cursor cursor = db.rawQuery(sql, new String[0]);
        while(cursor.moveToNext()) {
            cache.add(getFromCursor(cursor));
        }
    }

    public AppData getOrCreateAppData(String packageName) {
        AppData app = getDataFromCache(packageName);
        if(app != null) return app;
        return getOrInsert(packageName);
    }

    public AppData getDataFromCache(String packageName) {
        for(AppData app : cache) {
            if(app.getPackageName().equals(packageName)) return app;
        }
        return null;
    }

    private AppData getOrInsert(String packageName) {
        ContentValues values = new ContentValues();
        values.put("package", packageName);
        getWritebleDB().insertWithOnConflict(getTableName(), null, values, SQLiteDatabase.CONFLICT_IGNORE);
        AppData data = getFromDB(packageName);
        if(data != null) cache.add(data);
        return data;
    }

    private AppData getFromDB(String packageName) {
        Cursor cursor = getReadableDB().rawQuery("SELECT `id` FROM `" + getTableName() + "` WHERE `package`=?", new String[] {packageName});
        if(!cursor.moveToNext()) return null;
        return new AppData(cursor.getInt(0), packageName);
    }

    private AppData getFromCursor(Cursor cursor) {
        return new AppData(cursor.getInt(0), cursor.getString(1));
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
