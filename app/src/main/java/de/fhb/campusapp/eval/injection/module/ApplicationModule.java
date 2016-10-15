package de.fhb.campusapp.eval.injection.module;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import com.github.buchandersenn.android_permission_manager.PermissionManager;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.fhb.campusapp.eval.utility.ClassMapper;

/**
 * Created by Sebastian Müller on 14.10.2016.
 */
@Module
public class ApplicationModule {

    protected final Application mApplication;

    public ApplicationModule(Application application) { mApplication = application; }

    @Provides
    Application provideApplication(){ return mApplication; }

    @Provides
    Context provideContext() { return mApplication; }

    @Provides @Singleton
    ClassMapper provideMapper() { return new ClassMapper(); }

    @Provides @Singleton
    Resources provideResources(){ return mApplication.getResources(); }
}
