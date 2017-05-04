package de.thb.ue.android.utility.animators;

import android.view.animation.LinearInterpolator;
import android.widget.TextView;

/**
 * Created by scorp on 04.03.2017.
 */

public class TextShrinkAndUpAnimator {

    private final TextView mViewToAnimate;

    private final float initialTextSize;
    private final float shrunkenTextSize;
    private final int duration;

    public TextShrinkAndUpAnimator(TextView viewToAnimate, float initialTextSize, float shrunkenTextSize, int duration) {
        this.mViewToAnimate = viewToAnimate;
        this.initialTextSize = initialTextSize;
        this.shrunkenTextSize = shrunkenTextSize;
        this.duration = duration;
    }


    public void shrinkTextAndMoveUpSingleItem(){
            mViewToAnimate.setPivotX(0);
            shrinkAnimation();
    }

    public void resetTextAndMoveDownSingleItem (){
            mViewToAnimate.setPivotX(0);
            enlargeAnimation();
    }

    private void shrinkAnimation() {
        mViewToAnimate.animate()
                .translationYBy(-initialTextSize)
                .scaleY(0.6f)
                .scaleX(0.6f)
                .setDuration(duration)
                .setInterpolator(new LinearInterpolator());
    }

    private void enlargeAnimation() {
        mViewToAnimate.animate()
                .translationYBy(initialTextSize)
                .setDuration(duration)
                .scaleY(1)
                .scaleX(1)
                .setInterpolator(new LinearInterpolator());
    }
}
