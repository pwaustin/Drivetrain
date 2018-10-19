package com.example.milec.drivetrain_new;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;

public class AboutActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tv = findViewById(R.id.aboutText);

        loadText();

    }

    private void loadText(){

        String s = "Drivetrain is a platform for knowledge discovery from a massive dataset of used car sales. " +
                "Enter information about an individual, and see the used cars our trained models predict that individual might buy. " +
                "Developed for CS 426 at UNR, Spring 2018.";

        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText(s);
    }
}
