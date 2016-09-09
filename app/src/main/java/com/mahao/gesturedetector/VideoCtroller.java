package com.mahao.gesturedetector;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.media.AudioManager;
import android.view.Display;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.VideoView;

import io.vov.vitamio.widget.MediaController;

/**
 * Created by Penghy on 2016/9/9.
 */

public class VideoCtroller extends MediaController {

    private Context mContext;
    private GestureDetector mGestureDetector;
    private Activity mActivity;
    private VideoView mVideoView;
    private int mVolume;
    private int mMaxVolume;
    private AudioManager mAudioManager;

    public VideoCtroller(Context context, VideoView videoView,Activity activity) {

        super(context);
        this.mContext = context;
        this.mActivity = activity;
        this.mVideoView = videoView;
        mMaxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        mGestureDetector = new GestureDetector(mContext, new MyGestureListener());
    }

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
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            float mOldX = e1.getX();  // 按下的控件位置
            float mOldY = e1.getY();

            int currentY = (int) e2.getY();  // 手指抬起的位置
            int currentX = (int) e2.getX();

            // 获取屏幕的宽高
            Display disp = mActivity.getWindowManager().getDefaultDisplay();
            Point point = new Point();
            disp.getSize(point);
            int screenX = point.x;
            int screenY = point.y;

            // 当开始滑动的位置在屏幕的右侧1/4位置时, 调节声音
            float  precent = (mOldY - currentY) / screenX;
            if(mOldX > screenX*3/4){



            }else if(mOldX < screenY/4){  // 调节音量

            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }
    }


    @Override
    protected View makeControllerView() {

        View view = LayoutInflater.from(mContext).inflate(R.layout.audio_player,null);

        return super.makeControllerView();
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

        if(mVolume == -1){

            mVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
            if(mVolume < 0){
                mVolume = 0;


            }
        }

    }

}














