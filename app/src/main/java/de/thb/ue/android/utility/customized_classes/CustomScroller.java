package de.thb.ue.android.utility.customized_classes;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import de.thb.ue.android.utility.FeatureSwitch;

/**
 * Created by Sebastian Müller on 01.07.2016.
 */

public class CustomScroller extends Scroller {

    private int mDuration = 1100;
    private boolean buttonClicked = false;

    public CustomScroller(Context context) {
        super(context);
    }

    public CustomScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);

    }

    public CustomScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy, int duration) {
        // Ignore received duration, use fixed one instead
        if(buttonClicked){
            super.startScroll(startX, startY, dx, dy, mDuration);
            buttonClicked = false;
        } else {
            super.startScroll(startX, startY, dx, dy, duration);
        }
    }

    @Override
    public void startScroll(int startX, int startY, int dx, int dy) {
        // Ignore received duration, use fixed one instead
        if(buttonClicked){
            super.startScroll(startX, startY, dx, dy, mDuration);
            buttonClicked = false;
        } else {
            super.startScroll(startX, startY, dx, dy);
        }
    }
}

