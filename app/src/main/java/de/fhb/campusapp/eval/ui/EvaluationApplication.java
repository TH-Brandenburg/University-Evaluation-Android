package de.fhb.campusapp.eval.ui;

import android.app.Application;
import android.content.Context;

import net.danlew.android.joda.JodaTimeAndroid;

import de.fhb.campusapp.eval.injection.component.ApplicationComponent;
import de.fhb.campusapp.eval.injection.component.DaggerApplicationComponent;
import de.fhb.campusapp.eval.injection.module.ApplicationModule;


/**
 * Created by Sebastian Müller on 18.06.2016.
 */

public class EvaluationApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        JodaTimeAndroid.init(this);
    }

    public static ApplicationComponent getApplicationComponent(Context context){
        return ((EvaluationApplication)context.getApplicationContext()).applicationComponent;
    }

}
