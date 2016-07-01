package de.fhb.campusapp.eval.custom;

import android.content.Context;
import android.view.animation.Interpolator;
import android.widget.Scroller;

import com.squareup.otto.Subscribe;

import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.ClickedChoiceButtonEvent;

/**
 * Created by Admin on 01.07.2016.
 */

public class CustomScroller extends Scroller {

    private int mDuration = 1000;
    private boolean buttonClicked = false;

    public CustomScroller(Context context) {
        super(context);
        EventBus.get().register(this);
    }

    public CustomScroller(Context context, Interpolator interpolator) {
        super(context, interpolator);
        EventBus.get().register(this);

    }

    public CustomScroller(Context context, Interpolator interpolator, boolean flywheel) {
        super(context, interpolator, flywheel);
        EventBus.get().register(this);
    }



    @Subscribe
    public void onClickedChoiceButtonEvent(ClickedChoiceButtonEvent event){
        buttonClicked = true;
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

