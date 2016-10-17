package de.fhb.campusapp.eval.utility;

import android.app.Activity;
import android.content.Context;
import android.os.Build;
import android.view.View;
import android.view.ViewTreeObserver;

/**
// * Created by Sebastian Müller on 09.10.2016.
// */
public class ActivityUtil {
    public static void saveFinish(Activity activity){
        if(Build.VERSION.SDK_INT < 21){
            activity.finish();
        } else {
            activity.finishAndRemoveTask();
        }
    }

    public static void removeGlobalLayoutListener(View view, ViewTreeObserver.OnGlobalLayoutListener victim) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
            view.getViewTreeObserver().removeOnGlobalLayoutListener(victim);
        } else {
            view.getViewTreeObserver().removeGlobalOnLayoutListener(victim);
        }
    }
}
