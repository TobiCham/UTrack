package edu.utrack.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;

/**
 * Created by Tobi on 07/03/2018.
 */

public class AppUtils {

    public static String getAppName(String packageName, Context context) {
        PackageManager manager = context.getApplicationContext().getPackageManager();
        String name = null;
        try {
            ApplicationInfo info = manager.getApplicationInfo( packageName, 0);
            if(info != null) {
                CharSequence seq = manager.getApplicationLabel(info);
                if(seq != null) name = seq.toString();
            }
        } catch (PackageManager.NameNotFoundException e) { }
        return name == null ? "Unknown" : name;
    }
}
