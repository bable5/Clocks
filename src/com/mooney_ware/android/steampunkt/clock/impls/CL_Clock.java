package com.mooney_ware.android.steampunkt.clock.impls;

import java.text.DateFormat;
import java.util.Date;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;

import com.mooney_ware.android.steampunkt.clock.IClockInterface;

/**
 * A clock that simply draws its time as a string on the screen.
 * 
 * This class is suitable for extending for any other 
 * clock face type drawables.
 * 
 * @author Sean Mooney
 *
 */
public class CL_Clock extends Drawable implements IClockInterface {

    protected DateFormat mFormat = DateFormat.getDateTimeInstance();
    protected Paint mPaint = new Paint();
    protected int mOpacity = PixelFormat.TRANSLUCENT;
    
    static final int MAX_NUM_SECS = 60;
    static final int MAX_NUM_MINS = 60;
    static final int MAX_NUM_HRS = 12;
    
    protected Date mDate;
    private TickTockListener listener;
    private ColorFilter mColorFilter;
    
    public CL_Clock(){
        init();
    }

    /**
     * Common init functions, and override hook.
     */
    protected void init(){
        final Paint paint = mPaint;
        paint.setColor(0xff00ff00);
        paint.setAntiAlias(true);
        paint.setStrokeWidth(1);
        paint.setStrokeCap(Paint.Cap.SQUARE);
        paint.setStyle(Paint.Style.STROKE);
        paint.setTextSize(12f);
        paint.setTypeface(Typeface.SERIF);
    }

    
    public void draw(Canvas canvas){
        drawBackground(canvas);
        drawTime(canvas);
    }

    protected void drawTime(Canvas canvas){
        final DateFormat format = mFormat;
        final Paint paint = mPaint;

        String text = format.format(mDate);
        float textLen = paint.measureText(text);
        float offset = textLen / 2.0f;

        canvas.drawText(text, -offset, 0, paint);
    }

    /**
     * Hook for subclasses to override and draw their
     * specific background.
     * @param canvas
     */
    protected void drawBackground(Canvas canvas){}
    
    /**
     * Set the date and notify any connected listeners
     * that a tick happened.
     */
    @Override
    public void update(Date d) {
        mDate = d;
        if(this.listener != null) listener.tick();
    }

    /* (non-Javadoc)
     * @see com.mooney_ware.android.steampunkt.clock.IClockInterface#registerTickListener(com.mooney_ware.android.steampunkt.clock.IClockInterface.TickTockListener)
     */
    @Override
    public void registerTickListener(TickTockListener listener) {
        this.listener = listener;
    }

    /*
     * (non-Javadoc)
     * @see android.graphics.drawable.Drawable#getOpacity()
     */
    @Override
    public int getOpacity() {
        return mOpacity;
    }

    /*
     * (non-Javadoc)
     * @see android.graphics.drawable.Drawable#setAlpha(int)
     */
    @Override
    public void setAlpha(int alpha) {
       this.mOpacity = alpha; 
       mPaint.setAlpha(alpha);
    }

    /*
     * (non-Javadoc)
     * @see android.graphics.drawable.Drawable#setColorFilter(android.graphics.ColorFilter)
     */
    @Override
    public void setColorFilter(ColorFilter cf) {
        mPaint.setColorFilter(cf);
        mColorFilter = cf;
    }
}
