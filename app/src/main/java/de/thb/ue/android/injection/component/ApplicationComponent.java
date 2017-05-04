package de.thb.ue.android.injection.component;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import javax.inject.Singleton;

import dagger.Component;
import de.thb.ue.android.data.IDataManager;
import de.thb.ue.android.injection.ApplicationContext;
import de.thb.ue.android.injection.module.ActivityModule;
import de.thb.ue.android.injection.module.ApplicationModule;


/**
 * Created by Sebastian Müller on 14.10.2016.
 */
@Singleton
@Component(modules = {ApplicationModule.class })
public interface ApplicationComponent {

    @ApplicationContext
    Context context();
    Application application();
    Resources resources();
    IDataManager dataManager();

    ActivityComponent activityComponent(ActivityModule activityModule);

}
