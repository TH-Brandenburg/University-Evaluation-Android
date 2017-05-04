package de.thb.ue.android.injection.module;

import android.app.Activity;

import dagger.Module;
import dagger.Provides;
import de.thb.ue.android.utility.customized_classes.BaseFlexibleAdapter;
import eu.davidea.flexibleadapter.common.SmoothScrollLinearLayoutManager;
import eu.davidea.flexibleadapter.items.IFlexible;

/**
 * Created by scorp on 15.02.2017.
 */
@Module
public class FragmentModule {

    private Activity mActivity;

    public FragmentModule(Activity activity) {
        mActivity = activity;
    }

    @Provides
    BaseFlexibleAdapter provideFlexibleAdapter(){
        BaseFlexibleAdapter<IFlexible> adapter = new BaseFlexibleAdapter<>(null);
        return adapter;
    }

    @Provides
    SmoothScrollLinearLayoutManager provideSmoothScrollLinearLayoutManager(){
        return new SmoothScrollLinearLayoutManager(mActivity);
    }
}
