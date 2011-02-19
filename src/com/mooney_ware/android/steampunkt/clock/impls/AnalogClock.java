package com.mooney_ware.android.steampunkt.clock.impls;

import java.util.Date;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;

/**
 * Draws a simple analog clock face.
 * 
 * TODO: Lots of redundant calculations that could be refactored
 * out to make this more efficient.
 * @author sean
 *
 */
public class AnalogClock extends CL_Clock{

    protected Paint mPaint = new Paint();
    protected float clockRadius = 1;
    protected RectF mClipBounds;
    protected final static float SEC_HAND_LEN = .8f;
    protected final static float MIN_HAND_LEN = .75f;
    protected final static float HOUR_HAND_LEN = .5f;
    protected final static int MAX_MIN_TICKS = 60;
    protected final static int MAX_SEC_TICKS = 60;
    protected final static int MAX_HOUR_TICKS = 12;


    @Override
    protected void drawTime(Canvas canvas){
        final RectF bounds = new RectF(canvas.getClipBounds());
        mClipBounds = bounds;
        
        final float width = bounds.width();
        final float height = bounds.height();
        final int xinset = 20;
        float radius = width < height ? width : height;
        radius /= 2;
        radius = radius > xinset ? radius - xinset : radius;

        setRadius(radius);

        Date d = mDate;

        int oldColor = mPaint.getColor();
        
        mPaint.setColor(Color.GREEN);
        drawClockFace(canvas);
        
        int hour, min, sec;
        hour =d.getHours();
        min = d.getMinutes();
        sec = d.getSeconds();
        
        //minute hand
        drawHourHand(canvas, hour, min);
        //hour hand
        drawMinuteHand(canvas, min, sec);
        //secondhand
        drawSecondHand(canvas, sec);
        
        mPaint.setColor(oldColor);
    }


    protected void drawMinuteHand(Canvas c, int ticks, int secTicks){
        drawHand(c, ticks, MAX_MIN_TICKS, secTicks, MAX_SEC_TICKS, MIN_HAND_LEN);
    }

    protected void drawHourHand(Canvas c, int ticks, int minTicks){
        drawHand(c, ticks, MAX_HOUR_TICKS, minTicks, MAX_MIN_TICKS,  HOUR_HAND_LEN);
    }

    protected void drawSecondHand(Canvas c, int ticks){
        drawHand(c, ticks, MAX_SEC_TICKS, 0, 0, SEC_HAND_LEN);
    }

    /**
     * Compute the angle the arm should be at.
     * @param fullCircle number of units in a full circle (2\Pi or 360)
     * @param ticks
     * @param max_ticks
     * @param depTicks
     * @param max_depTicks
     * @return
     */
    protected static float handAngle (final float fullCircle, final int ticks, final int max_ticks, final int depTicks, final int max_depTicks){
        float theta = (float)ticks/(float)max_ticks * fullCircle;
        
        float partialPer = 0f;
        if(max_depTicks != 0){
             partialPer = (float)depTicks/(float)max_depTicks; 
             //size of arc from 1 tic to the next
             //todo: Could optimize by only computing the tick arcs once, and
             //passing as a param.
             float tickArc = fullCircle/max_ticks;
             partialPer = partialPer * tickArc;
        }
        
        return theta + partialPer;
    }
   
    
    /**
     * Draw a generic hand.
     * Draw minutes, hours, and seconds should filter into here,
     * but are implemented as separate methods, in case in the future
     * each hand gets drawn differently.
     * @param c
     * @param ticks
     * @param max_ticks
     * @param handLen
     */
    protected void drawHand(final Canvas c, final int ticks, final int max_ticks, final int depTicks, final int max_depTicks, final float handLen){
        final Paint paint = mPaint;
        final RectF bounds = mClipBounds;
        
        float theta = handAngle(TWO_PI, ticks, max_ticks, depTicks, max_depTicks);
        
        float centerX, centerY;

        float xAdj = bounds.centerX();
        float yAdj = bounds.centerY();
        centerX = xAdj; 
        centerY = yAdj; 

        theta += deg_offset;
        float handLength = handLen * clockRadius;
        float x = (float)(Math.cos(theta) * handLength + xAdj);
        float y = (float)(Math.sin(theta) * handLength + yAdj);

        c.drawLine(centerX, centerY, x, y, paint);
    }


    protected static final float PI = (float)Math.PI;
    protected static final float TWO_PI = 2*PI;
    protected static final float deg_offset = -.5f * PI;
    protected static final String[] HOURS_LABELS = {"12", "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", "11"};

    public void setRadius(float radius){
        this.clockRadius = radius;
    }

    
    protected void drawClockFace(Canvas c){
        final Paint paint = mPaint;
        
        final float radius = clockRadius;
        
        drawTicks(c, HOURS_LABELS, radius, paint);
    }
    
    protected void drawTicks(Canvas c, String[] labels, float radius, Paint paint ){
        final RectF bounds = mClipBounds;
        float xAdj = bounds.centerX();
        float yAdj = bounds.centerY();
        
        float x, y;
        
        for(int i = 0; i<labels.length; i++){
            float theta = (float)i/12.0f * TWO_PI;
            theta += deg_offset;

            x = (float)(Math.cos(theta) * radius + xAdj);
            y = (float)(Math.sin(theta) * radius + yAdj);
            c.drawText(labels[i], x, y, paint);
            //System.out.println("Theta: " + theta);
            //System.out.println("Drawing " + i + " at: " + x + "," + y);
        }
    }
}
