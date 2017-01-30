package de.fhb.campusapp.eval.ui;

import android.content.Context;
import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import net.danlew.android.joda.JodaTimeAndroid;

import de.fhb.campusapp.eval.injection.component.ApplicationComponent;
import de.fhb.campusapp.eval.injection.component.DaggerApplicationComponent;
import de.fhb.campusapp.eval.injection.module.ApplicationModule;


/**
 * Created by Sebastian Müller on 18.06.2016.
 */

public class EvaluationApplication extends MultiDexApplication {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();

        JodaTimeAndroid.init(this);
    }

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }

    public static ApplicationComponent getApplicationComponent(Context context){
        return ((EvaluationApplication)context.getApplicationContext()).applicationComponent;
    }

}
