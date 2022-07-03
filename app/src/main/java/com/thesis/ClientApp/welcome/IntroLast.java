package com.thesis.ClientApp.welcome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.thesis.ClientApp.authEmail.SignIn;
import com.thesis.ClientApp.R;

public class IntroLast extends AppCompatActivity {

    Button button4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_intro_last);
        button4 = findViewById(R.id.button4);

        button4.setOnClickListener(v -> {
            Intent intent = new Intent(IntroLast.this, SignIn.class);
            startActivity(intent);
        });
    }
}
