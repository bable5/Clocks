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
        
    	public ClockEngine(){
    	    super();
    	    
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
    	    if("brass".equals(facename)){
                Context context = ClockLWP.this;
    	        mClockFace = new SteamPunktClock(context); 
                Resources res = context.getResources();
                mBGImage = res.getDrawable(R.drawable.repeating_woodbg1);
            }else if("binary".equals(facename)){
    	        mClockFace = new AnalogClock();
                mBGImage = new ColorDrawable(Color.BLACK);
    	    }else throw new RuntimeException("Unknown Clock Face " + facename);
    	    
    	}
    	
    	//IClockInterface mClockFace = new SteamPunktClock(mContex);
        
    	//private final Paint mPaint = new Paint();
        private float mOffset;
//        private float mTouchX = -1;
//        private float mTouchY = -1;
//        private long mStartTime;
        private float mCenterX;
        private float mCenterY;
    	private static final int BG_COLOR = 0xff000000;
        
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
                    //canvas.save();
                    //canvas.drawColor(BG_COLOR);
                    mBGImage.draw(canvas);
                    //canvas.translate(mCenterX, mCenterY);
                    final Date now = new Date(System.currentTimeMillis());
                    mClockFace.update(now);
                    mClockFace.onDraw(canvas);
                    //canvas.restore();
                }
            } finally {
                if (canvas != null) holder.unlockCanvasAndPost(canvas);
            }

            // Reschedule the next redraw
            mHandler.removeCallbacks(mDrawTime);
            if (mVisible) {
                mHandler.postDelayed(mDrawTime, TICK_FREQ);
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
	            mCenterX = width/2.0f;
	            mCenterY = height/2.0f;
	            
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
	            mOffset = xOffset;
	            
	            Log.i("CLWP", "Offsets now: " + xOffset + "," + yOffset);
	            
	            drawFrame();
	        }
    }
}
