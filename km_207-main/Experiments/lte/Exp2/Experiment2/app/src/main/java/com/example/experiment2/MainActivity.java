package com.example.experiment2;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.google.android.material.textfield.TextInputEditText;

public class MainActivity extends AppCompatActivity {

    private Button button1;

    private TextInputEditText userInput;

    private String value;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button1 = findViewById(R.id.buttonID);

        userInput = findViewById(R.id.takeNote);

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                value = userInput.getText().toString();
                Intent intent = new Intent(MainActivity.this, Notes_view.class);
                intent.putExtra("userNote", value);
                startActivity(intent);
            }
        });
    }


}