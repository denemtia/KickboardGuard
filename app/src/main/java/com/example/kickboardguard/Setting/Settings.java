package com.example.kickboardguard.Setting;


import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.example.kickboardguard.ImformationData;
import com.example.kickboardguard.Login;
import com.example.kickboardguard.MainActivity;
import com.example.kickboardguard.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Settings extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "Settings";

    PreferenceScreen logout;
    PreferenceScreen email;
    PreferenceScreen name;
    AlertDialog.Builder builder;
    FirebaseUser currentUser;
    FirebaseUser user;
    private FirebaseAuth mAuth;
    String name1;
    String email1;
    Uri photoUrl;
    ImformationData data;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preference);

        logout = (PreferenceScreen)findPreference("logout");
        email = (PreferenceScreen)findPreference(getString(R.string.email_key));
        name = (PreferenceScreen)findPreference(getString(R.string.name_key));
        setHasOptionsMenu(true);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        //갱신하는 곳
        onSharedPreferenceChanged(sharedPrefs, getString(R.string.email_key));
        onSharedPreferenceChanged(sharedPrefs, getString(R.string.name_key));

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
             name1 = user.getDisplayName();
             email1 = user.getEmail();
             photoUrl = user.getPhotoUrl();

            boolean emailVerified = user.isEmailVerified();
        }

        email.setSummary(email1);
        name.setSummary(name1);


        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Log.d("x","들어옴");
                Dialog();
                return true;
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceScreen().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
    }

    public void Dialog(){
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("로그아웃");
        builder.setMessage("로그아웃 하시겠습니까?(로그인화면으로 이동합니다.");
        builder.setPositiveButton("로그아웃", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                FirebaseAuth.getInstance().signOut();
                updateUI();
                //Log.d("로그아웃","로그아웃함");
            }
        });
        builder.setNegativeButton("취소", null);
        builder.show();
    }

    private void updateUI() { //update ui code here
        currentUser = mAuth.getCurrentUser();
        if (currentUser == null){
            Intent intent = new Intent(getActivity(), Login.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
    }

     @Override
     public void onCreatePreferences (Bundle savedInstanceState, String rootKey){
     }

    @Override
    public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {

    }
}
