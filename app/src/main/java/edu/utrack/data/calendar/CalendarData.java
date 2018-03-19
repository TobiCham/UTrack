package edu.utrack.data.calendar;

import java.util.Objects;

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        CalendarData data = (CalendarData) o;

        if (dbID != data.dbID) return false;
        return Objects.equals(accountName, data.accountName) && Objects.equals(name, data.name) && Objects.equals(ownerAccount, data.ownerAccount);
    }

    @Override
    public int hashCode() {
        int result = dbID;
        result = 43 * result + (accountName != null ? accountName.hashCode() : 0);
        result = 43 * result + (name != null ? name.hashCode() : 0);
        result = 43 * result + (ownerAccount != null ? ownerAccount.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("CalendarData{");
        sb.append("dbID=").append(dbID);
        sb.append(", accountName='").append(accountName).append('\'');
        sb.append(", name='").append(name).append('\'');
        sb.append(", ownerAccount='").append(ownerAccount).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
