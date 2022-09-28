package com.madmobiledevs.ecommerce;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.TextView;

public class NWLaunchActivity extends AppCompatActivity {

    ImageView splash_Logo;
    TextView splash_TextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.nwlaunch_activity);

        splash_Logo= findViewById(R.id.app_Logo);
        splash_TextView= findViewById(R.id.logo_Text);

        splash_TextView.setX(-2000);
        splash_TextView.animate().translationXBy(2000).setDuration(500);

        splash_Logo.setY(-2000);
        splash_Logo.animate().translationYBy(2000).setDuration(500);

        CountDownTimer countDownTimer = new CountDownTimer(1000,500) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                Intent intent = new Intent(NWLaunchActivity.this,HomeActivity.class);
                finishAffinity();
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
            }
        }.start();



    }
}