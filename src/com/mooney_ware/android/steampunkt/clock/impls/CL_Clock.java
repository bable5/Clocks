package com.mooney_ware.android.steampunkt.clock.impls;

import java.text.DateFormat;
import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.view.View;

import com.mooney_ware.android.steampunkt.clock.IClockInterface;
import com.mooney_ware.android.steampunkt.clock.mechanics.CyclicCounter;

public class CL_Clock implements IClockInterface {

    DateFormat mFormat = DateFormat.getDateTimeInstance();
    Paint mPaint = new Paint();
    
    static final int MAX_NUM_SECS = 60;
    static final int MAX_NUM_MINS = 60;
    static final int MAX_NUM_HRS = 12;
    
    protected Date mDate;
    
//    CyclicCounter seconds = new CyclicCounter(MAX_NUM_SECS);
//    CyclicCounter minutes = new CyclicCounter(MAX_NUM_MINS);
//    CyclicCounter hours = new CyclicCounter(numTicks);

    private TickTockListener listener;
    
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

    public void onDraw(Canvas canvas){
        drawTime(canvas);
    }

    protected void drawTime(Canvas canvas){
        final DateFormat format = mFormat;
        final Paint paint = mPaint;

        String text = format.format(mDate);
        float textLen = paint.measureText(text);
        float offset = textLen / 2.0f;

        drawBackground(canvas);
        canvas.drawText(text, -offset, 0, paint);
    }

    /**
     * Hook for subclasses to override and draw their
     * specific background.
     * @param canvas
     */
    protected void drawBackground(Canvas canvas){}
    
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

}
