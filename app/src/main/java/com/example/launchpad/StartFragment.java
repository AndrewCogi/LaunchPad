package com.example.launchpad;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;

public class StartFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_start, container, false);

        // get image button
        ImageButton touchToStart = rootView.findViewById(R.id.touchToStartImageButton);

        // add gif
        Glide.with(this).load(R.raw.test1).into(touchToStart);

        // add listener
        touchToStart.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // set content view -> activity_main
                MainActivity activity = (MainActivity) getActivity();
                activity.setContentView(R.layout.activity_main);
                // set oct_up & oct_dwn buttons
                activity.setOctButtonListener();
                // set instrument fragments
                activity.setInstrumentFragments();
                // set metronome
                activity.setMetronome();
                // get permission
                activity.getPermission();
                // set recorder & player buttons
                activity.setRecordersAndPlayers();
                // set center button
                activity.setCenter();
            }
        });

        return rootView;
    }
}