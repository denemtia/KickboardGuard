package com.example.kickboardguard;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceScreen;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class Settings extends PreferenceFragmentCompat {

    PreferenceScreen logout;
    AlertDialog.Builder builder;
    FirebaseUser currentUser;
    private FirebaseAuth mAuth;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = super.onCreateView(inflater, container, savedInstanceState);
       view.setBackgroundColor(getResources().getColor(android.R.color.white));
        return view;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        logout = (PreferenceScreen)findPreference("logout");
        mAuth = FirebaseAuth.getInstance();

        logout.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                //Log.d("x","들어옴");
                Dialog();
                return false;
            }
        });

    }

    public void Dialog(){
        builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("로그아웃");
        builder.setMessage("로그아웃 하시겠습니까?");
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

//    private void signOut(){
//        mAuth.signOut();
//
//        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(new ResultCallback<Status>() {
//            @Override
//            public void onResult(@NonNull Status status) {
//                updateUI();
//            }
//        });
//    }

    @Override
    public void onStart() {
        super.onStart();
        currentUser = mAuth.getCurrentUser();
        if (currentUser != null){
            Log.d("태그","구글로그인중...");
        }
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
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.setting_preference, rootKey);
    }








//    @Override
//    public boolean onPreferenceTreeClick(Preference preference) {
//        String key = preference.getKey();
//
//        if (key.equals("logout")){
//            builder.show();
//            return true;
//        }
//        return false;
//    }

//    private void updateUI(FirebaseUser user) {
//        if (user == null) {
//            Intent intent = new Intent(getContext(), Login.class);
//            startActivity(intent);
//        }
//    }

}
