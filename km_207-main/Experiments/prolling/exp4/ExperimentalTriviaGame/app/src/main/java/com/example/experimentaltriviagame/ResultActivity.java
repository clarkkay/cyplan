package com.example.experimentaltriviagame;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ResultActivity extends AppCompatActivity {
    private TextView answer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        answer = findViewById(R.id.result_text);
        String correctAnswer = "aglet";
        String userAnswer = getIntent().getStringExtra("USER_ANSWER");

        if (userAnswer.equals(correctAnswer)){
            answer.setText(userAnswer + "\nYou're correct!");
        } else {
            answer.setText(userAnswer + "\nYou're wrong :(");
        }
    }

}
