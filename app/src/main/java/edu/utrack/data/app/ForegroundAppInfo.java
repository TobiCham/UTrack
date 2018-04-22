package edu.utrack.data.app;

import android.content.pm.ApplicationInfo;

import edu.utrack.util.DataClass;

/**
 * Created by Tobi on 04/03/2018.
 */

public class ForegroundAppInfo extends DataClass {

    private String packageName;
    private String className;

    public ForegroundAppInfo(String packageName, String className) {
        this.packageName = packageName;
        this.className = className;
    }

    public ForegroundAppInfo(ApplicationInfo info) {
        this(info.packageName, info.className);
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ForegroundAppInfo info = (ForegroundAppInfo) o;
        return packageName.equals(info.packageName) && className.equals(info.className);
    }

    public static boolean areSameApp(ForegroundAppInfo app1, ForegroundAppInfo app2) {
        if(app1 == app2) return true;
        if(app1 == null || app2 == null) return false;
        return app1.getPackageName().equals(app2.getPackageName());
    }

    @Override
    protected String[] getFieldNames() {
        return new String[] {"packageName", "className"};
    }

    @Override
    protected Object[] getFields() {
        return new Object[] {packageName, className};
    }
}
