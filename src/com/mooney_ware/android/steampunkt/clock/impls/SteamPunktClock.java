/**
 *
 */
package com.mooney_ware.android.steampunkt.clock.impls;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;

import com.mooney_ware.android.steampunkt.R;

/**
 * @author sean
 *
 */
public class SteamPunktClock extends AnalogClock{

    Drawable mClockFace;
    //RotateDrawable mHand;
    Drawable mHand;
    
    /**
     * Background image
     */
    Drawable mBgTile;
    
    int mHandWidth;
    int mHandHeight;
    private float mClockBorderWidth = 25;
    private String[] ROMAN_LABELS = {"XII", "I", "II", "III", "IV", "V", "VI", "VII", "VIII", "IX", "X", "XI"};

    public SteamPunktClock(Context context){
        Resources res = context.getResources();
        mClockFace = res.getDrawable(R.drawable.clockface);
        //Drawable hand = res.getDrawable(R.drawable.hand);
        mHand = res.getDrawable(R.drawable.hand);
        
        mHandWidth = mHand.getIntrinsicWidth();
        mHandHeight = mHand.getIntrinsicWidth();
        
        mBgTile = res.getDrawable(R.drawable.repeating_woodbg1);
        
    }   

    @Override
    protected void drawBackground(Canvas canvas){
        mBgTile.draw(canvas);
    }
    
    /**
     * Draw a generic hand.
     * Draw minutes, hours, and seconds should filter into here,
     * but are implemented as separate methods, in case in the future
     * each hand gets drawn differently.
     * @param c
     * @param ticks
     * @param max_ticks
     * @param depTicks number of ticks from the input. (i.e. when drawing the hour hand depTicks is the number of minutes)
     * @param max_depTicks
     * @param handLen
     */
    @Override
    protected void drawHand(final Canvas c, final int ticks, final int max_ticks, final int depTicks, final int max_depTicks, final float handLen){
        final RectF bounds = mClipBounds;

        int theta = (int)handAngle(360f, ticks, max_ticks, depTicks, max_depTicks);
        float centerX, centerY;

        float xAdj = bounds.centerX();
        float yAdj = bounds.centerY();
        centerX = xAdj; 
        centerY = yAdj; 

        //do floating point math to prevent round off errors.
        float fHandHeight = clockRadius * handLen;
        //keep the aspect ratio the same.
        float fHandWidth = mHandWidth * (fHandHeight/mHandHeight);
        
        //convert to ints now what we're done with the math.
        int handWidth = (int)fHandWidth;
        int handHeight = (int)fHandHeight;
        
        //keep the hand from being so thin, it's zero.
        //if(handWidth < 4) handWidth = 16;
        
        int halfWidth = handWidth/8;
        int xLeft = (int)(centerX) - halfWidth;
        int yTop = (int)(centerY) - handHeight;
        int xRight = (int)(centerX) + halfWidth;
        int yBottom = (int)centerY;
        
//        Log.i("SteamPunktClock", "Bounds[ " 
//                + xLeft + "," + yTop + " and " 
//                + xRight + "," + yBottom + "]");
        c.save();
        c.rotate(theta, centerX, centerY);
        mHand.setBounds(xLeft, yTop, xRight, yBottom);
        mHand.draw(c);
        c.restore();
    }

    @Override
    protected void drawSecondHand(Canvas c, int ticks){
        //don't show the second hand in this clock
    }
    
    @Override
    protected void drawClockFace(Canvas c){
        float cx = mClipBounds.centerX();
        float cy = mClipBounds.centerY();

        mClockFace.setBounds((int)(cx - clockRadius),
                (int)(cy - clockRadius),
                (int)(cx + clockRadius), 
                (int)(cy + clockRadius));
        mClockFace.draw(c);
        
        final Paint paint = mPaint;
        
        int color = paint.getColor();
        
        paint.setColor(Color.BLACK);
        
        final float radius = clockRadius - mClockBorderWidth;
        drawTicks(c, ROMAN_LABELS, radius, paint);
        
        //restore to the way things were, in case anything is depending on 
        //a constent state.
        paint.setColor(color);
    }
}
