package com.example.launchpad;

import android.annotation.SuppressLint;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class PianoFragment extends Fragment {
    private SoundPool soundPool; // sound container
    private final int[] pianoSound = new int[61]; // piano sound
    private final Button[][] padButton = new Button[5][4]; // launchPad button
    private final int[][] buttonPressed = new int[13][13]; // check button pressed
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // init root view
        ViewGroup rootView = (ViewGroup) inflater.inflate(R.layout.fragment_piano, container, false);

        // MainActivity instance
        MainActivity act = (MainActivity) getActivity();

        // init soundPool >> maxMultiTouch: 3, quality: 100
        soundPool = new SoundPool(3, AudioManager.STREAM_MUSIC,100);

        // set piano sounds
        setPianoSounds(act);

        // init pad buttons
        initPadButtons(rootView);

        // set pad button listener by octNum
        setPadButtonListener();

        // Inflate the layout for this fragment
        return rootView;
    }

    // set piano sounds
    private void setPianoSounds(MainActivity act){
        // piano sounds
        pianoSound[1] = soundPool.load(act,R.raw.sound_piano_c1_0,1);
        pianoSound[2] = soundPool.load(act,R.raw.sound_piano_c1_1,1);
        pianoSound[3] = soundPool.load(act,R.raw.sound_piano_d1_0,1);
        pianoSound[4] = soundPool.load(act,R.raw.sound_piano_d1_1,1);
        pianoSound[5] = soundPool.load(act,R.raw.sound_piano_e1_0,1);
        pianoSound[6] = soundPool.load(act,R.raw.sound_piano_f1_0,1);
        pianoSound[7] = soundPool.load(act,R.raw.sound_piano_f1_1,1);
        pianoSound[8] = soundPool.load(act,R.raw.sound_piano_g1_0,1);
        pianoSound[9] = soundPool.load(act,R.raw.sound_piano_g1_1,1);
        pianoSound[10] = soundPool.load(act,R.raw.sound_piano_a1_0,1);
        pianoSound[11] = soundPool.load(act,R.raw.sound_piano_a1_1,1);
        pianoSound[12] = soundPool.load(act,R.raw.sound_piano_b1_0,1);
        pianoSound[13] = soundPool.load(act,R.raw.sound_piano_c2_0,1);
        pianoSound[14] = soundPool.load(act,R.raw.sound_piano_c2_1,1);
        pianoSound[15] = soundPool.load(act,R.raw.sound_piano_d2_0,1);
        pianoSound[16] = soundPool.load(act,R.raw.sound_piano_d2_1,1);
        pianoSound[17] = soundPool.load(act,R.raw.sound_piano_e2_0,1);
        pianoSound[18] = soundPool.load(act,R.raw.sound_piano_f2_0,1);
        pianoSound[19] = soundPool.load(act,R.raw.sound_piano_f2_1,1);
        pianoSound[20] = soundPool.load(act,R.raw.sound_piano_g2_0,1);
        pianoSound[21] = soundPool.load(act,R.raw.sound_piano_g2_1,1);
        pianoSound[22] = soundPool.load(act,R.raw.sound_piano_a2_0,1);
        pianoSound[23] = soundPool.load(act,R.raw.sound_piano_a2_1,1);
        pianoSound[24] = soundPool.load(act,R.raw.sound_piano_b2_0,1);
        pianoSound[25] = soundPool.load(act,R.raw.sound_piano_c3_0,1);
        pianoSound[26] = soundPool.load(act,R.raw.sound_piano_c3_1,1);
        pianoSound[27] = soundPool.load(act,R.raw.sound_piano_d3_0,1);
        pianoSound[28] = soundPool.load(act,R.raw.sound_piano_d3_1,1);
        pianoSound[29] = soundPool.load(act,R.raw.sound_piano_e3_0,1);
        pianoSound[30] = soundPool.load(act,R.raw.sound_piano_f3_0,1);
        pianoSound[31] = soundPool.load(act,R.raw.sound_piano_f3_1,1);
        pianoSound[32] = soundPool.load(act,R.raw.sound_piano_g3_0,1);
        pianoSound[33] = soundPool.load(act,R.raw.sound_piano_g3_1,1);
        pianoSound[34] = soundPool.load(act,R.raw.sound_piano_a3_0,1);
        pianoSound[35] = soundPool.load(act,R.raw.sound_piano_a3_1,1);
        pianoSound[36] = soundPool.load(act,R.raw.sound_piano_b3_0,1);
        pianoSound[37] = soundPool.load(act,R.raw.sound_piano_c4_0,1);
        pianoSound[38] = soundPool.load(act,R.raw.sound_piano_c4_1,1);
        pianoSound[39] = soundPool.load(act,R.raw.sound_piano_d4_0,1);
        pianoSound[40] = soundPool.load(act,R.raw.sound_piano_d4_1,1);
        pianoSound[41] = soundPool.load(act,R.raw.sound_piano_e4_0,1);
        pianoSound[42] = soundPool.load(act,R.raw.sound_piano_f4_0,1);
        pianoSound[43] = soundPool.load(act,R.raw.sound_piano_f4_1,1);
        pianoSound[44] = soundPool.load(act,R.raw.sound_piano_g4_0,1);
        pianoSound[45] = soundPool.load(act,R.raw.sound_piano_g4_1,1);
        pianoSound[46] = soundPool.load(act,R.raw.sound_piano_a4_0,1);
        pianoSound[47] = soundPool.load(act,R.raw.sound_piano_a4_1,1);
        pianoSound[48] = soundPool.load(act,R.raw.sound_piano_b4_0,1);
        pianoSound[49] = soundPool.load(act,R.raw.sound_piano_c5_0,1);
        pianoSound[50] = soundPool.load(act,R.raw.sound_piano_c5_1,1);
        pianoSound[51] = soundPool.load(act,R.raw.sound_piano_d5_0,1);
        pianoSound[52] = soundPool.load(act,R.raw.sound_piano_d5_1,1);
        pianoSound[53] = soundPool.load(act,R.raw.sound_piano_e5_0,1);
        pianoSound[54] = soundPool.load(act,R.raw.sound_piano_f5_0,1);
        pianoSound[55] = soundPool.load(act,R.raw.sound_piano_f5_1,1);
        pianoSound[56] = soundPool.load(act,R.raw.sound_piano_g5_0,1);
        pianoSound[57] = soundPool.load(act,R.raw.sound_piano_g5_1,1);
        pianoSound[58] = soundPool.load(act,R.raw.sound_piano_a5_0,1);
        pianoSound[59] = soundPool.load(act,R.raw.sound_piano_a5_1,1);
        pianoSound[60] = soundPool.load(act,R.raw.sound_piano_b5_0,1);
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
        buttonPressed[x][y]= soundPool.play(pianoSound[(12*(act.getOctNum()-1))+(3*(x-1)+y)],1,1,0,0,1);
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