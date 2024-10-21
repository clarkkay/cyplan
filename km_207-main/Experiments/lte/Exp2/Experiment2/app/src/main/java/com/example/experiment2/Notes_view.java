package com.example.experiment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class Notes_view extends AppCompatActivity {

    private Button button;
    private TextView note;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notes_view);

        String text = getIntent().getStringExtra("userNote");

        note = findViewById(R.id.textView2);

        note.setText(text);

        button = findViewById(R.id.backButton);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Notes_view.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }
}