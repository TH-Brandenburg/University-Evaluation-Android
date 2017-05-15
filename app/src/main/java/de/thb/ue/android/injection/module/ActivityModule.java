package de.thb.ue.android.injection.module;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.widget.ArrayAdapter;

import com.github.buchandersenn.android_permission_manager.PermissionManager;

import java.util.List;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import de.thb.ue.android.data.IDataManager;
import de.thb.ue.android.injection.ActivityContext;
import de.thb.ue.android.ui.evaluation.choice.ButtonPresenter;
import de.thb.ue.android.ui.evaluation.send.SendFragment;
import de.thb.ue.android.ui.evaluation.send.SendPresenter;
import de.thb.ue.android.ui.evaluation.studypath.PathPresenter;
import de.thb.ue.android.ui.evaluation.text.TextPresenter;
import de.thb.ue.android.utility.customized_classes.BasePagerAdapter;
import thb.de.ue.android.R;


/**
 * Created by Sebastian Müller on 14.10.2016.
 */
@Module
public class ActivityModule {
    private List<ButtonPresenter> buttons;


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
    BasePagerAdapter provideFragmentPagerAdapter(FragmentManager fragmentManager, IDataManager dataManager){
        return new BasePagerAdapter(fragmentManager, mActivity, dataManager);
    }

    @Provides
    ArrayAdapter<String> provideArrayAdapter(){
        return new ArrayAdapter<String>(mActivity, R.layout.simple_list_item);
    }

    @Provides
    TextPresenter provideTextPresenter(IDataManager dataManager, @ActivityContext Context context){
        return new TextPresenter(dataManager, context);
    }
    @Provides
    PathPresenter providePathPresenter(IDataManager dataManager, @ActivityContext Context context){
        return new PathPresenter(dataManager, context);
    }
    @Provides
    ButtonPresenter provideButtonPresenter(IDataManager dataManager, @ActivityContext Context context){
        return new ButtonPresenter(dataManager, context);
    }
    @Provides
    SendPresenter provideSendPresenter(IDataManager dataManager, @ActivityContext Context context){
        return new SendPresenter(dataManager, context);
    }
}
