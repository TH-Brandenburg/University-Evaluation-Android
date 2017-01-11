package de.fhb.campusapp.eval.injection.module;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;

import com.github.buchandersenn.android_permission_manager.PermissionManager;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import de.fhb.campusapp.eval.injection.ActivityContext;
import de.fhb.campusapp.eval.injection.ApplicationContext;

/**
 * Created by Sebastian Müller on 14.10.2016.
 */
@Module
public class ActivityModule {

    private Activity mActivity;

    public ActivityModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    Activity provideActivity() {
        return mActivity;
    }

    @Provides @ActivityContext
    Context provideContext() {
        return mActivity;
    }

    @Provides
    PermissionManager providePermissionManager(){ return PermissionManager.create(mActivity); }

    @Provides
    DisplayMetrics provideDisplayMetrics(){
        return mActivity.getResources().getDisplayMetrics();
    }
}
