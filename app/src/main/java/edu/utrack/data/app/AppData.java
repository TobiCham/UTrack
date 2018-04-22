package edu.utrack.data.app;

import edu.utrack.util.DataClass;

/**
 * Created by Tobi on 07/03/2018.
 */

public class AppData extends DataClass {

    private int id;
    private String packageName;

    public AppData(int id, String packageName) {
        this.id = id;
        this.packageName = packageName;
    }

    public int getId() {
        return id;
    }

    public String getPackageName() {
        return packageName;
    }

    @Override
    protected String[] getFieldNames() {
        return new String[] {"id", "packageName"};
    }

    @Override
    protected Object[] getFields() {
        return new Object[] {id, packageName};
    }
}
