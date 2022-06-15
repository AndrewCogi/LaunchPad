package com.example.launchpad;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.text.Html;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private int octNum; // current octave number
    private Metronome player; // metronome player

    // onCreate method
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // set content view -> activity_main
        setContentView(R.layout.activity_main);
        // set oct_up & oct_dwn buttons
        setOctButtonListener();
        // set instrument fragments
        setInstrumentFragments();
        // set metronome
        setMetronome();
        // get permission
        getPermission();
        // set recorder & player buttons
        setRecordersAndPlayers();
        // set center button
        setCenter();
        // hide action bar
        ActionBar actionbar = getSupportActionBar();
        actionbar.hide();

        // init default octNum (3)
        octNum = 3;
    }

    private PianoFragment pianofragment = new PianoFragment(); // Fragment for Piano
    private GuitarFragment guitarfragment = new GuitarFragment(); // Fragment for Guitar
    private DrumFragment drumfragment = new DrumFragment(); // Fragment for Drum
    private BassFragment bassfragment = new BassFragment(); // Fragment for Bass

    // set instrument fragments
    public void setInstrumentFragments() {
        // set piano button
        Button pianoButton = findViewById(R.id.piano);
        // add click listener
        pianoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // change piano fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.padFragment, pianofragment).commit();
                // enable oct_up & oct_dwn
                findViewById(R.id.oct_up).setEnabled(true);
                findViewById(R.id.oct_dwn).setEnabled(true);
                // release fixed
                TextView txt = findViewById(R.id.show_oct);
                txt.setText("oct: 3");
                // set octNum = 3
                setOctNum(3);
                //toast message
                Toast.makeText(getApplicationContext(), "pad: Piano", Toast.LENGTH_SHORT).show();
            }
        });
        // set guitar button
        Button guitarButton = findViewById(R.id.guitar);
        // add click listener
        guitarButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // change guitar fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.padFragment, guitarfragment).commit();
                // disable oct_up & oct_dwn
                findViewById(R.id.oct_up).setEnabled(false);
                findViewById(R.id.oct_dwn).setEnabled(false);
                // fixed
                TextView txt = findViewById(R.id.show_oct);
                txt.setText("Fixed");
                // set octNum = 3
                setOctNum(3);
                //toast message
                Toast.makeText(getApplicationContext(), "pad: Guitar", Toast.LENGTH_SHORT).show();
            }
        });
        // set drum button
        Button drumButton = findViewById(R.id.drum);
        // add click listener
        drumButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // change drum fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.padFragment, drumfragment).commit();
                // disable oct_up & oct_dwn
                findViewById(R.id.oct_up).setEnabled(false);
                findViewById(R.id.oct_dwn).setEnabled(false);
                // fixed
                TextView txt = findViewById(R.id.show_oct);
                txt.setText("Fixed");
                // set octNum = 3
                setOctNum(3);
                //toast message
                Toast.makeText(getApplicationContext(), "pad: Drum", Toast.LENGTH_SHORT).show();
            }
        });
        // set bass button
        Button bassButton = findViewById(R.id.bass);
        // add click listener
        bassButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // change bass fragment
                getSupportFragmentManager().beginTransaction().replace(R.id.padFragment, bassfragment).commit();
                // disable oct_up & oct_dwn
                findViewById(R.id.oct_up).setEnabled(false);
                findViewById(R.id.oct_dwn).setEnabled(false);
                // fixed
                TextView txt = findViewById(R.id.show_oct);
                txt.setText("Fixed");
                // set octNum = 3
                setOctNum(3);
                //toast message
                Toast.makeText(getApplicationContext(), "pad: Bass", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // set oct_up & oct_dwn buttons
    public void setOctButtonListener() {
        // set oct_up
        Button upButton = findViewById(R.id.oct_up);
        // set click listener
        upButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // get text
                TextView txt = findViewById(R.id.show_oct);
                // octNum over check
                if (getOctNum() < 5) {
                    txt.setText("oct: " + (getOctNum() + 1));
                    setOctNum(Integer.parseInt(txt.getText().toString().split(" ")[1]));
                } else {
                    // abort
                    Toast.makeText(getApplicationContext(), "highest value!", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // set oct_dwn
        Button downButton = findViewById(R.id.oct_dwn);
        // set click listener
        downButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView txt = findViewById(R.id.show_oct);
                // octNum over check
                if (getOctNum() > 1) {
                    txt.setText("oct: " + (getOctNum() - 1));
                    setOctNum(Integer.parseInt(txt.getText().toString().split(" ")[1]));
                } else {
                    // abort
                    Toast.makeText(getApplicationContext(), "lowest value!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private AudioRecord mAudioRecord = null; // audio recorder
    private Thread[] mRecordThread = new Thread[8]; // music player record thread
    private boolean isRecording = false; // check is recording now
    private String[] mFilepath = new String[8]; // file path to save music
    private int mAudioSource = MediaRecorder.AudioSource.MIC; // audio source (mic)
    private int mSampleRate = 44100; // Hz
    private int mChannelCount = AudioFormat.CHANNEL_IN_STEREO; // audio channel
    private int mAudioFormat = AudioFormat.ENCODING_PCM_16BIT; // audio format
    private int mBufferSize = AudioTrack.getMinBufferSize(mSampleRate, mChannelCount, mAudioFormat); // buffer size
    private boolean[] isEmpty = new boolean[]{true,true,true,true,true,true,true,true}; // check isEmpty

    // get permission
    public void getPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);
        }
    }

    // set recordThread (buttonNum: 0~7)
    private void setRecordThread(int buttonNum){
        // make recording thread
        mRecordThread[buttonNum] = new Thread(new Runnable() {
            @Override
            public void run() {
                // data container
                byte[] readData = new byte[mBufferSize];
                // set filePath
                mFilepath[buttonNum] = getApplicationContext().getFilesDir().getAbsolutePath()+"/record_"+buttonNum+".pcm";
                FileOutputStream fos = null;
                try {
                    // init
                    fos = new FileOutputStream(mFilepath[buttonNum]);
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                // set count button init
                (MainActivity.this).runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Button cnt = findViewById(R.id.center);
                        cnt.setTextSize(19);
                        cnt.setTextColor(Color.WHITE);
                    }
                });

                // play metronome without sound
                player.playNoSound();

                // recording
                while (isRecording && player.getRecordLength() < player.getMeasure()*4-0.35) {
                    // check read data
                    int ret = mAudioRecord.read(readData, 0, mBufferSize);
                    Log.d("aa", "read bytes is " + ret);
                    // write data into file
                    try {
                        fos.write(readData, 0, mBufferSize); // write data
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                // if recording is successfully done,
                if(isRecording == true){
                    (MainActivity.this).runOnUiThread(new Runnable() { // for UI control
                        @Override
                        public void run() {
                            Button b;
                            // re-initialize center button
                            b = findViewById(R.id.center);
                            b.setText("▶");
                            b.setTextSize(25);
                            b.setTextColor(Color.GREEN);

                            // set record button "Ready"
                            switch(buttonNum){
                                case 0:
                                    b = findViewById(R.id.recordbtn1);
                                    b.setText("Ready");
                                    b.setTextColor(Color.YELLOW);
                                    break;
                                case 1:
                                    b = findViewById(R.id.recordbtn2);
                                    b.setText("Ready");
                                    b.setTextColor(Color.YELLOW);
                                    break;
                                case 2:
                                    b = findViewById(R.id.recordbtn3);
                                    b.setText("Ready");
                                    b.setTextColor(Color.YELLOW);
                                    break;
                                case 3:
                                    b = findViewById(R.id.recordbtn4);
                                    b.setText("Ready");
                                    b.setTextColor(Color.YELLOW);
                                    break;
                                case 4:
                                    b = findViewById(R.id.recordbtn5);
                                    b.setText("Ready");
                                    b.setTextColor(Color.YELLOW);
                                    break;
                                case 5:
                                    b = findViewById(R.id.recordbtn6);
                                    b.setText("Ready");
                                    b.setTextColor(Color.YELLOW);
                                    break;
                                case 6:
                                    b = findViewById(R.id.recordbtn7);
                                    b.setText("Ready");
                                    b.setTextColor(Color.YELLOW);
                                    break;
                                case 7:
                                    b = findViewById(R.id.recordbtn8);
                                    b.setText("Ready");
                                    b.setTextColor(Color.YELLOW);
                                    break;
                            }
                        }
                    });
                }

                // stop metronome
                player.stop();
                // destroy recorder
                mAudioRecord.stop();
                mAudioRecord.release();
                mAudioRecord = null;
                // change bool valeus
                isRecording = false;
                isEmpty[buttonNum] = false;
                // close strean
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // check complete
                Log.d("aa","mRecordThread is DEAD");
            }
        });
    }

    private AudioTrack[] mAudioTrack = new AudioTrack[8]; // audio track for playing file
    private Thread[] mPlayThread = new Thread[8]; // play thread
    private boolean[] isPlaying = new boolean[]{false,false,false,false,false,false,false,false}; // check now playing

    // set playerThread
    public void setPlayerThread(int buttonNum){
        mPlayThread[buttonNum] = new Thread(new Runnable() {
            @Override
            public void run() {
                // make write data
                byte[] writeData = new byte[mBufferSize];
                FileInputStream fis = null;
                try {
                    // check file path
                    Log.d("aa","filepath: "+mFilepath[buttonNum]);
                    fis = new FileInputStream(mFilepath[buttonNum]);
                }catch (FileNotFoundException e) {
                    e.printStackTrace();
                }

                DataInputStream dis = null;
                mAudioTrack[buttonNum] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM); // init audioTrack
                mAudioTrack[buttonNum].play();  // play before writing

                // playing
                while(isPlaying[buttonNum]) {
                    try {
                        // read data
                        dis = new DataInputStream(fis);
                        int ret = dis.read(writeData, 0, mBufferSize);
                        // if EOF, restart
                        if (ret <= 0) {
                            fis = new FileInputStream(mFilepath[buttonNum]);
                            dis = new DataInputStream(fis);
                        }
                        // play sound
                        mAudioTrack[buttonNum].write(writeData, 0, ret);
                    }catch (IOException e) {
                        e.printStackTrace();
                    }

                }

                // destroy audio track
                mAudioTrack[buttonNum].stop();
                mAudioTrack[buttonNum].release();
                mAudioTrack[buttonNum] = null;
                // close streams
                try {
                    dis.close();
                    fis.close();
                }catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    // set recorder & player buttons
    public void setRecordersAndPlayers() {
        // Button1
        Button btn1 = findViewById(R.id.recordbtn1);
        // add click listener
        btn1.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                // if recording, abort
                if(isRecording == true) return;
                // if metronome is on, abort
                if(player.getCanPlay() == false) return;
                // if center is playing, abort
                if(centerClicked == true) return;

                // start recording
                if(isRecording == false && isEmpty[0] == true){
                    isRecording = true;
                    Log.d("aa","start");
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn1);
                    b.setText("REC...");
                    b.setTextColor(Color.RED);
                    // init recorder
                    if (mAudioRecord == null) {
                        // make recorder
                        mAudioRecord = new AudioRecord(mAudioSource, mSampleRate, mChannelCount, mAudioFormat, mBufferSize);
                        mAudioRecord.startRecording();
                    }
                    // set record thread
                    setRecordThread(0);
                    // before start, play metronome once
                    player.playOnce();
                    // start recording
                    mRecordThread[0].start();
                }

                // stop playing
                else if(isPlaying[0] == true && isEmpty[0] == false) {
                    isPlaying[0] = false;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn1);
                    b.setText("READY");
                    b.setTextColor(Color.YELLOW);
                }

                // start playing
                else if(isPlaying[0] == false && isEmpty[0] == false){
                    isPlaying[0] = true;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn1);
                    b.setText("Playing");
                    b.setTextColor(Color.GREEN);
                    // init audio track
                    if(mAudioTrack[0] == null) {
                        mAudioTrack[0] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
                    }
                    // set player thread
                    setPlayerThread(0);
                    // start playing
                    mPlayThread[0].start();
                }
            }
        });

        // Button2
        Button btn2 = findViewById(R.id.recordbtn2);
        // add click listener
        btn2.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                // if recording, abort
                if(isRecording == true) return;
                // if metronome is on, abort
                if(player.getCanPlay() == false) return;
                // if center is playing, abort
                if(centerClicked == true) return;

                // start recording
                if(isRecording == false && isEmpty[1] == true){
                    isRecording = true;
                    Log.d("aa","start");
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn2);
                    b.setText("REC...");
                    b.setTextColor(Color.RED);
                    // init recorder
                    if (mAudioRecord == null) {
                        // make recorder
                        mAudioRecord = new AudioRecord(mAudioSource, mSampleRate, mChannelCount, mAudioFormat, mBufferSize);
                        mAudioRecord.startRecording();
                    }
                    // set record thread
                    setRecordThread(1);
                    // before start, play metronome once
                    player.playOnce();
                    // start recording
                    mRecordThread[1].start();
                }

                // stop playing
                else if(isPlaying[1] == true && isEmpty[1] == false) {
                    isPlaying[1] = false;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn2);
                    b.setText("READY");
                    b.setTextColor(Color.YELLOW);
                }

                // start playing
                else if(isPlaying[1] == false && isEmpty[1] == false){
                    isPlaying[1] = true;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn2);
                    b.setText("Playing");
                    b.setTextColor(Color.GREEN);
                    // init audio track
                    if(mAudioTrack[1] == null) {
                        mAudioTrack[1] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
                    }
                    // set player thread
                    setPlayerThread(1);
                    // start playing
                    mPlayThread[1].start();
                }
            }
        });

        // Button3
        Button btn3 = findViewById(R.id.recordbtn3);
        // set click listener
        btn3.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                // if recording, abort
                if(isRecording == true) return;
                // if metronome is on, abort
                if(player.getCanPlay() == false) return;
                // if center is playing, abort
                if(centerClicked == true) return;

                // start recording
                if(isRecording == false && isEmpty[2] == true){
                    isRecording = true;
                    Log.d("aa","start");
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn3);
                    b.setText("REC...");
                    b.setTextColor(Color.RED);
                    // init recorder
                    if (mAudioRecord == null) {
                        // make recorder
                        mAudioRecord = new AudioRecord(mAudioSource, mSampleRate, mChannelCount, mAudioFormat, mBufferSize);
                        mAudioRecord.startRecording();
                    }
                    // set record thread
                    setRecordThread(2);
                    // before start, play metronome once
                    player.playOnce();
                    // start recording
                    mRecordThread[2].start();
                }

                // stop playing
                else if(isPlaying[2] == true && isEmpty[2] == false) {
                    isPlaying[2] = false;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn3);
                    b.setText("READY");
                    b.setTextColor(Color.YELLOW);
                }

                // start playing
                else if(isPlaying[2] == false && isEmpty[2] == false){
                    isPlaying[2] = true;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn3);
                    b.setText("Playing");
                    b.setTextColor(Color.GREEN);
                    // init audio track
                    if(mAudioTrack[2] == null) {
                        mAudioTrack[2] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
                    }
                    // set player thread
                    setPlayerThread(2);
                    // start playing
                    mPlayThread[2].start();
                }
            }
        });

        // Button4
        Button btn4 = findViewById(R.id.recordbtn4);
        // set click listener
        btn4.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                // if recording, abort
                if(isRecording == true) return;
                // if metronome is on, abort
                if(player.getCanPlay() == false) return;
                // if center is playing, abort
                if(centerClicked == true) return;

                // start recording
                if(isRecording == false && isEmpty[3] == true){
                    isRecording = true;
                    Log.d("aa","start");
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn4);
                    b.setText("REC...");
                    b.setTextColor(Color.RED);
                    // init recorder
                    if (mAudioRecord == null) {
                        // make recorder
                        mAudioRecord = new AudioRecord(mAudioSource, mSampleRate, mChannelCount, mAudioFormat, mBufferSize);
                        mAudioRecord.startRecording();
                    }
                    // set record thread
                    setRecordThread(3);
                    // before start, play metronome once
                    player.playOnce();
                    // start recording
                    mRecordThread[3].start();
                }

                // stop playing
                else if(isPlaying[3] == true && isEmpty[3] == false) {
                    isPlaying[3] = false;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn4);
                    b.setText("READY");
                    b.setTextColor(Color.YELLOW);
                }

                // start playing
                else if(isPlaying[3] == false && isEmpty[3] == false){
                    isPlaying[3] = true;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn4);
                    b.setText("Playing");
                    b.setTextColor(Color.GREEN);
                    // init audio track
                    if(mAudioTrack[3] == null) {
                        mAudioTrack[3] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
                    }
                    // set player thread
                    setPlayerThread(3);
                    // start playing
                    mPlayThread[3].start();
                }
            }
        });

        // Button5
        Button btn5 = findViewById(R.id.recordbtn5);
        // set click listener
        btn5.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                // if recording, abort
                if(isRecording == true) return;
                // if metronome is on, abort
                if(player.getCanPlay() == false) return;
                // if center is playing, abort
                if(centerClicked == true) return;

                // start recording
                if(isRecording == false && isEmpty[4] == true){
                    isRecording = true;
                    Log.d("aa","start");
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn5);
                    b.setText("REC...");
                    b.setTextColor(Color.RED);
                    // init recorder
                    if (mAudioRecord == null) {
                        // make recorder
                        mAudioRecord = new AudioRecord(mAudioSource, mSampleRate, mChannelCount, mAudioFormat, mBufferSize);
                        mAudioRecord.startRecording();
                    }
                    // set record thread
                    setRecordThread(4);
                    // before start, play metronome once
                    player.playOnce();
                    // start recording
                    mRecordThread[4].start();
                }

                // stop playing
                else if(isPlaying[4] == true && isEmpty[4] == false) {
                    isPlaying[4] = false;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn5);
                    b.setText("READY");
                    b.setTextColor(Color.YELLOW);
                }

                // start playing
                else if(isPlaying[4] == false && isEmpty[4] == false){
                    isPlaying[4] = true;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn5);
                    b.setText("Playing");
                    b.setTextColor(Color.GREEN);
                    // init audio track
                    if(mAudioTrack[4] == null) {
                        mAudioTrack[4] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
                    }
                    // set player thread
                    setPlayerThread(4);
                    // start playing
                    mPlayThread[4].start();
                }
            }
        });

        // Button6
        Button btn6 = findViewById(R.id.recordbtn6);
        // set click listener
        btn6.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                // if recording, abort
                if(isRecording == true) return;
                // if metronome is on, abort
                if(player.getCanPlay() == false) return;
                // if center is playing, abort
                if(centerClicked == true) return;

                // start recording
                if(isRecording == false && isEmpty[5] == true){
                    isRecording = true;
                    Log.d("aa","start");
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn6);
                    b.setText("REC...");
                    b.setTextColor(Color.RED);
                    // init recorder
                    if (mAudioRecord == null) {
                        // make recorder
                        mAudioRecord = new AudioRecord(mAudioSource, mSampleRate, mChannelCount, mAudioFormat, mBufferSize);
                        mAudioRecord.startRecording();
                    }
                    // set record thread
                    setRecordThread(5);
                    // before start, play metronome once
                    player.playOnce();
                    // start recording
                    mRecordThread[5].start();
                }

                // stop playing
                else if(isPlaying[5] == true && isEmpty[5] == false) {
                    isPlaying[5] = false;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn6);
                    b.setText("READY");
                    b.setTextColor(Color.YELLOW);
                }

                // start playing
                else if(isPlaying[5] == false && isEmpty[5] == false){
                    isPlaying[5] = true;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn6);
                    b.setText("Playing");
                    b.setTextColor(Color.GREEN);
                    // init audio track
                    if(mAudioTrack[5] == null) {
                        mAudioTrack[5] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
                    }
                    // set player thread
                    setPlayerThread(5);
                    // start playing
                    mPlayThread[5].start();
                }
            }
        });

        // Button7
        Button btn7 = findViewById(R.id.recordbtn7);
        // add click listener
        btn7.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                // if recording, abort
                if(isRecording == true) return;
                // if metronome is on, abort
                if(player.getCanPlay() == false) return;
                // if center is playing, abort
                if(centerClicked == true) return;

                // start recording
                if(isRecording == false && isEmpty[6] == true){
                    isRecording = true;
                    Log.d("aa","start");
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn7);
                    b.setText("REC...");
                    b.setTextColor(Color.RED);
                    // init recorder
                    if (mAudioRecord == null) {
                        // make recorder
                        mAudioRecord = new AudioRecord(mAudioSource, mSampleRate, mChannelCount, mAudioFormat, mBufferSize);
                        mAudioRecord.startRecording();
                    }
                    // set record thread
                    setRecordThread(6);
                    // before start, play metronome once
                    player.playOnce();
                    // start recording
                    mRecordThread[6].start();
                }

                // stop playing
                else if(isPlaying[6] == true && isEmpty[6] == false) {
                    isPlaying[6] = false;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn7);
                    b.setText("READY");
                    b.setTextColor(Color.YELLOW);
                }

                // start playing
                else if(isPlaying[6] == false && isEmpty[6] == false){
                    isPlaying[6] = true;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn7);
                    b.setText("Playing");
                    b.setTextColor(Color.GREEN);
                    // init audio track
                    if(mAudioTrack[6] == null) {
                        mAudioTrack[6] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
                    }
                    // set player thread
                    setPlayerThread(6);
                    // start playing
                    mPlayThread[6].start();
                }
            }
        });

        // Button8
        Button btn8 = findViewById(R.id.recordbtn8);
        // add click listener
        btn8.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View view) {
                // if recording, abort
                if(isRecording == true) return;
                // if metronome is on, abort
                if(player.getCanPlay() == false) return;
                // if center is playing, abort
                if(centerClicked == true) return;

                // start recording
                if(isRecording == false && isEmpty[7] == true){
                    isRecording = true;
                    Log.d("aa","start");
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn8);
                    b.setText("REC...");
                    b.setTextColor(Color.RED);
                    // init recorder
                    if (mAudioRecord == null) {
                        // make recorder
                        mAudioRecord = new AudioRecord(mAudioSource, mSampleRate, mChannelCount, mAudioFormat, mBufferSize);
                        mAudioRecord.startRecording();
                    }
                    // set record thread
                    setRecordThread(7);
                    // before start, play metronome once
                    player.playOnce();
                    // start recording
                    mRecordThread[7].start();
                }

                // stop playing
                else if(isPlaying[7] == true && isEmpty[7] == false) {
                    isPlaying[7] = false;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn8);
                    b.setText("READY");
                    b.setTextColor(Color.YELLOW);
                }

                // start playing
                else if(isPlaying[7] == false && isEmpty[7] == false){
                    isPlaying[7] = true;
                    // set txt in button
                    Button b = findViewById(R.id.recordbtn8);
                    b.setText("Playing");
                    b.setTextColor(Color.GREEN);
                    // init audio track
                    if(mAudioTrack[7] == null) {
                        mAudioTrack[7] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
                    }
                    // set player thread
                    setPlayerThread(7);
                    // start playing
                    mPlayThread[7].start();
                }
            }
        });

        // Add long click button1
        btn1.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // if ready state
                if(isEmpty[0] == false && isPlaying[0] == false){
                    // allow recording
                    isEmpty[0] = true;
                    btn1.setText("");
                    Toast.makeText(MainActivity.this, "Delete Complete (Rc1)",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        // Add long click button2
        btn2.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // if ready state
                if(isEmpty[1] == false && isPlaying[1] == false){
                    // allow recording
                    isEmpty[1] = true;
                    btn2.setText("");
                    Toast.makeText(MainActivity.this, "Delete Complete (Rc2)",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        // Add long click button3
        btn3.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // if ready state
                if(isEmpty[2] == false && isPlaying[2] == false){
                    // allow recording
                    isEmpty[2] = true;
                    btn3.setText("");
                    Toast.makeText(MainActivity.this, "Delete Complete (Rc3)",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        // Add long click button4
        btn4.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // if ready state
                if(isEmpty[3] == false && isPlaying[3] == false){
                    // allow recording
                    isEmpty[3] = true;
                    btn4.setText("");
                    Toast.makeText(MainActivity.this, "Delete Complete (Rc4)",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        // Add long click button5
        btn5.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // if ready state
                if(isEmpty[4] == false && isPlaying[4] == false){
                    // allow recording
                    isEmpty[4] = true;
                    btn5.setText("");
                    Toast.makeText(MainActivity.this, "Delete Complete (Rc5)",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        // Add long click button6
        btn6.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // if ready state
                if(isEmpty[5] == false && isPlaying[5] == false){
                    // allow recording
                    isEmpty[5] = true;
                    btn6.setText("");
                    Toast.makeText(MainActivity.this, "Delete Complete (Rc6)",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        // Add long click button7
        btn7.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // if ready state
                if(isEmpty[6] == false && isPlaying[6] == false){
                    // allow recording
                    isEmpty[6] = true;
                    btn7.setText("");
                    Toast.makeText(MainActivity.this, "Delete Complete (Rc7)",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });

        // Add long click button8
        btn8.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                // if ready state
                if(isEmpty[7] == false && isPlaying[7] == false){
                    // allow recording
                    isEmpty[7] = true;
                    btn8.setText("");
                    Toast.makeText(MainActivity.this, "Delete Complete (Rc8)",Toast.LENGTH_SHORT).show();
                }
                return true;
            }
        });
    }

    private boolean centerClicked=false;

    // set center button
    public void setCenter(){
        // init center button
        Button center = findViewById(R.id.center);
        center.setText("▶");
        center.setTextColor(Color.GREEN);
        // add click listener
        center.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(centerClicked == false){
                    // recording... abort
                    if(isRecording == true) return;
                    // change center icon
                    Log.d("aa","All Start");
                    center.setText("■");
                    center.setTextColor(Color.RED);
                    // all player start
                    allStart();
                    // change bool value
                    centerClicked = true;
                }
                else{
                    // change center icon
                    Log.d("aa","All Stop");
                    center.setText("▶");
                    center.setTextColor(Color.GREEN);
                    // all player stop
                    allStop();
                    // change bool value
                    centerClicked = false;
                }
            }
        });
    }

    // all player start
    private void allStart(){
        Button b;
        // check btn1 is ready state
        if(isPlaying[0] == false && isEmpty[0] == false){
            isPlaying[0] = true;
            // set "Playing" this button
            b = findViewById(R.id.recordbtn1);
            b.setText("Playing");
            b.setTextColor(Color.GREEN);
            // init audio track
            if(mAudioTrack[0] == null) {
                mAudioTrack[0] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
            }
            // set player thread
            setPlayerThread(0);
            // start playing
            mPlayThread[0].start();
        }

        // check btn2 is ready state
        if(isPlaying[1] == false && isEmpty[1] == false){
            isPlaying[1] = true;
            // set "Playing" this button
            b = findViewById(R.id.recordbtn2);
            b.setText("Playing");
            b.setTextColor(Color.GREEN);
            // init audio track
            if(mAudioTrack[1] == null) {
                mAudioTrack[1] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
            }
            // set player thread
            setPlayerThread(1);
            // start playing
            mPlayThread[1].start();
        }

        // check btn3 is ready state
        if(isPlaying[2] == false && isEmpty[2] == false){
            isPlaying[2] = true;
            // set "Playing" this button
            b = findViewById(R.id.recordbtn3);
            b.setText("Playing");
            b.setTextColor(Color.GREEN);
            // init audio track
            if(mAudioTrack[2] == null) {
                mAudioTrack[2] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
            }
            // set player thread
            setPlayerThread(2);
            // start playing
            mPlayThread[2].start();
        }

        // check btn4 is ready state
        if(isPlaying[3] == false && isEmpty[3] == false){
            isPlaying[3] = true;
            // set "Playing" this button
            b = findViewById(R.id.recordbtn4);
            b.setText("Playing");
            b.setTextColor(Color.GREEN);
            // init audio track
            if(mAudioTrack[3] == null) {
                mAudioTrack[3] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
            }
            // set player thread
            setPlayerThread(3);
            // start playing
            mPlayThread[3].start();
        }

        // check btn5 is ready state
        if(isPlaying[4] == false && isEmpty[4] == false){
            isPlaying[4] = true;
            // set "Playing" this button
            b = findViewById(R.id.recordbtn5);
            b.setText("Playing");
            b.setTextColor(Color.GREEN);
            // init audio track
            if(mAudioTrack[4] == null) {
                mAudioTrack[4] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
            }
            // set player thread
            setPlayerThread(4);
            // start playing
            mPlayThread[4].start();
        }

        // check btn6 is ready state
        if(isPlaying[5] == false && isEmpty[5] == false){
            isPlaying[5] = true;
            // set "Playing" this button
            b = findViewById(R.id.recordbtn6);
            b.setText("Playing");
            b.setTextColor(Color.GREEN);
            // init audio track
            if(mAudioTrack[5] == null) {
                mAudioTrack[5] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
            }
            // set player thread
            setPlayerThread(5);
            // start playing
            mPlayThread[5].start();
        }

        // check btn7 is ready state
        if(isPlaying[6] == false && isEmpty[6] == false){
            isPlaying[6] = true;
            // set "Playing" this button
            b = findViewById(R.id.recordbtn7);
            b.setText("Playing");
            b.setTextColor(Color.GREEN);
            // init audio track
            if(mAudioTrack[6] == null) {
                mAudioTrack[6] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
            }
            // set player thread
            setPlayerThread(6);
            // start playing
            mPlayThread[6].start();
        }

        // check btn7 is ready state
        if(isPlaying[7] == false && isEmpty[7] == false){
            isPlaying[7] = true;
            // set "Playing" this button
            b = findViewById(R.id.recordbtn8);
            b.setText("Playing");
            b.setTextColor(Color.GREEN);
            // init audio track
            if(mAudioTrack[7] == null) {
                mAudioTrack[7] = new AudioTrack(AudioManager.STREAM_MUSIC, mSampleRate, mChannelCount, mAudioFormat, mBufferSize, AudioTrack.MODE_STREAM);
            }
            // set player thread
            setPlayerThread(7);
            // start playing
            mPlayThread[7].start();
        }
    }

    // all player stop
    private void allStop(){
        Button b;
        // check btn1 is playing state
        if(isPlaying[0] == true && isEmpty[0] == false){
            isPlaying[0] = false;
            // set "Ready" this button
            b = findViewById(R.id.recordbtn1);
            b.setText("READY");
            b.setTextColor(Color.YELLOW);
        }

        // check btn2 is playing state
        if(isPlaying[1] == true && isEmpty[1] == false){
            isPlaying[1] = false;
            // set "Ready" this button
            b = findViewById(R.id.recordbtn2);
            b.setText("READY");
            b.setTextColor(Color.YELLOW);
        }

        // check btn3 is playing state
        if(isPlaying[2] == true && isEmpty[2] == false){
            isPlaying[2] = false;
            // set "Ready" this button
            b = findViewById(R.id.recordbtn3);
            b.setText("READY");
            b.setTextColor(Color.YELLOW);
        }

        // check btn4 is playing state
        if(isPlaying[3] == true && isEmpty[3] == false){
            isPlaying[3] = false;
            // set "Ready" this button
            b = findViewById(R.id.recordbtn4);
            b.setText("READY");
            b.setTextColor(Color.YELLOW);
        }

        // check btn5 is playing state
        if(isPlaying[4] == true && isEmpty[4] == false){
            isPlaying[4] = false;
            // set "Ready" this button
            b = findViewById(R.id.recordbtn5);
            b.setText("READY");
            b.setTextColor(Color.YELLOW);
        }

        // check btn6 is playing state
        if(isPlaying[5] == true && isEmpty[5] == false){
            isPlaying[5] = false;
            // set "Ready" this button
            b = findViewById(R.id.recordbtn6);
            b.setText("READY");
            b.setTextColor(Color.YELLOW);
        }

        // check btn7 is playing state
        if(isPlaying[6] == true && isEmpty[6] == false){
            isPlaying[6] = false;
            // set "Ready" this button
            b = findViewById(R.id.recordbtn7);
            b.setText("READY");
            b.setTextColor(Color.YELLOW);
        }

        // check btn8 is playing state
        if(isPlaying[7] == true && isEmpty[7] == false){
            isPlaying[7] = false;
            // set "Ready" this button
            b = findViewById(R.id.recordbtn8);
            b.setText("READY");
            b.setTextColor(Color.YELLOW);
        }
    }

    // set metronome
    public void setMetronome(){
        // make instance Metronome class
        player = new Metronome(MainActivity.this,122);

        // get button info
        Button metronome = findViewById(R.id.metronome);
        // set text
        metronome.setText("OFF");
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(MainActivity.this, metronome);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.metronome_menu, popup.getMenu());

        // add button listener
        metronome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // is recording, abort
                if(!isRecording){
                    // start
                    if(player.getCanPlay()){
                        // set text
                        metronome.setText("ON");
                        player.play();
                        player.setCanPlay(false);
                    }
                    // stop
                    else{
                        // set text
                        metronome.setText("OFF");
                        player.stop();
                        player.setCanPlay(true);
                    }
                }
            }
        });

        // add button long click listener
        metronome.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                popup.show();//showing popup menu
                return true;
            }
        });

        // registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                switch(item.getItemId()) {
                    case R.id.FourToThree:
                        // change measure value
                        changeMeasureValue(3);
                        // toast message
                        Toast.makeText(MainActivity.this,"Now Meter : " + item.getTitle().toString().split(" ")[1], Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.FourToFour:
                        // change measure value
                        changeMeasureValue(4);
                        // toast message
                        Toast.makeText(MainActivity.this,"Now Meter : " + item.getTitle().toString().split(" ")[1], Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.EightToSix:
                        // change measure value
                        changeMeasureValue(6);
                        // toast message
                        Toast.makeText(MainActivity.this,"Now Meter : " + item.getTitle().toString().split(" ")[1], Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.largo:
                        // change bpm value
                        player.setBpm(45);
                        // toast message
                        Toast.makeText(MainActivity.this,"Now Tempo : " + player.getBpm()+"bpm", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.larghetto:
                        // change bpm value
                        player.setBpm(60);
                        // toast message
                        Toast.makeText(MainActivity.this, "Now Tempo : " + player.getBpm()+"bpm", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.adagio:
                        // change bpm value
                        player.setBpm(76);
                        // toast message
                        Toast.makeText(MainActivity.this, "Now Tempo : " + player.getBpm()+"bpm", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.andante:
                        // change bpm value
                        player.setBpm(96);
                        // toast message
                        Toast.makeText(MainActivity.this, "Now Tempo : " + player.getBpm()+"bpm", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.moderato:
                        // change bpm value
                        player.setBpm(122);
                        // toast message
                        Toast.makeText(MainActivity.this, "Now Tempo : " + player.getBpm()+"bpm", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.allegro:
                        // change bpm value
                        player.setBpm(144);
                        // toast message
                        Toast.makeText(MainActivity.this, "Now Tempo : " + player.getBpm()+"bpm", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.vivace:
                        // change bpm value
                        player.setBpm(168);
                        // toast message
                        Toast.makeText(MainActivity.this, "Now Tempo : " + player.getBpm()+"bpm", Toast.LENGTH_SHORT).show();
                        break;
                    case R.id.presto:
                        // change bpm value
                        player.setBpm(200);
                        // toast message
                        Toast.makeText(MainActivity.this, "Now Tempo : " + player.getBpm()+"bpm", Toast.LENGTH_SHORT).show();
                        break;
                }
                return true;
            }
        });
    }

    // change measure value safely
    private void changeMeasureValue(int newMeasure){
        if(!player.getCanPlay()){
            // stop metronome
            player.stop();
            player.setCanPlay(true);
            // change value
            player.setMeasure(newMeasure);
            // restart metronome
            player.play();
            player.setCanPlay(false);
        }
        else{
            // change value
            player.setMeasure(newMeasure);
        }
    }

    // get octNum
    public int getOctNum(){
        return octNum;
    }

    // set octNum
    public void setOctNum(int n){
        this.octNum = n;
    }
}