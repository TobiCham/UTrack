package edu.utrack;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import java.util.Calendar;

public class BootListener extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        if(!intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) return;

        registerGoalAlarm(context);
    }

    /**
     * Registers the Goal alarm, is not already registered
     * @param context
     */
    public static void registerGoalAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        calendar.add(Calendar.DAY_OF_YEAR, 1);

        Intent intent = new Intent("edu.utrack.goalalarm");
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);

        AlarmManager manager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
