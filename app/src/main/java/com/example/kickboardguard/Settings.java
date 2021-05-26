package com.example.kickboardguard;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListAdapter;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.preference.EditTextPreference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceGroup;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceScreen;

public class Settings extends PreferenceFragmentCompat {

    SharedPreferences pref;
    EditTextPreference name;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.setting_preference);

        //SharedPreference객체를 참조하여 설정상태에서 대한 제어 기능
        pref = PreferenceManager.getDefaultSharedPreferences(getActivity());

        name = (EditTextPreference) findPreference("name");

        if (!pref.getString("name","").equals("")){
            name.setSummary(pref.getString("name","닉네임을 입력하세요"));
        }

        pref.registerOnSharedPreferenceChangeListener(listener);

    }

    @Override
    public void onResume() {
        super.onResume();

        pref.registerOnSharedPreferenceChangeListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        pref.unregisterOnSharedPreferenceChangeListener(listener);
    }

    SharedPreferences.OnSharedPreferenceChangeListener listener = new SharedPreferences.OnSharedPreferenceChangeListener() {
        @Override
        public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
            if (key.equals("name")){
                EditTextPreference ep = (EditTextPreference) findPreference(key);
                ep.setSummary(pref.getString(key,""));
            }

           // ((BaseAdapter)getPreferenceScreen().get.notifyDataSetChanged();

        }

    };

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
       View view = super.onCreateView(inflater, container, savedInstanceState);
       view.setBackgroundColor(getResources().getColor(android.R.color.white));
        return view;
    }

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {

    }
}
