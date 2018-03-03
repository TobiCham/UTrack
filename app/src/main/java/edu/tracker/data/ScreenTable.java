package edu.tracker.data;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteStatement;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import edu.tracker.monitor.screen.ScreenData;

public class ScreenTable extends Table<ScreenData> {

    private static final String TABLE_NAME = "screendata";

    public ScreenTable(Database database) {
        super(database);
    }

    @Override
    public String getCreateSQLData() {
        return "(`time` INTEGER NOT NULL, `type` INTEGER NOT NULL)";
    }

    public Map<ScreenDataType, Integer> getScreenCounts() {
        List<ScreenData> cache = new ArrayList<>(getToSaveData());
        Cursor cursor = getReadableDB().rawQuery("select distinct `type`, count(`type`) as \"count\" from `" + getTableName() + "` group by `type`", new String[0]);

        Map<ScreenDataType, Integer> map = new HashMap<>();
        for(ScreenDataType type : ScreenDataType.values()) map.put(type, 0);

        while(cursor.moveToNext()) {
            ScreenDataType type = ScreenDataType.getByDatabaseID(cursor.getInt(0));
            int count = cursor.getInt(1);
            map.put(type, count);
        }
        for(ScreenData d : cache) {
            Integer val = map.get(d.getType());
            if(val == null) val = 0;
            val++;
            map.put(d.getType(), val);
        }
        return map;
    }

    @Override
    protected void writeValue(ScreenData data) {
        getWritebleDB().execSQL("INSERT INTO `" + getTableName() + "` VALUES(" + data.getTimeStamp() + ", " + data.getType().getDatabaseId() + ")");
    }

    @Override
    protected ScreenData readValue(Cursor cursor) {
        long timestamp = cursor.getLong(0);
        ScreenDataType type = ScreenDataType.getByDatabaseID((byte) cursor.getInt(1));

        System.out.println(timestamp + ", " + type.getFriendlyName());

        return new ScreenData(type, timestamp);
    }

    @Override
    public String getTableName() {
        return TABLE_NAME;
    }
}
