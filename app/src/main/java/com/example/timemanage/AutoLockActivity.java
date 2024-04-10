package com.example.timemanage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class AutoLockActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auto_lock);
        Button button = findViewById(R.id.yes_btn);
        TextView textView = findViewById(R.id.question);
        EditText editText = findViewById(R.id.answer_edt);
        Context context = this;

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String s = editText.getText().toString();
                if(s.equals("123")){
                    Intent intent = new Intent(AutoLockActivity.this,MainActivity.class);
                    startActivity(intent);
                    AutoLockActivity.this.finish();
                }
            }
        });

    }
}