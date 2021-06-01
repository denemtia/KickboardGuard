package com.example.kickboardguard.Setting;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.kickboardguard.MainActivity;
import com.example.kickboardguard.Myload;
import com.example.kickboardguard.R;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class MyloadRemove extends AppCompatActivity {
    private TextView textView;
    private ListView listView;
    private int i = -1;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_myload);
        textView = (TextView)findViewById(R.id.trackTitle);
        listView = (ListView)findViewById(R.id.trackListview);
        Button backbtn = (Button)findViewById(R.id.back_button2);
        final TrackDBhelper trackDBhelper = new TrackDBhelper(this);
        trackDBhelper.open();
        Cursor cursor = trackDBhelper.fetchAllListOrderByDec();
        if (cursor.getCount()==0){
            textView.setText("저장된 기록이 없습니다.");
            trackDBhelper.close();
        }
        else {
            try {
                while (!cursor.isAfterLast()) {
                    TrackAdapter trackAdapter = new TrackAdapter(getApplicationContext(), cursor);
                    listView.setAdapter(trackAdapter);
                    i++;
                    cursor.moveToNext();
                }
                textView.setText("저장된 기록 총 :"+" "+ i);
                trackDBhelper.close();
            }catch (IllegalStateException e){
                e.printStackTrace();
            }
        }

        backbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(MyloadRemove.this);
                alertDialog.setTitle("선택해주세요");
                alertDialog.setPositiveButton("목록 삭제", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            TrackDBhelper trackDBhelper = new TrackDBhelper(getApplicationContext());
                            trackDBhelper.open();
                            Cursor cursor = trackDBhelper.fetchAllListOrderByDec();
                            TrackAdapter trackAdapter = new TrackAdapter(getApplicationContext(), cursor);
                            Cursor cursor1 = (Cursor) trackAdapter.getItem(position);
                            int index = cursor1.getInt(cursor1.getColumnIndex(TrackDBhelper.KEY_TABLE_ID));
                            trackDBhelper.removeList(index);
                            Cursor newcursor = trackDBhelper.fetchAllListOrderByDec();
                            trackAdapter.changeCursor(newcursor);
                            listView.setAdapter(trackAdapter);
                            trackDBhelper.close();
                            i--;
                            textView.setText("저장된 기록 총 :" + " " + i);
                            Toast.makeText(getApplicationContext(), "기록이 삭제 되었습니다.", Toast.LENGTH_SHORT).show();
                        }catch (Exception e){
                            e.printStackTrace();
                            Toast.makeText(getApplicationContext(), "알 수 없는 오류 발생"+e, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                alertDialog.setNegativeButton("취소", null);
                alertDialog.create().show();

            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }
}
