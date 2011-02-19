/**
 *
 */
package com.mooney_ware.android.steampunkt.clock.views;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

import com.mooney_ware.android.steampunkt.clock.IClockInterface;
import com.mooney_ware.android.steampunkt.clock.IClockInterface.TickTockListener;

/**
 * @author sean
 *
 */
public class  ClockFaceView extends View implements TickTockListener {

    protected IClockInterface mClockface;
    
    /**
     * @param context
     */
    public ClockFaceView(Context context) {
        super(context);
    }
    
    public ClockFaceView(Context context, AttributeSet attributes) {
        super(context, attributes);
    }

    public void setClockFace(IClockInterface clockface){
        mClockface = clockface;
        clockface.registerTickListener(this);
    }
    
    @Override
    public void onDraw(Canvas canvas){
        if(mClockface != null) mClockface.onDraw(canvas);
    }

    /* (non-Javadoc)
     * @see com.mooney_ware.android.steampunkt.clock.IClockInterface.TickTockListener#tick()
     */
    @Override
    public void tick() {
        invalidate();
    }
}
