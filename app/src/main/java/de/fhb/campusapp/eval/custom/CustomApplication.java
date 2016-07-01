package de.fhb.campusapp.eval.custom;

import android.app.Application;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Created by Admin on 18.06.2016.
 */

public class CustomApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        JodaTimeAndroid.init(this);
    }
}
