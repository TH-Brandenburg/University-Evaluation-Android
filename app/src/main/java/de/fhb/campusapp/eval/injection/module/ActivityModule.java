package de.fhb.campusapp.eval.injection.module;

import android.app.Activity;
import android.content.Context;

import com.github.buchandersenn.android_permission_manager.PermissionManager;

import dagger.Module;
import dagger.Provides;

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

    @Provides
    Context providesContext() {
        return mActivity;
    }

    @Provides
    PermissionManager providePermissionManager(){ return PermissionManager.create(mActivity); }
}
