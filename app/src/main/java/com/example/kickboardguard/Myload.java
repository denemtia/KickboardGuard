package com.example.kickboardguard;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

public class Myload extends Fragment {

    MainActivity activit;
    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        activit = (MainActivity)getActivity();
    }

    @Override
    public void onDetach(){
        super.onDetach();
        activit = null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_myload, container, false);
    }
}