package com.example.isai;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;

import java.io.File;
import java.util.ArrayList;

public class PlaySong extends AppCompatActivity {
    ImageView previous;
    ImageView pause;
    ImageView next;
    SeekBar seekBar;
    TextView textView;
    ArrayList<File> songs;
    MediaPlayer mediaPlayer;
    String textContent;
    int position;
    Thread updateSeek;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mediaPlayer.stop();
        mediaPlayer.release();
        updateSeek.interrupt();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_play_song);
        previous = findViewById(R.id.previous);
        pause = findViewById(R.id.pause);
        next = findViewById(R.id.next);
        textView = findViewById(R.id.textView);
        seekBar = findViewById(R.id.seekBar);

        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        songs = (ArrayList)bundle.getParcelableArrayList("songList");
        textContent = intent.getStringExtra("currentSong");
        position = intent.getIntExtra("position",0);
        textView.setText(textContent);
        textView.setSelected(true);

        Uri uri = Uri.parse(songs.get(position).toString());
        mediaPlayer = MediaPlayer.create(this,uri);
        mediaPlayer.start();
        seekBar.setMax(mediaPlayer.getDuration());

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            mediaPlayer.seekTo(seekBar.getProgress());
            }
        });

        updateSeek = new Thread(){
            @Override
            public void run() {
                super.run();
                int currentPosition = 0;
                try {
                    while(currentPosition < mediaPlayer.getDuration()){
                        currentPosition = mediaPlayer.getCurrentPosition();
                        seekBar.setProgress(currentPosition);
                        sleep(800);
                    }
                }catch (Exception e){
                    e.printStackTrace();
                }
            }
        };
        updateSeek.start();

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(mediaPlayer.isPlaying()){
                    pause.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }else{
                    pause.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });

        previous.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position == 0) {
                    position = songs.size() - 1;
                } else {
                    position = position - 1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if(position == songs.size()-1){
                    position = 0;
                }else{
                    position = position + 1;
                }
                Uri uri = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), uri);
                textContent = songs.get(position).getName().toString();
                textView.setText(textContent);
                mediaPlayer.start();
                seekBar.setMax(mediaPlayer.getDuration());
            }
        });




    }
}