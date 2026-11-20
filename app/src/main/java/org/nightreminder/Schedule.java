package org.nightreminder;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import java.time.LocalTime;
import java.time.ZonedDateTime;

final class Schedule {
    static final String FIRE = "org.nightreminder.FIRE";

    static SharedPreferences settings(Context context) {
        return context.getSharedPreferences("bedtime", Context.MODE_PRIVATE);
    }

    static LocalTime bedtime(Context context) {
        return LocalTime.ofSecondOfDay(settings(context).getInt("minute", 23 * 60) * 60L);
    }

    static boolean hasExactPermission(Context context) {
        return Build.VERSION.SDK_INT < 31
            || context.getSystemService(AlarmManager.class).canScheduleExactAlarms();
    }

    static void refresh(Context context) {
        AlarmManager alarms = context.getSystemService(AlarmManager.class);
        PendingIntent trigger = PendingIntent.getBroadcast(context, 1,
            new Intent(context, ReminderReceiver.class).setAction(FIRE), PendingIntent.FLAG_IMMUTABLE);
        alarms.cancel(trigger);
        if (!settings(context).getBoolean("enabled", false) || !hasExactPermission(context)) return;

        ZonedDateTime now = ZonedDateTime.now();
        LocalTime time = bedtime(context);
        ZonedDateTime next = now.toLocalDate().atTime(time).atZone(now.getZone());
        if (!next.isAfter(now)) {
            next = now.toLocalDate().plusDays(1).atTime(time).atZone(now.getZone());
        }
        try {
            // Wake the CPU to sample the state on time. This alarm does not turn on the display.
            alarms.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, next.toInstant().toEpochMilli(), trigger);
        } catch (SecurityException permissionRevoked) {
            // The grant broadcast or the next app launch will restore the schedule.
        }
    }
}
