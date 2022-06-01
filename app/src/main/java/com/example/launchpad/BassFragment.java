package com.example.launchpad;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

public class BassFragment extends Fragment {
    private SoundPool soundPool; // sound container
    private final int[] bassSound = new int[61]; // bass sound
    private final Button[][] padButton = new Button[5][4]; // launchPad button
    private final int[][] buttonPressed = new int[13][13]; // check button pressed
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // init root view
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_bass, container, false);

        // MainActivity instance
        MainActivity act = (MainActivity) getActivity();

        // init soundPool >> maxMultiTouch: 3, quality: 100
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC,100);

        // set bass sounds
        setBassSounds(act);

        // init pad buttons
        initPadButtons(rootView);

        // set pad button listener by octNum
        setPadButtonListener();

        // Inflate the layout for this fragment
        return rootView;
    }

    // set bass sounds
    private void setBassSounds(MainActivity act){
        // bass sounds
        bassSound[25] = soundPool.load(act,R.raw.sound_bass_c_0,1);
        bassSound[26] = soundPool.load(act,R.raw.sound_bass_c_1,1);
        bassSound[27] = soundPool.load(act,R.raw.sound_bass_d_0,1);
        bassSound[28] = soundPool.load(act,R.raw.sound_bass_d_1,1);
        bassSound[29] = soundPool.load(act,R.raw.sound_bass_e_0,1);
        bassSound[30] = soundPool.load(act,R.raw.sound_bass_f_0,1);
        bassSound[31] = soundPool.load(act,R.raw.sound_bass_f_1,1);
        bassSound[32] = soundPool.load(act,R.raw.sound_bass_g_0,1);
        bassSound[33] = soundPool.load(act,R.raw.sound_bass_g_1,1);
        bassSound[34] = soundPool.load(act,R.raw.sound_bass_a_0,1);
        bassSound[35] = soundPool.load(act,R.raw.sound_bass_a_1,1);
        bassSound[36] = soundPool.load(act,R.raw.sound_bass_b_0,1);
    }

    // init pad buttons
    private void initPadButtons(ViewGroup rootView){
        padButton[1][1] = rootView.findViewById(R.id.button_1_1);
        padButton[1][2] = rootView.findViewById(R.id.button_1_2);
        padButton[1][3] = rootView.findViewById(R.id.button_1_3);
        padButton[2][1] = rootView.findViewById(R.id.button_2_1);
        padButton[2][2] = rootView.findViewById(R.id.button_2_2);
        padButton[2][3] = rootView.findViewById(R.id.button_2_3);
        padButton[3][1] = rootView.findViewById(R.id.button_3_1);
        padButton[3][2] = rootView.findViewById(R.id.button_3_2);
        padButton[3][3] = rootView.findViewById(R.id.button_3_3);
        padButton[4][1] = rootView.findViewById(R.id.button_4_1);
        padButton[4][2] = rootView.findViewById(R.id.button_4_2);
        padButton[4][3] = rootView.findViewById(R.id.button_4_3);
    }

    // play sound
    private void playSound(int x, int y){
        MainActivity act = (MainActivity) getActivity();
        buttonPressed[x][y]= soundPool.play(bassSound[(12*(act.getOctNum()-1))+(3*(x-1)+y)],1,1,0,0,1);
    }

    // set pad button listener
    @SuppressLint("ClickableViewAccessibility")
    private void setPadButtonListener(){
        // set listener (button_1_1)
        padButton[1][1].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playSound(1, 1);
                }
                return false;
            }
        });

        // set listener (button_1_2)
        padButton[1][2].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playSound(1, 2);
                }
                return false;
            }
        });

        // set listener (button_1_3)
        padButton[1][3].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playSound(1, 3);
                }
                return false;
            }
        });

        // set listener (button_2_1)
        padButton[2][1].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playSound(2, 1);
                }
                return false;
            }
        });

        // set listener (button_2_2)
        padButton[2][2].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playSound(2, 2);
                }
                return false;
            }
        });

        // set listener (button_2_3)
        padButton[2][3].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playSound(2, 3);
                }
                return false;
            }
        });

        // set listener (button_3_1)
        padButton[3][1].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playSound(3, 1);
                }
                return false;
            }
        });

        // set listener (button_3_2)
        padButton[3][2].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playSound(3, 2);
                }
                return false;
            }
        });

        // set listener (button_3_3)
        padButton[3][3].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playSound(3, 3);
                }
                return false;
            }
        });

        // set listener (button_4_1)
        padButton[4][1].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playSound(4, 1);
                }
                return false;
            }
        });

        // set listener (button_4_2)
        padButton[4][2].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playSound(4, 2);
                }
                return false;
            }
        });

        // set listener (button_4_3)
        padButton[4][3].setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    playSound(4, 3);
                }
                return false;
            }
        });
    }
}