package edu.utrack.database.table;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import edu.utrack.data.calendar.CalendarEvent;
import edu.utrack.database.Database;

/**
 * Created by Tobi on 07/03/2018.
 */

public abstract class EventTable<T> extends Table {

    private boolean autosave;
    private List<T> toSaveData;

    public EventTable(Database database) {
        this(database, false);
    }

    public EventTable(Database database, boolean autosave) {
        super(database);
        this.autosave = autosave;
        toSaveData = new ArrayList<>();
    }

    protected abstract T readValue(Cursor cursor);

    protected List<T> readValues(Cursor cursor) {
        List<T> list = new ArrayList<>();
        while(cursor.moveToNext()) {
            list.add(readValue(cursor));
        }
        return list;
    }

    public abstract List<T> getEvents(CalendarEvent event);

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

    public List<T> getToSaveData() {
        return toSaveData;
    }

    public void insertData(T data) {
        if(autosave) insertData(data);
        else this.toSaveData.add(data);
    }

    public void insertData(List<T> data) {
        if(autosave) insertData(data);
        else this.toSaveData.addAll(data);
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

    @Override
    public void clearTable() {
        toSaveData.clear();
        super.clearTable();
    }
}
