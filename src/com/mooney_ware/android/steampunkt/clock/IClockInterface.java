package com.mooney_ware.android.steampunkt.clock;

import java.util.Date;

import android.content.Context;
import android.graphics.Canvas;
import android.view.View;

public interface IClockInterface {

    public void update(Date d);
    public void draw(Canvas c);
	public void registerTickListener(TickTockListener listener);
    
    /**
     * Listener interface
     * @author sean
     *
     */
    public static interface TickTockListener{
        public void tick();
    }
}
