package com.luxurytranslator.app;

import android.app.Service;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.IBinder;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class FloatingTranslatorService extends Service {
    private WindowManager windowManager;
    private View bubble;
    private WindowManager.LayoutParams params;

    @Override public void onCreate() {
        super.onCreate();
        windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
        TextView view = new TextView(this);
        view.setText("ترجمه");
        view.setTextColor(0xFFFFFFFF);
        view.setTextSize(13f);
        view.setGravity(Gravity.CENTER);
        view.setPadding(18, 12, 18, 12);
        view.setBackgroundColor(0xFF8C6A2F);
        bubble = view;

        int overlayType = android.os.Build.VERSION.SDK_INT >= 26
            ? WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            : WindowManager.LayoutParams.TYPE_PHONE;
        params = new WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            overlayType,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT);
        params.gravity = Gravity.TOP | Gravity.END;
        params.x = 24;
        params.y = 260;
        windowManager.addView(bubble, params);

        bubble.setOnTouchListener(new View.OnTouchListener() {
            private int startX, startY;
            private float downX, downY;
            @Override public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        startX = params.x; startY = params.y;
                        downX = event.getRawX(); downY = event.getRawY();
                        return true;
                    case MotionEvent.ACTION_MOVE:
                        params.x = startX - (int)(event.getRawX() - downX);
                        params.y = startY + (int)(event.getRawY() - downY);
                        windowManager.updateViewLayout(bubble, params);
                        return true;
                    case MotionEvent.ACTION_UP:
                        if (Math.abs(event.getRawX() - downX) < 8 && Math.abs(event.getRawY() - downY) < 8) {
                            Toast.makeText(FloatingTranslatorService.this,
                                "مرحله بعد: تشخیص متن و ترجمه زنده", Toast.LENGTH_SHORT).show();
                        }
                        return true;
                }
                return false;
            }
        });
    }

    @Override public void onDestroy() {
        if (windowManager != null && bubble != null) windowManager.removeView(bubble);
        super.onDestroy();
    }
    @Override public IBinder onBind(Intent intent) { return null; }
}
