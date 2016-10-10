package com.mahao.gesturedetector;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.os.Handler;
import android.os.Message;
import android.view.Display;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import io.vov.vitamio.widget.MediaController;
import io.vov.vitamio.widget.VideoView;

/**
 * Created by Penghy on 2016/9/9.
 */

public class VideoCtroller extends MediaController {

    private Context mContext;
    private GestureDetector mGestureDetector;
    private Activity mActivity;
    private VideoView mVideoView;
    private int mVolume = -1;
    private int mMaxVolume;
    private AudioManager mAudioManager;
    private float mBrighness = -1f;  // 当前亮度
    private int width;
    private String mVideoName;
    private RelativeLayout mVideoLayout;
    private ImageView mImgVolume;
    private TextView mTxtHint;
    private TextView mTxtTitle;
    private ImageButton mImgPlay;

    public VideoCtroller(Context context, VideoView videoView,Activity activity) {

        super(context);
        this.mContext = context;
        this.mActivity = activity;
        this.mVideoView = videoView;
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mGestureDetector = new GestureDetector(mContext, new MyGestureListener());
        WindowManager wm = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Point point = new Point();
        wm.getDefaultDisplay().getSize(point);
        width = point.x;
    }

    private Handler myHandler = new Handler(){

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){

                case 0:
                    mVideoLayout.setVisibility(View.GONE);
                    mTxtHint.setVisibility(View.GONE);
                    mImgPlay.setVisibility(View.VISIBLE);
                    break;
            }
            super.handleMessage(msg);
        }
    };

    class MyGestureListener extends GestureDetector.SimpleOnGestureListener{

        /**
         *  因为使用的是自定义的MediaController ，当显示后，mediaController会铺满屏幕
         *  所以ViedioView的点击事件会被拦截，所以重写控制器的手势事件
         *
         *  将全部的操作写在控制器中
         *  因为点击事件会被控制器拦截，无法传递到下层的额ViewdioView;
         *  所以  原来的单机 隐藏，就会失效，
         *  需要在手势中自定义点击 暂停和播放
         */
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {

            startAndStopPlayer();
            return super.onSingleTapConfirmed(e);
        }

        // 将事件拦截，不再向下传递
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return false;
        }

        // 双击播放和暂停
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            playOrPause();
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            mImgPlay.setVisibility(GONE);
            float mOldX = e1.getX();  // 按下的控件位置
            float mOldY = e1.getY();

            int currentX = (int) e2.getX();
            int currentY = (int) e2.getY();  // 手指抬起的位置

            // 获取屏幕的宽高
            Display disp = mActivity.getWindowManager().getDefaultDisplay();
            Point point = new Point();
            disp.getSize(point);
            int screenX = point.x;
            int screenY = point.y;

            // 当开始滑动的位置在屏幕的右侧1/4位置时, 调节声音
            float  precent = (mOldY - currentY) / screenY;
            if(mOldX > screenX*3/4){

                onVolumeSlide(precent);

            }else if(mOldX < screenX/4){  // 调节亮度

                onBrightNessSlide(precent);
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }

    @Override
    protected View makeControllerView() {

        View view = ((LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(getResources().getIdentifier("controller_view","layout",getContext().getPackageName()),this);
        view.setMinimumWidth(width);
        //获取控件
        mImgPlay = (ImageButton) view.findViewById(getResources().getIdentifier("mediacontroller_play_pause","id",mContext.getPackageName()));
        mTxtTitle = (TextView) view.findViewById(getResources().getIdentifier("video_title","id",mContext.getPackageName()));

        if(mTxtTitle != null){
            mTxtTitle.setText(mVideoName);
        }
        mVideoLayout = (RelativeLayout) view.findViewById(R.id.video_volume_brightness);
        mImgVolume = (ImageView) view.findViewById(R.id.video_volume);
        mTxtHint = (TextView) view.findViewById(R.id.video_volume_hint);
        ImageView imgBack = (ImageView) view.findViewById(R.id.video_back);
        mTxtHint.setVisibility(GONE);
        AudioManager mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        //注册事件监听
        imgBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mActivity.finish();
            }
        });
        return view;
    }

    //  处理手势识别
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if(mGestureDetector.onTouchEvent(event)){
            return true;
        }
        // 处理手势结束
        switch(event.getAction() & MotionEvent.ACTION_MASK){
            case MotionEvent.ACTION_UP:
                endGesture();
                break;
        }
        return super.onTouchEvent(event);
    }

    //设置视屏名字
    public void setVideoName(String name){

        mVideoName = name;
        if(mTxtTitle != null){
            mTxtTitle.setText(mVideoName);
        }
    }

    /**
     *  处理手势结束
     */
    private void endGesture() {
        mVolume = -1;
        mBrighness = -1;
        myHandler.removeMessages(0);
        myHandler.sendEmptyMessageDelayed(0,5);
    }


    //消费按键事件
    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return true;
    }

    // 显示进度条，或者隐藏精度条
    private void startAndStopPlayer() {

        if(isShowing()){
            hide();
        }else{
            show();
        }
    }

    // 调试音量大小
    private void onVolumeSlide(Float percent){

            if(mVolume == -1) {

                mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
                if (mVolume < 0) {
                    mVolume = 0;
                }
                //显示
                mVideoLayout.setVisibility(VISIBLE);
                mTxtHint.setVisibility(VISIBLE);
            }
            int index = (int) (percent*mMaxVolume + mVolume);
            if(index > mMaxVolume){
                index =mMaxVolume;
            }else if(index < 0){
                index = 0;
            }

            if(index >= 10){
                mImgVolume.setImageResource(R.drawable.volmn_100);
            }else if(index > 5 && index < 10){
                mImgVolume.setImageResource(R.drawable.volmn_60);
            }else if(index > 0 && index < 5){
                mImgVolume.setImageResource(R.drawable.volmn_30);
            }else{
                mImgVolume.setImageResource(R.drawable.volmn_no);
            }
            mTxtHint.setText((int)(((double)index / mMaxVolume)*100) + "%");

            //变更声音
            mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC,index,0);
    }

    // 滑动改变亮度
    private void onBrightNessSlide(Float percent){

        if(mBrighness < 0){

            mBrighness = mActivity.getWindow().getAttributes().screenBrightness;
            if(mBrighness <= 0.00f)
                mBrighness = 0.5f;
            if(mBrighness < 0.01){
                mBrighness = 0.01f;
            }
            //显示
            mVideoLayout.setVisibility(VISIBLE);
            mTxtHint.setVisibility(VISIBLE);
        }
        WindowManager.LayoutParams lpa = mActivity.getWindow().getAttributes();
        lpa.screenBrightness = mBrighness + percent;
        if(lpa.screenBrightness > 1.0f){
            lpa.screenBrightness = 1.0f;
        }else if(lpa.screenBrightness < 0.01f){
            lpa.screenBrightness = 0.01f;
        }
        mActivity.getWindow().setAttributes(lpa);
        mTxtHint.setText((int)(lpa.screenBrightness *100) + "%");

        if(lpa.screenBrightness *100 >= 90){
            mImgVolume.setImageResource(R.drawable.light_100);
        }else if(lpa.screenBrightness *100 > 80 && lpa.screenBrightness * 100 < 90){
            mImgVolume.setImageResource(R.drawable.light_90);
        }else if(lpa.screenBrightness* 100 > 70){
            mImgVolume.setImageResource(R.drawable.light_80);
        }else if(lpa.screenBrightness*100 > 60){
            mImgVolume.setImageResource(R.drawable.light_70);
        }else if(lpa.screenBrightness*100 > 50){
            mImgVolume.setImageResource(R.drawable.light_60);
        }else if(lpa.screenBrightness*100 > 40){
            mImgVolume.setImageResource(R.drawable.light_50);
        }else if(lpa.screenBrightness*100 > 30){
            mImgVolume.setImageResource(R.drawable.light_40);
        }else if (lpa.screenBrightness*100 > 20){
            mImgVolume.setImageResource(R.drawable.light_30);
        }else if(lpa.screenBrightness*100 > 10){
            mImgVolume.setImageResource(R.drawable.light_20);
        }
    }

    /**
     *  播放和暂停
     */
    private void playOrPause(){

        if(mVideoView != null){

            if(mVideoView.isPlaying()){
                mVideoView.pause();
                mImgPlay.setBackgroundResource(R.drawable.ic_player_play);
            }else {
                mVideoView.start();
                mImgPlay.setBackgroundResource(R.drawable.ic_player_pause);
            }
        }
    }
}














