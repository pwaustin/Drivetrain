package com.example.milec.drivetrain_new;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;


public class HomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


    }


    public void launchQueryActivity(View view) {
        Intent intent = new Intent(this, QueryActivity.class);

        startActivity(intent);
    }

    public void launchAboutActivity(View view) {
        Intent intent = new Intent(this, AboutActivity.class);

        startActivity(intent);
    }

    public void launchLogoutActivity(View view) {
        Intent intent = new Intent(this, LogoutActivity.class);

        startActivity(intent);
    }

    public void launchHistoryActivity(View view) {
        Intent intent = new Intent(this, HistoryActivity.class);

        startActivity(intent);
    }

    public void launchTutorialActivity(View view) {
        Intent intent = new Intent(this, TutorialActivity.class);

        startActivity(intent);
    }


}
