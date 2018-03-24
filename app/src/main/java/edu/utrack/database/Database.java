package edu.utrack.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import edu.utrack.database.table.Table;
import edu.utrack.database.table.tables.AppEventsTable;
import edu.utrack.database.table.tables.AppsTable;
import edu.utrack.database.table.tables.ScreenEventsTable;

public class Database extends SQLiteOpenHelper {

    private static final String DB_NAME = "data.db";

    private ScreenEventsTable screenEventsTable;
    private AppsTable appsTable;
    private AppEventsTable appEventsTable;

    public Database(Context context) {
        super(context, DB_NAME, null, 1);

        screenEventsTable = new ScreenEventsTable(this);
        appsTable = new AppsTable(this);
        appEventsTable = new AppEventsTable(this);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(Table table : getTables()) {
            table.createTable(db);
        }
        appsTable.loadTopApps(db);
    }

    public ScreenEventsTable getScreenEventsTable() {
        return screenEventsTable;
    }

    public AppsTable getAppsTable() {
        return appsTable;
    }

    public AppEventsTable getAppEventsTable() {
        return appEventsTable;
    }

    public Table[] getTables() {
        return new Table[] {screenEventsTable, appsTable, appEventsTable};
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
