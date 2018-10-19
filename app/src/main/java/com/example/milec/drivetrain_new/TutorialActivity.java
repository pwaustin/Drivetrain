package com.example.milec.drivetrain_new;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.widget.TextView;


public class TutorialActivity extends AppCompatActivity {

    TextView tv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        tv = findViewById(R.id.aboutText);

        loadText();

    }

    private void loadText(){

        String s = "Drivetrain is a platform that enables you to make predictions about the type of used car an individual might be interested in " +
                "based on the information you enter about them. To use it, simply press 'Query' and enter any information you have about the " +
                "individual. Enter as much or as little as you want- but more helps with accurate predictions! Then, hit the 'Query' button to see our prediction.";



        tv.setMovementMethod(new ScrollingMovementMethod());
        tv.setText(s);
    }
}
