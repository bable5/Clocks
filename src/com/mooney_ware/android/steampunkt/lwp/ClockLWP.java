/*
 * Copyright (C) 2009 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.mooney_ware.android.steampunkt.lwp;

import java.util.Date;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.util.Log;
import android.view.SurfaceHolder;

import com.mooney_ware.android.steampunkt.R;
import com.mooney_ware.android.steampunkt.clock.IClockInterface;
import com.mooney_ware.android.steampunkt.clock.impls.AnalogClock;
import com.mooney_ware.android.steampunkt.clock.impls.BinaryClock;
import com.mooney_ware.android.steampunkt.clock.impls.SteamPunktClock;

/*
 * This animated wallpaper draws a rotating wireframe cube.
 */
public class ClockLWP extends WallpaperService {

    private final Handler mHandler = new Handler();
    private Drawable mBGImage;
    
    public static final String SHARED_PREFS_NAME="clocksettings";
    
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public Engine onCreateEngine() {
    	return new ClockEngine();
    }

    class ClockEngine extends Engine implements SharedPreferences.OnSharedPreferenceChangeListener{
    	//IClockInterface mClockFace = new CL_Clock();
    	//IClockInterface mClockFace = new AnalogClock();
        IClockInterface mClockFace; 
        
        SharedPreferences mPrefs;
        
        final String BRASS_PREF;
        final String SIMPLE_PREF;
        final String BINARY_PREF;
        
    	public ClockEngine(){
    	    super();
    	    
    	    Resources res = ClockLWP.this.getResources();
    	    
    	    BRASS_PREF = res.getString(R.string.cfp_brass);
    	    SIMPLE_PREF = res.getString(R.string.cfp_simple);
    	    BINARY_PREF = res.getString(R.string.cfp_binary);
    	    
    	    mPrefs = ClockLWP.this.getSharedPreferences(SHARED_PREFS_NAME, 0);
    	    Log.d("CLOCK ENGINE", "Registering shared pref listener");
            mPrefs.registerOnSharedPreferenceChangeListener(this);
            onSharedPreferenceChanged(mPrefs, null);
    	
    	}
    	
    	public void onSharedPreferenceChanged(SharedPreferences prefs, String key) {
    	    String face = prefs.getString("clock_face", "brass");
            setClockFace(face);
        }
    	
    	private void setClockFace(String facename){
    	    Log.d("CLOCK ENGINE", "Face changed to " + facename);
    	    if(BRASS_PREF.equals(facename)){
                Context context = ClockLWP.this;
    	        mClockFace = new SteamPunktClock(context); 
                Resources res = context.getResources();
                mBGImage = res.getDrawable(R.drawable.repeating_woodbg1);
                mBGImage.setBounds(0, 0, mWidth, mHeight);
    	    }else if(SIMPLE_PREF.equals(facename)){
    	        mClockFace = new AnalogClock();
    	        mBGImage = new ColorDrawable(Color.BLACK);
    	    }else if(BINARY_PREF.equals(facename)){
    	        mClockFace = new BinaryClock();
                mBGImage = new ColorDrawable(Color.WHITE);
    	    }else throw new RuntimeException("Unknown Clock Face " + facename);
    	    
    	}
    	
    	private float mXOffset;
        private float mYOffset;
        private int mWidth;
        private int mHeight;
        
    	//Tick twice a second
    	static final int TICK_FREQ = 500;
    	
    	private final Runnable mDrawTime = new Runnable() {
			
			@Override
			public void run() {
				drawFrame();
			}
		};
		private boolean mVisible = true;
		
		void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas canvas = null;
            try {
                canvas = holder.lockCanvas();
                if (canvas != null) {
                    //translating the canvase doesn't seem to effect how the clock is drawn
                    canvas.save();
                    canvas.translate(mXOffset, mYOffset);
                    mBGImage.draw(canvas);
                    final Date now = new Date(System.currentTimeMillis());
                    mClockFace.update(now);
                    mClockFace.draw(canvas);
                    canvas.restore();
                }
            } finally {
                if (canvas != null) holder.unlockCanvasAndPost(canvas);
            }
            // Reschedule the next redraw
            mHandler.removeCallbacks(mDrawTime);
            if (mVisible) {
                long postDelay = TICK_FREQ - (System.currentTimeMillis() % TICK_FREQ);
                mHandler.postDelayed(mDrawTime, postDelay);
            }
        }
		
		 @Override
	        public void onCreate(SurfaceHolder surfaceHolder) {
	            super.onCreate(surfaceHolder);

	            // By default we don't get touch events, so enable them.
	            setTouchEventsEnabled(true);
	        }

	        @Override
	        public void onDestroy() {
	            super.onDestroy();
	            mHandler.removeCallbacks(mDrawTime);
	        }

	        @Override
	        public void onVisibilityChanged(boolean visible) {
	            mVisible = visible;
	            if (visible) {
	                drawFrame();
	            } else {
	                mHandler.removeCallbacks(mDrawTime);
	            }
	        }

	        @Override
	        public void onSurfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	            super.onSurfaceChanged(holder, format, width, height);
	            // store the center of the surface, so we can draw the cube in the right spot
	            mWidth = width;
	            mHeight = height;
	            
	            //reset the size of the drawable
	            Drawable bg = mBGImage;
	            bg.setBounds(0, 0, width, height);
	            
	            drawFrame();
	        }

	        @Override
	        public void onSurfaceCreated(SurfaceHolder holder) {
	            super.onSurfaceCreated(holder);
	        }

	        @Override
	        public void onSurfaceDestroyed(SurfaceHolder holder) {
	            super.onSurfaceDestroyed(holder);
	            mVisible = false;
	            mHandler.removeCallbacks(mDrawTime);
	        }

	        @Override
	        public void onOffsetsChanged(float xOffset, float yOffset,
	                float xStep, float yStep, int xPixels, int yPixels) {
	            mXOffset = xOffset;
	            mYOffset = yOffset; 
	            drawFrame();
	        }
    }
}
