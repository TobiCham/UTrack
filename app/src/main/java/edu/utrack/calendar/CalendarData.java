package edu.utrack.calendar;

/**
 * Created by Tobi on 06/03/2018.
 */

public class CalendarData {

    private String accountName, name, ownerAccount;

    public CalendarData(String accountName, String name, String ownerAccount) {
        this.accountName = accountName;
        this.name = name;
        this.ownerAccount = ownerAccount;
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
