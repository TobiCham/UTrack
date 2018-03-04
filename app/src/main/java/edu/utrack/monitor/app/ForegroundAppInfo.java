package edu.utrack.monitor.app;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * Created by Tobi on 04/03/2018.
 */

public class ForegroundAppInfo {

    private String packageName;
    private String className;
    private String label;
    private Drawable icon;

    public ForegroundAppInfo(String packageName, String className, String label, Drawable icon) {
        this.packageName = packageName;
        this.className = className;
        this.label = label;
        this.icon = icon;
    }

    public ForegroundAppInfo(ApplicationInfo info, Context context) {
        init(info, context);
    }

    public ForegroundAppInfo(String packageName, String className, Context context) {
        PackageManager packageManager = context.getPackageManager();
        ApplicationInfo info = null;
        try {
            info = packageManager.getApplicationInfo(packageName, 0);
            init(info, context);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            this.packageName = packageName;
            this.className = className;
        }
    }

    private void init(ApplicationInfo info, Context context) {
        PackageManager packageManager = context.getPackageManager();

        CharSequence name = packageManager.getApplicationLabel(info);
        Drawable icon = packageManager.getApplicationIcon(info);

        this.packageName = info.packageName;
        this.className = info.className;
        if(name != null) this.label = name.toString();
        this.icon = icon;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }

    public String getLabel() {
        return label;
    }

    public Drawable getIcon() {
        return icon;
    }
}
