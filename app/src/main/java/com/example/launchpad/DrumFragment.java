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

public class DrumFragment extends Fragment {
    private SoundPool soundPool; // sound container
    private final int[] drumSound = new int[61]; // drum sound
    private final Button[][] padButton = new Button[5][4]; // launchPad button
    private final int[][] buttonPressed = new int[13][13]; // check button pressed
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // init root view
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_drum, container, false);

        // MainActivity instance
        MainActivity act = (MainActivity) getActivity();

        // init soundPool >> maxMultiTouch: 3, quality: 100
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC,100);

        // set drum sounds
        setDrumSounds(act);

        // init pad buttons
        initPadButtons(rootView);

        // set pad button listener by octNum
        setPadButtonListener();

        // Inflate the layout for this fragment
        return rootView;
    }

    // set drum sounds
    private void setDrumSounds(MainActivity act){
        // drum sounds
        drumSound[25] = soundPool.load(act,R.raw.sound_drum_closed_high_hat,1);//
        drumSound[26] = soundPool.load(act,R.raw.sound_drum_opened_hgih_hat,1);
        drumSound[27] = soundPool.load(act,R.raw.sound_drum_pedal_high_hat,1);
        drumSound[28] = soundPool.load(act,R.raw.sound_drum_snare,1);
        drumSound[29] = soundPool.load(act,R.raw.sound_drum_kick,1);//
        drumSound[30] = soundPool.load(act,R.raw.sound_drum_lim_short,1);
        drumSound[31] = soundPool.load(act,R.raw.sound_drum_high_tam,1);
        drumSound[32] = soundPool.load(act,R.raw.sound_drum_low_tam,1);//
        drumSound[33] = soundPool.load(act,R.raw.sound_drum_floor_tam,1);
        drumSound[34] = soundPool.load(act,R.raw.sound_drum_crash,1);
        drumSound[35] = soundPool.load(act,R.raw.sound_drum_ride_symbals,1);
        drumSound[36] = soundPool.load(act,R.raw.sound_drum_ride_bell,1);
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
        buttonPressed[x][y]= soundPool.play(drumSound[(12*(act.getOctNum()-1))+(3*(x-1)+y)],1,1,0,0,1);
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