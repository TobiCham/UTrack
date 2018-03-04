package edu.utrack.data;

import android.content.Intent;

/**
 * Created by Tobi on 03/03/2018.
 */

public enum ScreenDataType {

    ON(Intent.ACTION_SCREEN_ON, "Screen On", 0),
    OFF(Intent.ACTION_SCREEN_OFF, "Screen Off", 1),
    UNLOCK(Intent.ACTION_USER_PRESENT, "Unlock", 2);

    private String intentAction, friendlyName;
    private int dbID;

    ScreenDataType(String intentAction, String friendlyName, int dbID) {
        this.intentAction = intentAction;
        this.friendlyName = friendlyName;
        this.dbID = dbID;
    }

    public int getDatabaseId() {
        return dbID;
    }

    public String getIntentAction() {
        return intentAction;
    }

    public String getFriendlyName() {
        return friendlyName;
    }

    public static ScreenDataType getByDatabaseID(int id) {
        for(ScreenDataType type : values()) {
            if(type.dbID == id) return type;
        }
        return null;
    }

    public static ScreenDataType getByIntentAction(String name) {
        for(ScreenDataType type : values()) {
            if(type.intentAction.equals(name)) return type;
        }
        return null;
    }
}
