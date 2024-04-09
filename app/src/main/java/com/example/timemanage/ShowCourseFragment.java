package com.example.timemanage;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ShowCourseFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ShowCourseFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private int week = 1;

    public ShowCourseFragment() {
        // Required empty public constructor
    }

    public ShowCourseFragment(int week) {
        this.week = week;
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ShowCourseFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ShowCourseFragment newInstance(String param1, String param2) {
        ShowCourseFragment fragment = new ShowCourseFragment();
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_show_course, container, false);
        LinearLayout linearLayout = root.findViewById(R.id.linear);

        String query = "SELECT * FROM course where week = "+ week;
        SQLiteDatabase db = new DatabaseHelper(getContext(),"Course.db",null,1)
                .getWritableDatabase();
        Cursor cursor = db.rawQuery(query, null);
        if(cursor.getCount() != 0) {
            while (cursor.moveToNext()) {
                @SuppressLint("Range") String name = cursor.getString(cursor.getColumnIndex("name"));
                @SuppressLint("Range") String startTime = cursor.getString(cursor.getColumnIndex("startTime"));
                @SuppressLint("Range") String endTime = cursor.getString(cursor.getColumnIndex("endTime"));
                @SuppressLint("Range") int week = cursor.getInt(cursor.getColumnIndex("week"));
                String week_string = switch (week) {
                    case 1 -> "周一";
                    case 2 -> "周二";
                    case 3 -> "周三";
                    case 4 -> "周四";
                    case 5 -> "周五";
                    case 6 -> "周六";
                    case 7 -> "周日";
                    default -> "周一";
                };
                CardView cardView = new CardView(getContext());
                LayoutInflater layoutInflater = getLayoutInflater();
                CardView card = (CardView) layoutInflater.inflate(R.layout.couerse_cardview, cardView, true);
                TextView text_name = card.findViewById(R.id.text_name);
                TextView text_week = card.findViewById(R.id.text_week);
                TextView text_start_time = card.findViewById(R.id.text_start_time);
                TextView text_end_time = card.findViewById(R.id.text_end_time);
                text_name.setText(name);
                text_week.setText(week_string);
                text_start_time.setText(startTime);
                text_end_time.setText(endTime);
                linearLayout.addView(card);
            }
        }
        // Inflate the layout for this fragment
        return root;
    }
}