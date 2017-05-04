package de.thb.ue.android.ui;

import android.app.Application;
import android.content.Context;

import de.thb.ue.android.injection.component.ApplicationComponent;
import de.thb.ue.android.injection.component.DaggerApplicationComponent;
import de.thb.ue.android.injection.module.ApplicationModule;


public class BaseApplication extends Application {

    private ApplicationComponent applicationComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        applicationComponent = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .build();
    }

    public static ApplicationComponent getApplicationComponent(Context context){
        return ((BaseApplication)context.getApplicationContext()).applicationComponent;
    }
}
