package com.example.kickboardguard;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class SplashActivity extends AppCompatActivity {
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            Thread.sleep(3000); // splash 화면 대기 시간
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        startActivity(new Intent(this, Login.class)); // splash 화면이 끝난 뒤 띄울 Activity

        finish();
    }
}