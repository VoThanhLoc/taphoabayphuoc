package com.example.taphoabayphuoc.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.taphoabayphuoc.R;
import com.example.taphoabayphuoc.activities.login.LoginActivity;
import com.example.taphoabayphuoc.utils.SessionManager;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        SessionManager session = new SessionManager(this);

        if (session.isLogin()) {
            startActivity(new Intent(this, MainActivity.class));
        } else {
            startActivity(new Intent(this, LoginActivity.class));
        }
        setContentView(R.layout.activity_splash);
        finish();
    }
}