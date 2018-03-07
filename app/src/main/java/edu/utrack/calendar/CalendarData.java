package edu.utrack.calendar;

/**
 * Created by Tobi on 06/03/2018.
 */

public class CalendarData {

    private int dbID;
    private String accountName, name, ownerAccount;

    public CalendarData(int dbID, String accountName, String name, String ownerAccount) {
        this.dbID = dbID;
        this.accountName = accountName;
        this.name = name;
        this.ownerAccount = ownerAccount;
    }

    public int getDBID() {
        return dbID;
    }

    public String getAccountName() {
        return accountName;
    }

    public String getName() {
        return name;
    }

    public String getOwnerAccount() {
        return ownerAccount;
    }
}
