package edu.utrack.data.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

public abstract class Table<T> {

    protected final Database database;
    private boolean autosave;

    private List<T> toSaveData;

    public Table(Database database) {
        this(database, false);
    }

    public Table(Database database, boolean autosave) {
        this.autosave = autosave;
        this.database = database;
        this.toSaveData = new ArrayList<>();
    }

    public List<T> getToSaveData() {
        return toSaveData;
    }

    public void saveCache() {
        List<T> saveData;
        synchronized (toSaveData) {
            if(toSaveData == null || toSaveData.isEmpty()) return;
            saveData = new ArrayList<>(toSaveData);
            toSaveData.clear();
        }
        writeValues(saveData);
    }

    public void insertData(T data) {
        if(autosave) insertData(data);
        else this.toSaveData.add(data);
    }

    public List<T> getAllData() {
        List<T> data = new ArrayList<>(toSaveData);
        data.addAll(readValues(getReadableDB().rawQuery("SELECT * FROM `" + getTableName() + "`", new String[0])));
        return data;
    }

    public void clearTable() {
        toSaveData.clear();
        database.getWritableDatabase().execSQL("delete from `" + getTableName() + "`");
    }

    protected final SQLiteDatabase getWritebleDB() {
        return database.getWritableDatabase();
    }
    protected final SQLiteDatabase getReadableDB() {
        return database.getReadableDatabase();
    }

    public void insertData(List<T> data) {
        this.toSaveData.addAll(data);
    }

    public abstract String getCreateSQLData();

    public abstract String getTableName();

    protected abstract void writeValue(T data);

    protected void writeValues(List<T> data) {
        if(data.isEmpty()) return;
        if(data.size() == 1) {
            writeValue(data.get(0));
            return;
        }

        SQLiteDatabase db = getWritebleDB();
        try {
            db.beginTransaction();

            for(T d : data) writeValue(d);

            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

    protected abstract T readValue(Cursor cursor);

    protected List<T> readValues(Cursor cursor) {
        List<T> list = new ArrayList<>();
        while(cursor.moveToNext()) {
            list.add(readValue(cursor));
        }
        return list;
    }

}
