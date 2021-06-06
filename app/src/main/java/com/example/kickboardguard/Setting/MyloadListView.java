package com.example.kickboardguard.Setting;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.kickboardguard.ImformationData;
import com.example.kickboardguard.MainActivity;
import com.example.kickboardguard.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MyloadListView extends AppCompatActivity {
    private static ImformationData Imdata;
    ImformationData data;
    private DatabaseReference mPostReference;
    private FirebaseDatabase mDatabase;
    private ChildEventListener mChild;
    private ListView listView;
    private ArrayAdapter<String> arrayAdapter;
    List<Object> Array = new ArrayList<Object>();

    static ArrayList<String> arrayIndex =  new ArrayList<String>();
    Button btn_update1;
    Button btn_back1;
    String name1;
    String email1;
    int id_cnt;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.myload_listview);
        btn_update1 =  (Button)findViewById(R.id.btn_update);
        btn_back1 = (Button)findViewById(R.id.btn_back3);

        listView = (ListView) findViewById(R.id.listviewmsg);
        id_cnt = 0;

        initDatabase();

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_dropdown_item_1line, new ArrayList<String>());
        listView.setAdapter(arrayAdapter);


        data = MainActivity.getData();
       // Toast.makeText(this,"거리 : "+String.valueOf(data.getDistance()),Toast.LENGTH_SHORT).show();

        FirebaseUser  user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            name1 = user.getDisplayName();
            email1 = user.getEmail();

            boolean emailVerified = user.isEmailVerified();
        }

        btn_back1.setOnClickListener(v -> {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        });
        btn_update1.setOnClickListener(v -> {
            if (!IsExistID()) {
                postFirebaseDatabase(true);
            }else{
                Toast.makeText(MyloadListView.this, "이미 존재하는 ID 입니다. 다른 ID로 설정해주세요.", Toast.LENGTH_LONG).show();
            }
        });
        getFirebaseDatabase();

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("Long Click", "position = " + position);
                AlertDialog.Builder dialog = new AlertDialog.Builder(MyloadListView.this);
                dialog.setTitle("데이터 삭제")
                        .setMessage("해당 데이터를 삭제 하시겠습니까?" + "\n")
                        .setPositiveButton("네", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                postFirebaseDatabase(false);
                                getFirebaseDatabase();
                                id_cnt--;
                                Toast.makeText(MyloadListView.this, "데이터를 삭제했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Toast.makeText(MyloadListView.this, "삭제를 취소했습니다.", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .create()
                        .show();
            }
        });



    }

    public void getFirebaseDatabase(){
        mPostReference = mDatabase.getReference("/id_list/");
        mPostReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                arrayAdapter.clear();
                arrayIndex.clear();
                for (DataSnapshot messageData : snapshot.getChildren()) {
                    String key = messageData.getKey();
                    String[] info = {name1,email1 ,"   "+String.valueOf(data.getDistance())};
                    String Result = setTextLength(info[0],5) + setTextLength(info[1],5) + setTextLength(info[2],5);
                    Array.add(Result);
                    arrayAdapter.add(Result);
                    Log.d("getFirebaseDatabase", "key: " + key);
                    Log.d("getFirebaseDatabase", "info: " + info[0] + info[1] + info[2]);

                }
                arrayAdapter.notifyDataSetChanged();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("getFirebaseDatabase","loadPost:onCancelled", error.toException());
            }
        });

    }

    public void postFirebaseDatabase(boolean add) {
        mPostReference = FirebaseDatabase.getInstance().getReference();
        Map<String, Object> childUpdates = new HashMap<>();
        Map<String, Object> postValues = null;
        if (add){
            Imdata= new ImformationData(id_cnt,name1,email1,data.getDistance());
            postValues = Imdata.toMap();
            id_cnt++;
        }
        childUpdates.put("/id_list/" + id_cnt,postValues);
        mPostReference.updateChildren(childUpdates);
    }

    private void initDatabase() {

        mDatabase = FirebaseDatabase.getInstance();

        mPostReference = mDatabase.getReference("log");
        mPostReference.child("log").setValue("check");

        mChild = new ChildEventListener() {

            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mPostReference.addChildEventListener(mChild);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPostReference.removeEventListener(mChild);
    }

    public boolean IsExistID(){
        boolean IsExist = arrayIndex.contains(id_cnt);
        return IsExist;
    }

    public String setTextLength(String text, int length){
        if(text.length()<length){
            int gap = length - text.length();
            for (int i=0; i<gap; i++){
                text = text + "  ";
            }
        }
        return text;
    }

}
