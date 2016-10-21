package de.fhb.campusapp.eval.utility;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

/**
// * Created by Sebastian Müller on 09.10.2016.
// */
public class ActivityUtil {
    public static void saveTerminateTask(Activity activity){
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

    public static void startActivity(Activity activity, Class<? extends Activity> clazz){
        Intent intent = new Intent(activity, clazz);
        activity.startActivity(intent);
    }
}
