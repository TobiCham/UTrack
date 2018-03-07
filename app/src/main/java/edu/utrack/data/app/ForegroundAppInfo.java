package edu.utrack.data.app;

import android.content.pm.ApplicationInfo;

/**
 * Created by Tobi on 04/03/2018.
 */

public class ForegroundAppInfo {

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

    @Override
    public int hashCode() {
        return (41 * packageName.hashCode()) + className.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("ForegroundAppInfo{");
        sb.append("packageName='").append(packageName).append('\'');
        sb.append(", className='").append(className).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
