package de.fhb.campusapp.eval.utility;

import android.app.Activity;
import android.os.Build;

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
}
