package edu.utrack.data.app;

/**
 * Created by Tobi on 07/03/2018.
 */

public class AppData {

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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        AppData appData = (AppData) o;
        return id == appData.id && packageName.equals(appData.packageName);
    }

    @Override
    public int hashCode() {
        return (31 * id) + packageName.hashCode();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder("AppData{");
        sb.append("id=").append(id);
        sb.append(", packageName='").append(packageName).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
