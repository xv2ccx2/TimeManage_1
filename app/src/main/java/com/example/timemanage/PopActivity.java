package com.example.timemanage;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.timemanage.popup.FloatingButtonService;
import com.example.timemanage.popup.FloatingImageDisplayService;
import com.example.timemanage.popup.FloatingVideoService;

public class PopActivity extends AppCompatActivity {
    private View displayView;
    private WindowManager windowManager;
    private WindowManager.LayoutParams layoutParams;


    private int[] images;
    private int imageIndex = 0;

    private Handler changeImageHandler;

    private Handler.Callback changeImageCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if (msg.what == 0) {
                imageIndex++;
                if (imageIndex >= 5) {
                    imageIndex = 0;
                }
                if (displayView != null) {
                    ((ImageView) displayView.findViewById(R.id.image_display_imageview)).setImageResource(images[imageIndex]);
                }

                changeImageHandler.sendEmptyMessageDelayed(0, 2000);
            }
            return false;
        }
    };

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;

        private boolean isMove = false;
        @Override
        public boolean onTouch(View view, MotionEvent event) {

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    isMove = false;
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    layoutParams.x = layoutParams.x + movedX;
                    layoutParams.y = layoutParams.y + movedY;
                    windowManager.updateViewLayout(view, layoutParams);
                    if(movedX>20||movedY>20) {
                        isMove = true;
                    }
                    break;
                case MotionEvent.ACTION_UP:
                    Log.e("TAG", "onTouch: "+isMove );
                    if(!isMove) {
                        windowManager.removeView(displayView);
                    }
                default:
                    break;
            }
            return false;
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.popup);
//        startFloatingImageDisplayService();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                startService(new Intent(PopActivity.this, FloatingButtonService.class));
            }
        } else if (requestCode == 1) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                startService(new Intent(PopActivity.this, FloatingImageDisplayService.class));
            }
        } else if (requestCode == 2) {
            if (!Settings.canDrawOverlays(this)) {
                Toast.makeText(this, "授权失败", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "授权成功", Toast.LENGTH_SHORT).show();
                startService(new Intent(PopActivity.this, FloatingVideoService.class));
            }
        }
    }

    public void startFloatingButtonService(View view) {
        if (FloatingButtonService.isStarted) {
            return;
        }
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 0);
        } else {
            startService(new Intent(PopActivity.this, FloatingButtonService.class));
        }
    }

    public void startFloatingImageDisplayService() {
        if (FloatingImageDisplayService.isStarted) {
            return;
        }
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 1);
        } else {
//            startService(new Intent(PopActivity.this, FloatingImageDisplayService.class));
            windowManager = (WindowManager) getSystemService(WINDOW_SERVICE);
            layoutParams = new WindowManager.LayoutParams();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            } else {
                layoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
            }
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.gravity = Gravity.LEFT | Gravity.TOP;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.width = 500;
            layoutParams.height = 500;
            layoutParams.x = 300;
            layoutParams.y = 300;

            images = new int[] {
                    R.drawable.ic_home,
                    R.drawable.clock,
                    R.drawable.ic_add,
                    R.drawable.ic_list,
                    R.drawable.ic_statistics,
                    R.drawable.ic_launcher_background
            };
            changeImageHandler = new Handler(this.getMainLooper(), changeImageCallback);


            LayoutInflater layoutInflater = LayoutInflater.from(this);
            displayView = layoutInflater.inflate(R.layout.image_display, null);
            displayView.setOnTouchListener(new FloatingOnTouchListener());
            ImageView imageView = displayView.findViewById(R.id.image_display_imageview);
            imageView.setImageResource(images[imageIndex]);
            windowManager.addView(displayView, layoutParams);
            changeImageHandler.sendEmptyMessageDelayed(0, 3000);


        }
    }

    public void startFloatingVideoService(View view) {
        if (FloatingVideoService.isStarted) {
            return;
        }
        if (!Settings.canDrawOverlays(this)) {
            Toast.makeText(this, "当前无权限，请授权", Toast.LENGTH_SHORT);
            startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName())), 2);
        } else {
            startService(new Intent(PopActivity.this, FloatingVideoService.class));
        }
    }
}