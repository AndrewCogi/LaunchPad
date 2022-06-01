package com.example.launchpad;

import android.graphics.Color;
import android.media.SoundPool;
import android.util.Log;
import android.widget.Button;

import java.util.Timer;
import java.util.TimerTask;

public class MetronomePlayer {
    private int bpm=120; // beats per minute
    private int measure=4; // measure
    private float volume=1.0F; // sound volume
    private volatile boolean playing=false; // check isPlaying
    private MainActivity act; // MainActivity
    private SoundPool sp; // sound container
    private int id1, id2; // metronome sound1, metronome sound2
    private Timer timer; // timer
    private TimerTask task=null; // timer task
    private int beat=1; // init beat

    // constructor
    public MetronomePlayer(MainActivity act, SoundPool sp, int id1, int id2) {
        this.act=act;
        this.sp=sp;
        this.id1=id1;
        this.id2=id2;
        this.timer=new Timer("metronomePlayer");
    }

    // set bpm
    public MetronomePlayer setBpm(int bpm) {
        bpm=(bpm<30)?30:bpm;
        bpm=(bpm>500)?500:bpm;
        this.bpm=bpm;
        return this;
    }

    // set measure
    public MetronomePlayer setMeasure(int measure){
        measure=(measure<1)?4:measure;
        this.measure=measure;
        return this;
    }

    // start metronome beat
    public void start(){
        playing=true;
        beat=1;
        long interval=Math.round(60000/bpm);
        task=new TimerTask() {
            @Override
            public void run()
            {
                advance();
            }
        };
        timer.scheduleAtFixedRate(task, 0l, interval);
    }

    private float recordLength; // record length
    private int checkBeat; // show beats in UI

    // start metronome without sound
    public void startNoSound(){
        recordLength=0;
        checkBeat=0;
        long interval=Math.round(60000/bpm)/5;
        task=new TimerTask() {
            @Override
            public void run() {
                advanceNoSound();
            }
        };
        timer.scheduleAtFixedRate(task, 0l, interval);
    }

    // continue beats without sound
    private void advanceNoSound(){
        if(playing) {
            recordLength+=0.2;
            act.runOnUiThread(new Runnable(){
                @Override
                public void run(){
                    Button mt = act.findViewById(R.id.center);
                    mt.setText(Integer.toString(checkBeat/5+1));
                    checkBeat++;
                }
            });
        }
        return;
    }

    // get recordLength
    public float getRecordLength(){
        Log.d("aa","AA: "+recordLength);
        return recordLength;
    }

    // start once
    public void startOnce(){
        playing=true;
        long interval=Math.round(60000/bpm);
        for(int bt = 1; bt<=measure; bt++){
            if(bt == 1){
                sp.play(id1, volume, volume, 0, 0, 1.0F);
            }
            else{
                sp.play(id2, volume, volume, 0, 0, 1.0F);
            }
            try { Thread.sleep(interval); } catch (InterruptedException e) {}
        }
    }

    // stop metronome beat
    public void stop(){
        task.cancel();
        task=null;
        playing=false;
    }

    // continue beats
    private void advance(){
        if(playing) {
            if(beat==1) {
                sp.play(id1, volume, volume, 0, 0, 1.0F);
            } else {
                sp.play(id2, volume, volume, 0, 0, 1.0F);
            }
            if(beat==measure) {
                beat=1;
            } else {
                beat++;
            }
        }
        return;
    }
}
