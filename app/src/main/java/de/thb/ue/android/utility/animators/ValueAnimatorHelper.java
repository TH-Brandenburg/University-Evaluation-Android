package de.thb.ue.android.utility.animators;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.view.View;
import android.widget.TextView;

/**
 * Created by scorp on 20.02.2017.
 */

public class ValueAnimatorHelper {

    public static void animateColor(View view, int startColor, int endColor){
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(startColor, endColor);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setBackgroundColor((Integer)valueAnimator.getAnimatedValue());
            }
        });

        anim.setDuration(200);
        anim.start();
    }

    /**
     * Animates the text size. The provided sizes must be transformed into sp
     * before being passed into this method.
     * @param view
     * @param startSize
     * @param endSize
     */
    public static void animateTextSize(TextView view, int startSize, int endSize, int duration){
        ValueAnimator anim = new ValueAnimator();
        anim.setIntValues(startSize, endSize);
        anim.setEvaluator(new ArgbEvaluator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                view.setTextSize((Integer)valueAnimator.getAnimatedValue());
            }
        });

        anim.setDuration(duration);
        anim.start();
    }
}
