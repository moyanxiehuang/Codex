package org.nightreminder;

import android.app.Activity;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public final class MainActivity extends Activity {
    private LinearLayout body;
    private TextView status;
    private Button time;
    private Button overlayPermission;
    private Button exactPermission;
    private Button preview;
    private SharedPreferences preferences;

    @Override protected void onCreate(Bundle state) {
        super.onCreate(state);
        preferences = Schedule.settings(this);
        ScrollView scroll = new ScrollView(this);
        scroll.setBackgroundColor(0xfff2f3fb);
        body = new LinearLayout(this);
        body.setOrientation(LinearLayout.VERTICAL);
        body.setGravity(Gravity.CENTER_HORIZONTAL);
        scroll.addView(body);
        scroll.setOnApplyWindowInsetsListener((view, insets) -> {
            body.setPadding(dp(24) + insets.getSystemWindowInsetLeft(),
                dp(42) + insets.getSystemWindowInsetTop(),
                dp(24) + insets.getSystemWindowInsetRight(),
                dp(24) + insets.getSystemWindowInsetBottom());
            return insets;
        });
        setContentView(scroll);
        TextView title = label("早点睡", 36);
        title.setPadding(0, 0, 0, dp(12));
        label("每天到点，亮屏时弹出睡觉提醒。", 16);
        time = button("", view -> pickTime());
        Switch enabled = new Switch(this);
        enabled.setText("开启每日提醒");
        enabled.setPadding(dp(8), dp(12), dp(8), dp(12));
        enabled.setChecked(preferences.getBoolean("enabled", false));
        body.addView(enabled);
        enabled.setOnCheckedChangeListener((view, checked) -> {
            preferences.edit().putBoolean("enabled", checked).apply();
            if (!checked) stopService(new Intent(this, OverlayService.class));
            refresh();
        });
        status = label("", 15);
        overlayPermission = button("允许显示悬浮窗", view -> openSettings(Settings.ACTION_MANAGE_OVERLAY_PERMISSION));
        exactPermission = button("允许准时提醒", view -> openSettings(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM));
        preview = button("预览悬浮窗", view -> startForegroundService(new Intent(this, OverlayService.class)));
    }

    @Override protected void onResume() {
        super.onResume();
        refresh();
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private TextView label(String text, int size) {
        TextView label = new TextView(this);
        label.setText(text);
        label.setTextSize(size);
        label.setTextColor(Color.rgb(32, 38, 64));
        label.setGravity(Gravity.CENTER);
        body.addView(label);
        return label;
    }

    private Button button(String label, View.OnClickListener action) {
        Button button = new Button(this);
        button.setText(label);
        button.setMinHeight(dp(48));
        button.setOnClickListener(action);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(-1, -2);
        params.topMargin = dp(12);
        body.addView(button, params);
        return button;
    }

    private void openSettings(String action) {
        try {
            startActivity(new Intent(action, Uri.fromParts("package", getPackageName(), null)));
        } catch (android.content.ActivityNotFoundException error) {
            Toast.makeText(this, "请在系统设置中查找应用权限", Toast.LENGTH_LONG).show();
        }
    }

    private void pickTime() {
        java.time.LocalTime current = Schedule.bedtime(this);
        new TimePickerDialog(this, (picker, hour, minute) -> {
            preferences.edit().putInt("minute", hour * 60 + minute).apply();
            refresh();
        }, current.getHour(), current.getMinute(), android.text.format.DateFormat.is24HourFormat(this)).show();
    }

    private void refresh() {
        boolean overlay = Settings.canDrawOverlays(this);
        boolean exact = Schedule.hasExactPermission(this);
        time.setText("提醒时间  " + Schedule.bedtime(this));
        overlayPermission.setVisibility(overlay ? View.GONE : View.VISIBLE);
        exactPermission.setVisibility(exact ? View.GONE : View.VISIBLE);
        preview.setEnabled(overlay);
        if (!preferences.getBoolean("enabled", false)) {
            status.setText("每日提醒已关闭");
        } else if (!overlay || !exact) {
            status.setText("授权后启用每日提醒");
        } else {
            status.setText("每日提醒已开启");
        }
        Schedule.refresh(this);
    }
}
