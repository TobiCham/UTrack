package edu.utrack.data.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class Database extends SQLiteOpenHelper {

    private static final String DB_NAME = "data.db";
    private ScreenTable screenTable;

    public Database(Context context) {
        super(context, DB_NAME, null, 1);

        screenTable = new ScreenTable(this);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(Table table : new Table[] {screenTable}) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `" + table.getTableName() + "` " + table.getCreateSQLData());
        }
    }

    public ScreenTable getScreenTable() {
        return screenTable;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
