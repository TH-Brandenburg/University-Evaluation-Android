package de.fhb.campusapp.eval.utility.Events;

import android.app.Activity;

/**
 * Created by Admin on 24.07.2016.
 */

public class ActivityInstanceAcquiredEvent {
    private Activity activity;


    public ActivityInstanceAcquiredEvent(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
