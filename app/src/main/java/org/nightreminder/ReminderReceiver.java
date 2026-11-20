package org.nightreminder;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import java.time.Duration;
import java.time.ZonedDateTime;

public final class ReminderReceiver extends BroadcastReceiver {
    @Override public void onReceive(Context context, Intent intent) {
        if (!Schedule.FIRE.equals(intent.getAction())) {
            Schedule.refresh(context);
            return;
        }
        if (!Schedule.settings(context).getBoolean("enabled", false)) return;
        Schedule.refresh(context);
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime expected = now.toLocalDate().atTime(Schedule.bedtime(context)).atZone(now.getZone());
        long delay = Duration.between(expected, now).getSeconds();
        if (delay < 0 || delay >= 60 || !Settings.canDrawOverlays(context)
                || !context.getSystemService(PowerManager.class).isInteractive()) return;
        try {
            context.startForegroundService(new Intent(context, OverlayService.class));
        } catch (IllegalStateException | SecurityException error) {
            Log.w("NightReminder", "Could not start bedtime overlay", error);
        }
    }
}
