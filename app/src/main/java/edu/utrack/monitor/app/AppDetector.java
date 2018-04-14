package edu.utrack.monitor.app;

import android.app.ActivityManager;
import android.app.AppOpsManager;
import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.PowerManager;
import android.provider.Settings;

import edu.utrack.data.app.ForegroundAppInfo;

import static android.content.Context.POWER_SERVICE;

/**
 * Created by Tobi on 04/03/2018.
 */

public class AppDetector {

    private Context context;

    public AppDetector(Context context) {
        this.context = context;
        requestUsageStatsPermission();
    }

    public ForegroundAppInfo getForegroundApp() {
        if(!isInteractive()) return null;
        return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP ? getAppLollipop() : getApp();
    }

    /*
        The following two methods are a combination of three resources

        https://stackoverflow.com/questions/27087675/cannot-get-foreground-activity-name-in-android-lollipop-5-0-only
        https://stackoverflow.com/questions/44369003/get-open-activity-of-the-foreground-app
        https://github.com/ricvalerio/foregroundappchecker

        The majority of the code is copied from the github resource, with some tweaks
     */

    private ForegroundAppInfo getApp() {
        ActivityManager am = (ActivityManager) context.getSystemService(Service.ACTIVITY_SERVICE);
        ActivityManager.RunningTaskInfo foregroundTaskInfo = am.getRunningTasks(1).get(0);
        String foregroundTaskPackageName = foregroundTaskInfo.topActivity.getPackageName();
        PackageManager pm = context.getPackageManager();
        PackageInfo foregroundAppPackageInfo = null;
        try {
            foregroundAppPackageInfo = pm.getPackageInfo(foregroundTaskPackageName, 0);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }

        if(foregroundAppPackageInfo != null) {
            ApplicationInfo info = foregroundAppPackageInfo.applicationInfo;
            return new ForegroundAppInfo(info.packageName, info.className);
        }
        return null;
    }

    private ForegroundAppInfo getAppLollipop() {
        if(!hasUsageStatsPermission(context)) return null;

        ForegroundAppInfo info = null;

        UsageStatsManager mUsageStatsManager = (UsageStatsManager) context.getSystemService(Service.USAGE_STATS_SERVICE);
        long time = System.currentTimeMillis();

        //15 minutes
        UsageEvents usageEvents = mUsageStatsManager.queryEvents(time - 1000 * 60 * 15, time);
        UsageEvents.Event event = new UsageEvents.Event();
        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event);
            if(event.getEventType() == UsageEvents.Event.MOVE_TO_FOREGROUND) {
                info = new ForegroundAppInfo(event.getPackageName(), event.getClassName());
            }
        }
        return info;
    }

    /*
        The following two methods come from the github resource named above:
        https://github.com/ricvalerio/foregroundappchecker
     */

    private boolean hasUsageStatsPermission(Context context) {
        AppOpsManager appOps = (AppOpsManager) context.getSystemService(Context.APP_OPS_SERVICE);
        int mode = appOps.checkOpNoThrow("android:get_usage_stats", android.os.Process.myUid(), context.getPackageName());
        return mode == AppOpsManager.MODE_ALLOWED;
    }

    private void requestUsageStatsPermission() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && !hasUsageStatsPermission(context)) {
            Intent intent = new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            context.startActivity(intent);
        }
    }

    /**
     * https://stackoverflow.com/questions/2474367/how-can-i-tell-if-the-screen-is-on-in-android
     * @return if the phone is on/open
     */
    private boolean isInteractive() {
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) return powerManager.isInteractive();
        else return powerManager.isScreenOn();
    }
}
