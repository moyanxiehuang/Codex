package org.nightreminder;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.GradientDrawable;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

public final class OverlayService extends Service {
    private final Handler timer = new Handler(Looper.getMainLooper());
    private WindowManager windows;
    private LinearLayout card;
    private boolean listening;
    private final BroadcastReceiver screenOff = new BroadcastReceiver() {
        @Override public void onReceive(Context context, Intent intent) { stopSelf(); }
    };

    @Override public IBinder onBind(Intent intent) { return null; }

    @Override public int onStartCommand(Intent intent, int flags, int startId) {
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(new NotificationChannel(
            "overlay", "悬浮窗运行状态", NotificationManager.IMPORTANCE_LOW));
        PendingIntent dismiss = PendingIntent.getService(this, 2,
            new Intent(this, OverlayService.class).setAction("dismiss"), PendingIntent.FLAG_IMMUTABLE);
        startForeground(1, new Notification.Builder(this, "overlay")
            .setSmallIcon(R.drawable.moon).setContentTitle("早点睡")
            .setContentText("点此关闭睡觉提醒").setContentIntent(dismiss).build());
        if (intent == null || "dismiss".equals(intent.getAction()) || !Settings.canDrawOverlays(this)) {
            stopSelf();
            return START_NOT_STICKY;
        }
        if (!listening) {
            registerReceiver(screenOff, new IntentFilter(Intent.ACTION_SCREEN_OFF));
            listening = true;
        }
        if (!getSystemService(PowerManager.class).isInteractive()) {
            stopSelf();
            return START_NOT_STICKY;
        }
        if (card == null) showCard();
        timer.removeCallbacksAndMessages(null);
        timer.postDelayed(this::stopSelf, 60_000);
        return START_NOT_STICKY;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private WindowManager.LayoutParams layout() {
        Configuration config = getResources().getConfiguration();
        int width = dp(Math.round(config.screenWidthDp * 0.94f));
        int height = dp(Math.round(config.screenHeightDp * 0.53f));
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(width, height,
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE, PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.CENTER;
        return params;
    }

    private void showCard() {
        windows = getSystemService(WindowManager.class);
        LinearLayout panel = new LinearLayout(this);
        panel.setOrientation(LinearLayout.VERTICAL);
        panel.setGravity(Gravity.CENTER);
        panel.setPadding(dp(16), dp(8), dp(16), dp(8));
        GradientDrawable surface = new GradientDrawable();
        surface.setColor(0xff202640);
        surface.setCornerRadius(dp(24));
        panel.setBackground(surface);
        TextView title = new TextView(this);
        title.setText("该睡觉了");
        title.setTextSize(26);
        title.setTextColor(Color.WHITE);
        title.setGravity(Gravity.CENTER);
        panel.addView(title);
        TextView message = new TextView(this);
        message.setText("先放下手机，早点休息。");
        message.setTextColor(0xffd6dafa);
        message.setGravity(Gravity.CENTER);
        panel.addView(message);
        Button close = new Button(this);
        close.setText("知道了");
        close.setOnClickListener(view -> stopSelf());
        panel.addView(close);
        try {
            windows.addView(panel, layout());
            card = panel;
        } catch (WindowManager.BadTokenException | SecurityException error) {
            stopSelf();
        }
    }

    @Override public void onConfigurationChanged(Configuration config) {
        super.onConfigurationChanged(config);
        if (card != null) windows.updateViewLayout(card, layout());
    }

    @Override public void onDestroy() {
        timer.removeCallbacksAndMessages(null);
        if (card != null) windows.removeView(card);
        if (listening) unregisterReceiver(screenOff);
        stopForeground(STOP_FOREGROUND_REMOVE);
        super.onDestroy();
    }
}
