package com.example.timemanage;

import static android.content.Context.ACTIVITY_SERVICE;
import static android.content.Context.WINDOW_SERVICE;

import static androidx.core.content.ContextCompat.getSystemService;
import static androidx.core.content.ContextCompat.startForegroundService;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.PixelFormat;
import android.os.Build;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.os.Handler;
import android.os.SystemClock;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.SystemClock;
import android.text.format.Time;
import android.view.View;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.Toast;

import static java.security.AccessController.getContext;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.zip.Inflater;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link LockFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class LockFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    //静态全局变量用于计时
    private int startTime = 0;
    private long sum_time = 0;
    private Chronometer recordChronometer;
    private long recordingTime = 0;// 记录下来的总时间
    private Context context;
    private EditText editText;
    private View root;
    private boolean isGoTime = false;

    public LockFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment LockFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static LockFragment newInstance(String param1, String param2) {
        LockFragment fragment = new LockFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }
    //实现弹窗操作 当activity不在前台时 跳出弹窗


    public void checkIsBack() {
        if (isGoTime) {
            Toast.makeText(getActivity().getApplication(), "3秒后自动切换到前台", Toast.LENGTH_SHORT).show();
        }

        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                boolean foreground = isForeground(getActivity());

                if (foreground && isGoTime) {
                    Intent intent = new Intent(getActivity(), MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
                    startActivity(intent);
                    Log.e("TAG", "onPause: 是否在后台" + foreground);
                }


            }
        }, 3000); // 延迟3秒
    }


    @Override
    public void onPause() {
        super.onPause();
        // Activity 进入后台

        // 创建弹窗
//            final WindowManager.LayoutParams params = new WindowManager.LayoutParams(
//                    WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.WRAP_CONTENT,
//                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE |
//                            WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL |
//                            WindowManager.LayoutParams.FLAG_WATCH_OUTSIDE_TOUCH,
//                    PixelFormat.TRANSLUCENT
//            );
//
//            final WindowManager windowManager = (WindowManager) context.getApplicationContext().getSystemService(WINDOW_SERVICE);
//            LayoutInflater inflater = LayoutInflater.from(context.getApplicationContext());
//            View view = inflater.inflate(R.layout.pop_window, null);
//
//            windowManager.addView(view, params);
//
//            // 延迟3秒后关闭弹窗并返回到应用程序界面
    }

    protected void showDialog() {
        androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setIcon(R.drawable.clock);
        String ss = editText.getText().toString();
        builder.setTitle("温馨提示：").setMessage("您设置的时间" + ss + "(s)已到~~~")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (root == null) {
            //解析fragment的xml
            root = inflater.inflate(R.layout.fragment_lock, container, false);
        }
        final Chronometer chronometer = (Chronometer) root.findViewById(R.id.chronometer);
        Button btnStart = (Button) root.findViewById(R.id.btnStart);
        Button btnWait = (Button) root.findViewById(R.id.btnWait);
        Button btnStop = (Button) root.findViewById(R.id.btnStop);
        Button btnRest = (Button) root.findViewById(R.id.btnReset);
        //获取输入栏信息 并写入数据库
        editText = (EditText) root.findViewById(R.id.edt_settime);
        context = getActivity();

        btnStart.setVisibility(View.VISIBLE);
        btnWait.setVisibility(View.GONE);
        btnStop.setVisibility(View.GONE);
        btnRest.setVisibility(View.GONE);

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String ss = editText.getText().toString();
                if (ss.isEmpty()) {
                    Toast.makeText(context, "没有填写时间", Toast.LENGTH_LONG).show();
                    return;
                }
                if (!Settings.canDrawOverlays(getContext())) {
                    Toast.makeText(context, "需要悬浮窗权限", Toast.LENGTH_SHORT).show();

                    ((MainActivity)getActivity()).checkTopPermission();
                    return;
                }
                Toast t = Toast.makeText(context, "开始记时", Toast.LENGTH_LONG);

                t.show();

                if (!(ss.equals("") && ss != null)) {
                    startTime = Integer.parseInt(editText.getText().toString());
                }
                // 跳过已经记录了的时间，起到继续计时的作用
                chronometer.setBase(SystemClock.elapsedRealtime() - recordingTime);
                // 开始记时
                chronometer.start();
                ((MainActivity)getActivity()).hideNav();
                isGoTime = true;
                btnStart.setVisibility(View.GONE);
                btnWait.setVisibility(View.VISIBLE);
                btnStop.setVisibility(View.VISIBLE);
                btnRest.setVisibility(View.GONE);
            }
        });
        btnWait.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast t = Toast.makeText(context, "暂停记时", Toast.LENGTH_LONG);
                t.show();
                chronometer.stop();
                // 保存这次记录了的时间
                //SystemClock.elapsedRealtime()是系统启动到现在的毫秒数
                recordingTime = SystemClock.elapsedRealtime() - chronometer.getBase();//getBase():返回时间
                btnStart.setVisibility(View.VISIBLE);
                btnWait.setVisibility(View.GONE);
                btnStop.setVisibility(View.VISIBLE);
                btnRest.setVisibility(View.GONE);
                ((MainActivity)getActivity()).showNav();

                isGoTime = false;

            }
        });
        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
                builder.setIcon(R.drawable.clock);
                builder.setTitle("温馨提示：").setMessage("是否要停止计时")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // 停止
                                Toast t = Toast.makeText(context, "停止记时", Toast.LENGTH_LONG);
                                t.show();
                                //总时间
                                sum_time = (SystemClock.elapsedRealtime() - chronometer.getBase() - recordingTime) / (60 * 1000);
                                recordingTime = 0;
                                chronometer.stop();
                                btnStart.setVisibility(View.GONE);
                                btnWait.setVisibility(View.GONE);
                                btnStop.setVisibility(View.GONE);
                                btnRest.setVisibility(View.VISIBLE);
                                ((MainActivity)getActivity()).showNav();

                                isGoTime = false;

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                AlertDialog dialog = builder.create();
                dialog.show();




