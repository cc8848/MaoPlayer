package com.mao.maoplayer;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    private MediaController controller;
    private int count=0;
    private VideoView mVideoView;
    private int mLastPlayedTime;
    private final String LAST_PLAYED_TIME = "LAST_TIME";
    private final String LAST_PLAYED_COUNT = "LAST_COUNT";
    private int count1=0;
    private int position;
    private ArrayList<String> listFileName = new ArrayList<>();
    private String path = Environment.getExternalStorageDirectory().getPath() + "/video/";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        //getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        initView();

        playFirstVideo();
        mVideoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override//播放完毕之后触发的方法
            public void onCompletion(MediaPlayer mediaPlayer) {
                if (count < (listFileName.size() - 1)) {
                    playVideo();
                } else if (count == (listFileName.size() - 1)) {
                    Log.d("MAO", "重新开始循环播放");
                    playFirstVideo();
                }

            }
        });
        mVideoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mediaPlayer, int i, int i1) {
                exit();
                return true;
            }
        });

    }

    /**
     *沉浸式模式显示状态栏
     */
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (hasFocus && Build.VERSION.SDK_INT >= 19) {
            View decorView = getWindow().getDecorView();
            decorView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY);
        }
    }

    private void initView() {

        mVideoView = (VideoView) findViewById(R.id.video_view);
        controller = new MediaController(this);
        controller.setMediaPlayer(mVideoView);
        mVideoView.setMediaController(controller);
        getAllFilename(path, listFileName);

    }
    private void playFirstVideo() {
        mVideoView.setVideoURI(Uri.parse(path + listFileName.get(0)));
        mVideoView.start();
        mVideoView.requestFocus();
        count = 0;
    }


    public void playVideo() {
        //count = r.nextInt(listFileName.size());
        Toast.makeText(this, listFileName.get(count), Toast.LENGTH_LONG).show();
        Log.d("MAO", listFileName.get(count));
        if (count < listFileName.size() - 1) {
            count++;
            position=count;
        }
        Uri uri = Uri.parse(path + listFileName.get(count));
        mVideoView.setVideoURI(uri);
        mVideoView.start();
        mVideoView.requestFocus();

    }


    public void getAllFilename(String path, ArrayList<String> fileName) {
        File file = new File(path);
        File[] files = file.listFiles();
        String[] names = file.list();
        if (names != null)
            fileName.addAll(Arrays.asList(names));
        for (File a : files) {
            if (a.isDirectory()) {
                getAllFilename(a.getName().toString(), fileName);
            }
        }
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void exit() {
        Toast.makeText(this, "播放视频错误", Toast.LENGTH_SHORT).show();
        finish();
    }


    protected void onPause() {
        super.onPause();
        mVideoView.pause();
        mLastPlayedTime = mVideoView.getCurrentPosition();

    }


    protected void onResume() {
        super.onResume();
        mVideoView.start();
        if (mLastPlayedTime > 0) {
            mVideoView.seekTo(mLastPlayedTime);

        }
    }


    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(LAST_PLAYED_TIME, mVideoView.getCurrentPosition());
        outState.putInt(LAST_PLAYED_COUNT,position);
    }


    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mLastPlayedTime = savedInstanceState.getInt(LAST_PLAYED_TIME);
        count1= (int) savedInstanceState.get(LAST_PLAYED_COUNT);
    }
}
