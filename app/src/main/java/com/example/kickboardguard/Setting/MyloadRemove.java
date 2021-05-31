package com.example.kickboardguard.Setting;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.preference.PreferenceManager;

import com.example.kickboardguard.MainActivity;
import com.example.kickboardguard.Myload;
import com.example.kickboardguard.R;

import java.util.ArrayList;

public class MyloadRemove extends AppCompatActivity {
    ListViewAdapter adapter;
    Button myload_btn;
    float routing_data;
    static final int REQ_MYLOAD = 1;
    String name;
    String email;
    String phone;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.listview_myload);

        ListView listView;

        adapter = new ListViewAdapter();
        routing_data = 0.0f;
        name = null;
        email = null;
        phone = null;

        myload_btn = (Button)findViewById(R.id.update_btn);
        listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(adapter);

        myload_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent myload = new Intent(getApplicationContext(), MainActivity.class);
                startActivityForResult(myload,REQ_MYLOAD);
            }
        });

        adapter.addItem(name,email,phone,routing_data);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ListViewItem item = (ListViewItem) parent.getItemAtPosition(position);

                String nameStr = item.getname();
                String emailStr = item.getemail();
                String phoneStr = item.getphone();
                Float myloadStr = item.getMyload();
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQ_MYLOAD){
            if (resultCode == RESULT_OK){
                routing_data = data.getFloatExtra("myload_result",0);
            }
        }
    }

    private void loadSharedPreferences(Context context){
        SharedPreferences sharedPreferences2 = getSharedPreferences(getString(R.string.name_key),MODE_PRIVATE);
        name = sharedPreferences2.getString("inputText","");
        SharedPreferences sharedPreferences3 = getSharedPreferences(getString(R.string.email_key),MODE_PRIVATE);
        email = sharedPreferences3.getString("inputText1","");
        SharedPreferences sharedPreferences4 = getSharedPreferences(getString(R.string.phone_key),MODE_PRIVATE);
        phone = sharedPreferences4.getString("inputText2","");

    }
}
