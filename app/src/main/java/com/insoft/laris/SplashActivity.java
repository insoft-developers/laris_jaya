package com.insoft.laris;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ProgressBar;

import com.insoft.laris.utils.SessionServer;

public class SplashActivity extends AppCompatActivity {

    private int waktu_loading = 4000;
    private ProgressBar loading;
    SessionServer sessionServer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        loading = findViewById(R.id.loading);
        loading.setVisibility(View.VISIBLE);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                loading.setVisibility(View.GONE);
                Intent home = new Intent(SplashActivity.this,  LoginActivity.class);
                startActivity(home);
                finish();

            }

        },waktu_loading);

    }
}