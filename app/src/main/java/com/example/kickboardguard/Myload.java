package com.example.kickboardguard;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;

public class Myload extends AppCompatActivity {

    Button load_start;
    Button load_end;
    Button back_btn;

    private GpsTracker gpsTracker;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myload);
        load_start = (Button)findViewById(R.id.road_start);
        load_end = (Button)findViewById(R.id.road_end);
        back_btn = (Button)findViewById(R.id.back_button1);

        back_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });load_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent();

                gpsTracker = new GpsTracker(getApplicationContext());

                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                intent.putExtra("latitude_x",latitude);
                intent.putExtra("longitude_y", longitude);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        load_end.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();

                gpsTracker = new GpsTracker(getApplicationContext());

                double latitude = gpsTracker.getLatitude();
                double longitude = gpsTracker.getLongitude();

                intent.putExtra("latitude_x",latitude);
                intent.putExtra("longitude_y", longitude);
                setResult(RESULT_FIRST_USER, intent);
                finish();
            }
        });

    }
}
