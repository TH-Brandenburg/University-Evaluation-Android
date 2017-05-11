package de.thb.ue.android.ui;

import android.app.Application;
import android.content.Context;

import com.mikepenz.iconics.context.IconicsContextWrapper;

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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(IconicsContextWrapper.wrap(base));
    }

    public static ApplicationComponent getApplicationComponent(Context context){
        return ((BaseApplication)context.getApplicationContext()).applicationComponent;
    }
}
