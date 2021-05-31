package com.example.kickboardguard.Setting;


import android.app.AlertDialog;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

import com.example.kickboardguard.Login;
import com.example.kickboardguard.R;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Settings extends PreferenceFragmentCompat implements SharedPreferences.OnSharedPreferenceChangeListener {

    private static final String TAG = "Settings";

    PreferenceScreen logout;
    EditTextPreference email;
    EditTextPreference name;
    EditTextPreference phone;
    AlertDialog.Builder builder;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;
    Preference imageViewPreference;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_preference);
        imageViewPreference = (Preference)findPreference("image_preference");
        logout = (PreferenceScreen)findPreference("logout");
        email = (EditTextPreference)findPreference(getString(R.string.email_key));
        name = (EditTextPreference)findPreference(getString(R.string.name_key));
        phone = (EditTextPreference)findPreference(getString(R.string.phone_key));
        setHasOptionsMenu(true);

        SharedPreferences sharedPrefs = PreferenceManager.getDefaultSharedPreferences(getContext());

        //갱신하는 곳
        onSharedPreferenceChanged(sharedPrefs, getString(R.string.email_key));
        onSharedPreferenceChanged(sharedPrefs, getString(R.string.name_key));
        onSharedPreferenceChanged(sharedPrefs, getString(R.string.phone_key));

        mAuth = FirebaseAuth.getInstance();

        email.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                email.setSummary(email.getText());
                return true;
            }
        });
        name.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                name.setSummary(name.getText());
                return true;
            }
        });
        phone.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                phone.setSummary(phone.getText());
                return true;
            }
        });
        imageViewPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Image_choose();
                return true;
            }
        });

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

    public  void Image_choose(){
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("이미지 변경");
        builder.setMessage("이미지 변경하기");
        builder.setPositiveButton("앨범", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        builder.setNegativeButton("취소",null);
        builder.show();

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
        Preference pref = findPreference(key);
        if (pref instanceof EditTextPreference){
            EditTextPreference listPref = (EditTextPreference) pref;
            pref.setSummary(listPref.getText());
        }
    }

    //    @Override
//    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CODE){
//            if (resultCode == RESULT_OK){
//                try {
//                    InputStream in = getActivity().getContentResolver().openInputStream(data.getData());
//
//                    Bitmap img = BitmapFactory.decodeStream(in);
//                    in.close();
//
//                    imageViewPreference.setBitmap(img);
//                }catch (Exception e){
//
//                }
//            }
//            else if (resultCode == RESULT_CANCELED){
//                Toast.makeText(getActivity(), "사진 선택 취소", Toast.LENGTH_LONG).show();
//            }
//        }
//    }
}
