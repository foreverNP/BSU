package com.example.project1;

import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.widget.TextView;

import androidx.core.view.GestureDetectorCompat;

public class MainActivity extends Activity implements
        GestureDetector.OnGestureListener,
        GestureDetector.OnDoubleTapListener {

    private TextView tvOutput;
    private GestureDetectorCompat mDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvOutput = findViewById(R.id.textView1);

        mDetector = new GestureDetectorCompat(this, this);
        mDetector.setOnDoubleTapListener(this);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mDetector.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent event) {
        tvOutput.setText("onDown: " + event.toString());
        return true;
    }

    @Override
    public void onShowPress(MotionEvent event) {
        tvOutput.setText("onShowPress: " + event.toString());
    }

    @Override
    public boolean onSingleTapUp(MotionEvent event) {
        tvOutput.setText("onSingleTapUp: " + event.toString());
        return true;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        tvOutput.setText("onScroll: " + e1.toString() + " -> " + e2.toString());
        return true;
    }

    @Override
    public void onLongPress(MotionEvent event) {
        tvOutput.setText("onLongPress: " + event.toString());
    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        tvOutput.setText("onFling: " + e1.toString() + " -> " + e2.toString());
        return true;
    }

    @Override
    public boolean onDoubleTap(MotionEvent event) {
        tvOutput.setText("onDoubleTap: " + event.toString());
        return true;
    }

    @Override
    public boolean onDoubleTapEvent(MotionEvent event) {
        tvOutput.setText("onDoubleTapEvent: " + event.toString());
        return true;
    }

    @Override
    public boolean onSingleTapConfirmed(MotionEvent event) {
        tvOutput.setText("onSingleTapConfirmed: " + event.toString());
        return true;
    }
}
