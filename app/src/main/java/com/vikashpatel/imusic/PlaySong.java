package com.vikashpatel.imusic;

import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class PlaySong extends AppCompatActivity {
    TextView currentDuration;
    MediaPlayer mediaPlayer;
    ImageView next;
    ImageView pause;
    int position;
    ImageView previous;
    TextView totalDuration;
    SeekBar seekBar;
    ArrayList<File> songs;
    boolean stopThread;
    String textContent;
    TextView textView;
    Thread updateSeek;

    public void onDestroy() {
        super.onDestroy();
        stopThread = true;
        mediaPlayer.stop();
        mediaPlayer.reset();
        mediaPlayer.release();
    }
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        setContentView(R.layout.activity_play_song);
        textView = findViewById(R.id.textView);
        currentDuration = findViewById(R.id.currentDuration);
        totalDuration = findViewById(R.id.totalDuration);
        pause = findViewById(R.id.pause);
        previous = findViewById(R.id.previous);
        next = findViewById(R.id.next);
        seekBar = findViewById(R.id.seekBar);
        Intent intent = getIntent();
        songs = (ArrayList) intent.getExtras().getParcelableArrayList("songList");
        String stringExtra = intent.getStringExtra("currentSong");
        textContent = stringExtra;
        textView.setText(stringExtra);
        textView.setSelected(true);
        int intExtra = intent.getIntExtra("position", 0);
        position = intExtra;
        MediaPlayer create = MediaPlayer.create(this, Uri.parse(songs.get(intExtra).toString()));
        mediaPlayer = create;
        create.start();
        seekBar.setMax(this.mediaPlayer.getDuration());
        stopThread = false;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            public void onStartTrackingTouch(SeekBar seekBar) {
            }
            public void onProgressChanged(SeekBar seekBar, int i, boolean z) {
                Log.d("duration", mediaPlayer.getCurrentPosition() + " and " + mediaPlayer.getDuration());
                if (mediaPlayer.getCurrentPosition() >= mediaPlayer.getDuration() - 200) {
                    next.callOnClick();
                }
            }
            public void onStopTrackingTouch(SeekBar seekBar) {
                mediaPlayer.seekTo(seekBar.getProgress());
            }
        });
        updateSeek = new Thread() {
            public void run() {
                while (!stopThread) {
                    try {
                        if (mediaPlayer != null) {
                            long currentPosition = mediaPlayer.getCurrentPosition();
                            final String format = String.format("%02d:%02d", Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(currentPosition)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(currentPosition) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(currentPosition))));
                            runOnUiThread(new Runnable() {
                                public void run() {
                                    currentDuration.setText(format);
                                }
                            });
                            seekBar.setProgress(mediaPlayer.getCurrentPosition());
                            sleep(200);
                            Log.d("threadCode", "Updating Success");
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Log.d("threadCode", "Updating Failed");
                    }
                }
            }
        };
        pause.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (mediaPlayer.isPlaying()) {
                    pause.setImageResource(R.drawable.play);
                    mediaPlayer.pause();
                }
                else {
                    pause.setImageResource(R.drawable.pause);
                    mediaPlayer.start();
                }
            }
        });
        previous.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != 0) {
                    position--;
                } else {
                    position = songs.size() - 1;
                }
                Uri parse = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), parse);
                mediaPlayer.start();
                pause.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName();
                textView.setText(textContent);
                long duration = mediaPlayer.getDuration();
                totalDuration.setText(String.format("%02d:%02d", Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(duration)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)))));
            }
        });
        next.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                mediaPlayer.stop();
                mediaPlayer.release();
                if (position != songs.size() - 1) {
                    position++;
                } else {
                    position = 0;
                }
                Uri parse = Uri.parse(songs.get(position).toString());
                mediaPlayer = MediaPlayer.create(getApplicationContext(), parse);
                mediaPlayer.start();
                pause.setImageResource(R.drawable.pause);
                seekBar.setMax(mediaPlayer.getDuration());
                textContent = songs.get(position).getName();
                textView.setText(textContent);
                long duration = mediaPlayer.getDuration();
                totalDuration.setText(String.format("%02d:%02d", Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(duration)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)))));
            }
        });
        long duration = mediaPlayer.getDuration();
        totalDuration.setText(String.format("%02d:%02d", Long.valueOf(TimeUnit.MILLISECONDS.toMinutes(duration)), Long.valueOf(TimeUnit.MILLISECONDS.toSeconds(duration) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(duration)))));
        updateSeek.start();
    }
}

