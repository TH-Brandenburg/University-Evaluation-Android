package de.fhb.campusapp.eval.injection.module;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

import com.github.buchandersenn.android_permission_manager.PermissionManager;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import de.fhb.campusapp.eval.custom.CustomFragmentStatePagerAdapter;
import de.fhb.campusapp.eval.data.IDataManager;
import de.fhb.campusapp.eval.injection.ActivityContext;
import de.fhb.campusapp.eval.injection.ApplicationContext;

/**
 * Created by Sebastian Müller on 14.10.2016.
 */
@Module
public class ActivityModule {

    private AppCompatActivity mActivity;

    public ActivityModule(AppCompatActivity activity) {
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
    FragmentManager provideFragmentManager() {
        return mActivity.getSupportFragmentManager();
    }

    @Provides
    CustomFragmentStatePagerAdapter provideCustomFragmentStatePagerAdapter(FragmentManager manager
            , @ActivityContext Context context
            , IDataManager dataManager){
        return new CustomFragmentStatePagerAdapter(manager, context, dataManager);
    }
}
