package edu.tracker.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Database extends SQLiteOpenHelper {

    private static final String DB_NAME = "/sdcard/data.db";
    private ScreenTable tableScreen;

    public Database(Context context) {
        super(context, "data.db", null, 1);

        File f = getFile();
        if(!f.getParentFile().exists()) f.getParentFile().mkdirs();

        tableScreen = new ScreenTable(this);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        for(Table table : new Table[] {tableScreen}) {
            db.execSQL("CREATE TABLE IF NOT EXISTS `" + table.getTableName() + "` " + table.getCreateSQLData());
        }
    }

    public ScreenTable getTableScreen() {
        return tableScreen;
    }

    private static File getFile() {
        return new File(Environment.getExternalStorageDirectory(), "tracker.db");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
