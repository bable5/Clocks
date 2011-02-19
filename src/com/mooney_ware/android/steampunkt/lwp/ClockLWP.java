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
import android.graphics.Canvas;
import android.graphics.Paint;
import android.os.Handler;
import android.service.wallpaper.WallpaperService;
import android.view.SurfaceHolder;

import com.mooney_ware.android.steampunkt.clock.IClockInterface;
import com.mooney_ware.android.steampunkt.clock.impls.AnalogClock;
import com.mooney_ware.android.steampunkt.clock.impls.SteamPunktClock;

/*
 * This animated wallpaper draws a rotating wireframe cube.
 */
public class ClockLWP extends WallpaperService {

    private final Handler mHandler = new Handler();
    private final Context mContext = this;
    
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

    class ClockEngine extends Engine{
    	//IClockInterface mClockFace = new CL_Clock();
    	//IClockInterface mClockFace = new AnalogClock();
        IClockInterface mClockFace = new SteamPunktClock(mContext);
        
    	public ClockEngine(){
    	    super();
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
        
    	private final Runnable mDrawTime = new Runnable() {
			
			@Override
			public void run() {
				drawFrame();
			}
		};
		private boolean mVisible = true;
		
		void drawFrame() {
            final SurfaceHolder holder = getSurfaceHolder();

            Canvas c = null;
            try {
                c = holder.lockCanvas();
                if (c != null) {
                    final Date now = new Date(System.currentTimeMillis());
                    c.save();
                    c.drawColor(BG_COLOR);
                    c.translate(mCenterX, mCenterY);
                    mClockFace.update(now);
                    mClockFace.onDraw(c);
                    c.restore();
                }
            } finally {
                if (c != null) holder.unlockCanvasAndPost(c);
            }

            // Reschedule the next redraw
            mHandler.removeCallbacks(mDrawTime);
            if (mVisible) {
                mHandler.postDelayed(mDrawTime, 1000 / 25);
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
	            drawFrame();
	        }
    }
}
