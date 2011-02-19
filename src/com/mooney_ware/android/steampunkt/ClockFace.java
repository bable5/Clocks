package com.mooney_ware.android.steampunkt;

import java.util.Date;

import com.mooney_ware.android.steampunkt.R;
import com.mooney_ware.android.steampunkt.clock.IClockInterface;
import com.mooney_ware.android.steampunkt.clock.impls.AnalogClock;
import com.mooney_ware.android.steampunkt.clock.impls.SteamPunktClock;
import com.mooney_ware.android.steampunkt.clock.views.ClockFaceView;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.view.Window;
import android.view.WindowManager;

public class ClockFace extends Activity {

    private final Runnable mDrawTime = new Runnable() {

        @Override
        public void run() {
            updateTime();
        }
    };

    boolean mVisible;
    Handler mHandler = new Handler();
    IClockInterface mClockFace;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        
        setContentView(R.layout.main);
        
        mClockFace = new SteamPunktClock(this);
        
        ClockFaceView clockView = (ClockFaceView)findViewById(R.id.clockface);
        clockView.setClockFace(mClockFace);
        
        //hide the status bar
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mVisible = false;
        mHandler.removeCallbacks(mDrawTime);
    }

    @Override
    public void onPause(){
        super.onPause();
        mVisible = false;
        mHandler.removeCallbacks(mDrawTime);
    }

    @Override
    public void onResume(){
        super.onResume();
        mVisible = true;
        updateTime();
    }

    void updateTime() {

        final Date now = new Date(System.currentTimeMillis());

        mClockFace.update(now);

        // Reschedule the next redraw
        mHandler.removeCallbacks(mDrawTime);
        if (mVisible) {
            mHandler.postDelayed(mDrawTime, 1000);
        }
    }
}