package com.example.experimentaltriviagame;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.widget.TextView;

import android.content.Intent;
import android.widget.Button;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class MainActivity extends AppCompatActivity {

    private Button submit;
    private TextInputEditText answer;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        answer = findViewById(R.id.answer_input);
        submit = findViewById(R.id.submit_button);

        submit.setOnClickListener(v -> {
            String input = answer.getText().toString().toLowerCase();
            // create intent to communicate with the result activity
            Intent intent = new Intent(MainActivity.this, ResultActivity.class);
            // create key-value pair to send info to the result activity
            intent.putExtra("USER_ANSWER", input);
            // start the activity
            startActivity(intent);
        });
    }
}