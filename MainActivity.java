package com.luxurytranslator.app;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends Activity {
    private TextView statusText;
    private Button toggleButton;
    private boolean overlayRunning = false;

    @Override public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        statusText = findViewById(R.id.statusText);
        Button permissionButton = findViewById(R.id.permissionButton);
        toggleButton = findViewById(R.id.toggleButton);

        permissionButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                    Uri.parse("package:" + getPackageName()));
                startActivity(intent);
            } else {
                updateStatus();
            }
        });

        toggleButton.setOnClickListener(v -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
                statusText.setText("ابتدا مجوز پنجره شناور را فعال کنید.");
                return;
            }
            Intent serviceIntent = new Intent(this, FloatingTranslatorService.class);
            if (!overlayRunning) {
                startService(serviceIntent);
                overlayRunning = true;
                toggleButton.setText("خاموش‌کردن مترجم شناور");
                statusText.setText("دکمه شناور فعال شد. برای جابه‌جایی، آن را لمس کنید و بکشید.");
            } else {
                stopService(serviceIntent);
                overlayRunning = false;
                toggleButton.setText("فعال‌کردن مترجم شناور");
                statusText.setText("مترجم شناور خاموش شد.");
            }
        });
    }

    @Override protected void onResume() { super.onResume(); updateStatus(); }

    private void updateStatus() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Settings.canDrawOverlays(this)) {
            statusText.setText("مجوز پنجره شناور فعال است. می‌توانید دکمه مترجم را روشن کنید.");
        } else {
            statusText.setText("مجوز پنجره شناور هنوز فعال نشده است.");
        }
    }
}
