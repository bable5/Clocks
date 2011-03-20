package com.mooney_ware.android.steampunkt.clock.impls;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

/**
 * 
 * @author Sean Mooney
 *
 */
public class BinaryClock extends CL_Clock{

    Drawable mBackGround = new ColorDrawable(Color.WHITE);
    
    /* (non-Javadoc)
     * @see com.mooney_ware.android.steampunkt.clock.impls.CL_Clock#drawTime(android.graphics.Canvas)
     */
    @Override
    protected void drawTime(Canvas canvas) {
        // TODO Auto-generated method stub
        super.drawTime(canvas);
    }

    /* (non-Javadoc)
     * @see com.mooney_ware.android.steampunkt.clock.impls.CL_Clock#drawBackground(android.graphics.Canvas)
     */
    @Override
    protected void drawBackground(Canvas canvas) {
        // TODO Auto-generated method stub
        super.drawBackground(canvas);
    }

}
