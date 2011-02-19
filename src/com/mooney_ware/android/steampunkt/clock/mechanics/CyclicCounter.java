package com.mooney_ware.android.steampunkt.clock.mechanics;

import java.util.ArrayList;

public class CyclicCounter{
    
    private final int mNumTicks;
    private int mTicksReceived;

    private ArrayList<CyclicCounter> outputList = new ArrayList<CyclicCounter>();

    public CyclicCounter(int numTicks){
        mNumTicks = numTicks;
        mTicksReceived = 0;
    }

    public void connectOutput(CyclicCounter counter){
        outputList.add(counter);
    }

    public void tick(){
        mTicksReceived++;
        mTicksReceived %= mNumTicks;
        announceTick();
    }

    private void announceTick(){
        for(CyclicCounter cc : outputList){
            cc.tick();
        }
    }

    public int getCount(){
        return mTicksReceived;
    }
}
