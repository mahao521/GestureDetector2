package com.mahao.gesturedetector;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Random;

public class ThridActivity extends AppCompatActivity {

    private GestureDetector mGestureDetector;
    private TextView mLogcat;
    private StringBuilder sb ;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thrid);
        mGestureDetector = new GestureDetector(this,new MyNewGestor());
       // getLogCatInfo();

        mLogcat = (TextView) findViewById(R.id.txt_log_hint);
        sb = new StringBuilder();
        mRelativeLayout = (RelativeLayout) findViewById(R.id.activity_thrid);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
     mGestureDetector.onTouchEvent(event);
        switch (event.getAction()){

            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                break;
            case MotionEvent.ACTION_UP:
                break;
        }
        return super.onTouchEvent(event);
    }

    public void click(View view) {

        int id = view.getId();
        switch (id){
            case R.id.btn_get_log:

                if(sb!= null && sb.length() > 0){
                    mLogcat.setText(sb.toString());
                    Random random = new Random();
                    int r = random.nextInt(255)+1;
                    int g = random.nextInt(255)+1;
                    int b = random.nextInt(255)+1;
                    int argb = Color.rgb(r,g,b);
                    mLogcat.setTextColor(Color.BLUE);
                }
                break;
            case R.id.btn_remove_log:
                if(sb != null && sb.length() > 0){

                    sb.delete(0,sb.length());
                    mLogcat.setText("");
                }
                break;

            case R.id.btn_jump:
                Intent intent = new Intent(this,FirstActivity.class);
                startActivity(intent);
                break;
        }
    }

    class MyNewGestor extends GestureDetector.SimpleOnGestureListener{

        public MyNewGestor() {
            super();
        }

        @Override
        public boolean onSingleTapUp(MotionEvent e) {  // 单击事件
            Log.i("mahao","onSingleTabUp");
            sb.append("onSingleTabUp\n");
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {    //  长按事件
            Log.i("mahao","onLongPress");
            sb.append("onLongPress\n");
            super.onLongPress(e);
        }

        //滑动事件
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            Log.i("mahao","onScroll");
            sb.append("onScroll\n");
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        /**
         *
         * @param e1  按下的位置
         * @param e2  抬起的位置
         * @param velocityX   水平方向的速度
         * @param velocityY    数值方向上的速度
         * @return
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

            Log.i("mahao","onFling");
            sb.append("onFling\n");
            return super.onFling(e1, e2, velocityX, velocityY);
        }

        // 在touch后  事件确定之前 时触发；  比如快速点击不会触发，慢慢的点击会触发
        @Override
        public void onShowPress(MotionEvent e) {
            Log.i("mahao","onShowPress");
            sb.append("onShowPress\n");
            super.onShowPress(e);
        }

        // 点击事件
        @Override
        public boolean onDown(MotionEvent e) {
            Log.i("mahao","onDown");
            sb.append("onDown\n");
            return super.onDown(e);
        }

        // 双击事件
        // 执行过程 ondown---> onSingleTabUp----> onDoubleTab ---> onDoubleTabEvent
        // ---> onDown ---> onDoubleTabEvent;
        @Override
        public boolean onDoubleTap(MotionEvent e) {
            Log.i("mahao","onDoubleTap");
            sb.append("onDoubleTap\n");
            return super.onDoubleTap(e);
        }

        // 双击事件处理
        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            Log.i("mahao","onDoubleTabEvent");
            sb.append("onDoubleTabEvent\n");
            return super.onDoubleTapEvent(e);
        }

        // 点击事件确认
        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            Log.i("mahao","onSingleTabConfirm");
            sb.append("onSingleTabConfirm\n");
            return super.onSingleTapConfirmed(e);
        }

        @Override
        public boolean onContextClick(MotionEvent e) {
            Log.i("mahao","OnContextClick");
            sb.append("OnContextClick\n");
            return super.onContextClick(e);
        }
    }
}
