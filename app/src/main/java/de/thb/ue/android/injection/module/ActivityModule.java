package de.thb.ue.android.injection.module;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import com.github.buchandersenn.android_permission_manager.PermissionManager;

import dagger.Module;
import dagger.Provides;
import de.thb.ue.android.injection.ActivityContext;
import de.thb.ue.android.utility.customized_classes.BaseFragmentPagerAdapter;
import thb.de.ue.android.R;


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
    BaseFragmentPagerAdapter provideFragmentPagerAdapter(FragmentManager fragmentManager){
        return new BaseFragmentPagerAdapter(fragmentManager, mActivity);
    }

    @Provides
    ArrayAdapter<String> provideArrayAdapter(){
        return new ArrayAdapter<>(mActivity, R.layout.simple_list_item);
    }
}