//                    recordChronometer.setBase(SystemClock.elapsedRealtime());
            }
        });
        // 重置
        btnRest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast t = Toast.makeText(context, "重置计时", Toast.LENGTH_LONG);
                t.show();
                recordingTime = 0;
                chronometer.setBase(SystemClock.elapsedRealtime());// setBase(long base):设置计时器的起始时间
                editText.setText(null);//输入框清空
                btnStart.setVisibility(View.VISIBLE);
                btnWait.setVisibility(View.GONE);
                btnStop.setVisibility(View.GONE);
                btnRest.setVisibility(View.GONE);
                ((MainActivity)getActivity()).showNav();

                isGoTime = false;

            }
        });
        chronometer.setOnChronometerTickListener(new Chronometer.OnChronometerTickListener() {
                                                     @Override
                                                     public void onChronometerTick(Chronometer chronometer) {
                                                         // 如果开始计时到现在超过了startime分钟
                                                         if (SystemClock.elapsedRealtime() - chronometer.getBase() > startTime * 60000) {
                                                             chronometer.stop();
                                                             //总时间
                                                             sum_time = Integer.parseInt(editText.getText().toString());
                                                             // 给用户提示
                                                             showDialog();
                                                             ((MainActivity)getActivity()).showNav();

                                                         }
                                                     }
                                                 }
        );
        EditText editText1 = (EditText) root.findViewById(R.id.edt_setschedule);
        //获取日程名称
        String daily = editText1.getText().toString();
        //获取当天日期
        LocalDate currentDate = LocalDate.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        String formattedDate = currentDate.format(formatter);
        //数据库
        SQLiteDatabase db = new DatabaseHelper(getContext(), "SCHEDULE.db", null, 1)
                .getWritableDatabase();
        ContentValues values = new ContentValues();
        Cursor cursor = db.query("SCHEDULE", null, null, null, null, null, null);
        int count = cursor.getCount();
        values.put("id", count + 1);
        values.put("schedule_name", String.valueOf(editText1.getText()));
        values.put("schedule_dateTime", formattedDate);
        values.put("schedule_sum_time", sum_time);
        db.insert("SCHEDULE", null, values);
        // Inflate the layout for this fragment
        return root;
    }

    /**
     * 判断某个activity是否在前台显示
     */
    public static boolean isForeground(Activity activity) {
        ActivityManager activityManager = (ActivityManager) activity.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        boolean isBackground = true;
        String processName = "empty";
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.processName.equals(activity.getPackageName())) {
                processName = appProcess.processName;
                if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_CACHED) {
                    isBackground = true;
                } else if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND
                        || appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_VISIBLE) {
                    isBackground = false;
                } else {
                    isBackground = true;
                }
            }
        }
        return isBackground;
    }

    /**
     * 判断某个界面是否在前台,返回true，为显示,否则不是
     */
    public static boolean isForeground(Activity context, String className) {
        if (context == null || TextUtils.isEmpty(className))
            return false;
        ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> list = am.getRunningTasks(1);
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;

            if (className.equals(cpn.getClassName()))
                return true;
        }
        return false;
    }
}