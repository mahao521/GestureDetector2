package com.mahao.gesturedetector;

import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import io.vov.vitamio.MediaPlayer;
import io.vov.vitamio.Vitamio;
import io.vov.vitamio.widget.VideoView;

public class SecondActivity extends AppCompatActivity implements MediaPlayer.OnInfoListener, MediaPlayer.OnBufferingUpdateListener {

   //视频地址
    private String path = "http://baobab.wdjcdn.com/145076769089714.mp4";
    private Uri uri;
    private ProgressBar pb;
    private TextView  downLoadRate, loadRate;
    private VideoCtroller mController;
    private VideoView mVideoView;

    // 问题1 ：和哔哩哔哩的视频手势差别： 这个进度条点击屏幕的时候一定会出现，而bilibili没有。
    // 问题2 ： 使用Vitamio 框架，这个进度条和 前后时间的显示，ID必须是规定的。


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Vitamio.isInitialized(this);

        // 定义全屏参数
        int  flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;

        // 去掉标题栏,在setview之前调用
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_second);

        // 获取窗体对象
        Window window = SecondActivity.this.getWindow();
        // 设置全屏，去掉导航栏
        window.setFlags(flag,flag);

        downLoadRate = (TextView) this.findViewById(R.id.download_rate);
        loadRate = (TextView) findViewById(R.id.load_rate);
        mVideoView  = (VideoView) findViewById(R.id.video_view);
        mController = new VideoCtroller(this,mVideoView,this);
        mController.setVideoName("人定胜天");
        pb = (ProgressBar) findViewById(R.id.probar);

        initData();
    }

    private void initData() {

        uri = Uri.parse(path);
        mVideoView.setVideoURI(uri);
        mVideoView.setMediaController(mController);
        mVideoView.setVideoQuality(MediaPlayer.VIDEOQUALITY_HIGH);
        mController.show(5000);
        mVideoView.requestFocus();
        mVideoView.setOnInfoListener(this);
        mVideoView.setOnBufferingUpdateListener(this);
        mVideoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.setPlaybackSpeed(1.0f);
            }
        });
    }

    @Override
    public boolean onInfo(MediaPlayer mp, int what, int extra) {

        switch (what){

            case MediaPlayer.MEDIA_INFO_BUFFERING_START:  // 暂停缓冲更多数据
                if(mVideoView.isPlaying()){

                    mVideoView.pause();
                    pb.setVisibility(View.VISIBLE);
                    downLoadRate.setText("");
                    loadRate.setText("");
                    downLoadRate.setVisibility(View.VISIBLE);
                    loadRate.setVisibility(View.VISIBLE);
                }
                break;
            case MediaPlayer.MEDIA_INFO_BUFFERING_END: // 缓冲完成继续播放
                mVideoView.start();
                pb.setVisibility(View.GONE);
                downLoadRate.setVisibility(View.GONE);
                loadRate.setVisibility(View.GONE);
                break;
            case MediaPlayer.MEDIA_INFO_DOWNLOAD_RATE_CHANGED: // 下载的速度
                downLoadRate.setText(extra+"kb/s"+"  ");
        }
        return true;
    }

    @Override
    public void onBufferingUpdate(MediaPlayer mp, int percent) {

        loadRate.setText(percent + "%");
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {

        // 屏幕切换，设置全屏
        if(mVideoView != null ){
            mVideoView.setVideoLayout(VideoView.VIDEO_LAYOUT_SCALE,0);
        }
        super.onConfigurationChanged(newConfig);
    }
}
