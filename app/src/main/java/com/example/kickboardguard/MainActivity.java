package com.example.kickboardguard;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    ListView listview = null ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final String[] items = {"WHITE", "RED", "GREEN", "BLUE", "BLACK"} ;
        ArrayAdapter adapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, items) ;

        listview = (ListView) findViewById(R.id.drawer_menulist) ;
        listview.setAdapter(adapter) ;

        listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                TextView contentTextview = (TextView) findViewById(R.id.drawer_content) ;

                switch (position) {
                    case 0 : // WHITE

                        break ;
                    case 2 : // GREEN
                        contentTextview.setBackgroundColor(Color.rgb(0x00, 0xFF, 0x00)) ;
                        contentTextview.setTextColor(Color.rgb(0x00, 0x00, 0x00)) ;
                        contentTextview.setText("GREEN") ;
                        break ;
                    case 3 : // BLUE
                        contentTextview.setBackgroundColor(Color.rgb(0x00, 0x00, 0xFF)) ;
                        contentTextview.setTextColor(Color.rgb(0xFF, 0xFF, 0xFF)) ;
                        contentTextview.setText("BLUE") ;
                        break ;
                    case 4 : // BLACK
                        contentTextview.setBackgroundColor(Color.rgb(0x00, 0x00, 0x00)) ;
                        contentTextview.setTextColor(Color.rgb(0xFF, 0xFF, 0xFF)) ;
                        contentTextview.setText("BLACK") ;
                        break ;
                }

                // 코드 계속 ...
            }
        });

        listview.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView parent, View v, int position, long id) {

                // ... 코드 계속

                // close drawer.
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer) ;
                drawer.closeDrawer(Gravity.LEFT) ;
            }
        });
    }
}