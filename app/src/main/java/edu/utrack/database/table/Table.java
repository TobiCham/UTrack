package edu.utrack.database.table;

import android.database.sqlite.SQLiteDatabase;

import edu.utrack.database.Database;

public abstract class Table {

    protected final Database database;

    public Table(Database database) {
        this.database = database;
    }

    public void createTable(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS `" + getTableName() + "` " + getCreateSQLData());
    }

    public void clearTable() {
        database.getWritableDatabase().execSQL("drop table `" + getTableName() + "`");
    }

    protected final SQLiteDatabase getWritebleDB() {
        return database.getWritableDatabase();
    }
    protected final SQLiteDatabase getReadableDB() {
        return database.getReadableDatabase();
    }

    public abstract String getCreateSQLData();

    public abstract String getTableName();
}
