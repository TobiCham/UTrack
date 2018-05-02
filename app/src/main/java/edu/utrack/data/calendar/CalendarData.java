package edu.utrack.data.calendar;

import edu.utrack.util.DataClass;

/**
 * Created by Tobi on 06/03/2018.
 */

public class CalendarData extends DataClass {

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

    @Override
    protected String[] getFieldNames() {
        return new String[] {"id", "account", "name", "owner"};
    }

    @Override
    protected Object[] getFields() {
        return new Object[] {dbID, accountName, name, ownerAccount};
    }
}
