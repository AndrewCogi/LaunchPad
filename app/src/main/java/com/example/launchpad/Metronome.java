package com.example.launchpad;

import android.media.AudioManager;
import android.media.SoundPool;
import android.util.Log;

public class Metronome {
    private MainActivity act; // main activity
    private int bpm; // beat per minute
    private boolean canPlay; // check can play (==isRunning)
    private int measure; // how many repeat in one cycle
    private int soundId1, soundId2; // beat sound
    private SoundPool sp = null; // sound pool
    private MetronomePlayer player; // metronome player

    // constructor
    public Metronome(MainActivity act, int bpm){
        this.act = act;
        this.canPlay = true;
        this.measure = 4;
        this.bpm = bpm;
        initSoundPool();
        this.player = new MetronomePlayer(act,sp,soundId1,soundId2);
    }

    // init sound pool & sound id
    private void initSoundPool(){
        this.sp = new SoundPool(1, AudioManager.STREAM_MUSIC,100);
        this.soundId1 = sp.load(act,R.raw.default_bar,1);
        this.soundId2 = sp.load(act,R.raw.default_beat,1);
    }

    // play metronome
    public void play(){
        canPlay = false;
        player.setBpm(bpm);
        player.setMeasure(measure);
        player.start();
    }

    // play metronome once
    public void playOnce(){
        player.setBpm(bpm);
        player.setMeasure(measure);
        player.startOnce();
    }

    // play without sound
    public void playNoSound(){
        player.setBpm(bpm);
        player.setMeasure(measure);
        player.startNoSound();
    }

    // stop metronome
    public void stop(){
        player.stop();
        canPlay = true;
    }

    // get canPlay
    public boolean getCanPlay(){
        return canPlay;
    }

    // set canPlay
    public void setCanPlay(boolean b){
        this.canPlay = b;
    }

    // set measure
    public void setMeasure(int n){
        this.measure = n;
    }

    // get measure
    public int getMeasure(){
        return measure;
    }

    // get recordLength
    public float getRecordLength(){
        return player.getRecordLength();
    }

    // get bpm
    public int getBpm(){
        return bpm;
    }

    // set bpm
    public void setBpm(int bpm){
        this.bpm = bpm;
    }
}
