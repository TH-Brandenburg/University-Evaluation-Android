package de.fhb.campusapp.eval.injection.component;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Component;
import de.fhb.campusapp.eval.data.DataManager;
import de.fhb.campusapp.eval.data.IDataManager;
import de.fhb.campusapp.eval.data.local.PreferencesHelper;
import de.fhb.campusapp.eval.data.local.RetrofitHelper;
import de.fhb.campusapp.eval.injection.ApplicationContext;
import de.fhb.campusapp.eval.injection.module.ActivityModule;
import de.fhb.campusapp.eval.injection.module.ApplicationModule;
import de.fhb.campusapp.eval.services.CleanUpService;
import de.fhb.campusapp.eval.ui.eval.EvalPresenter;
import de.fhb.campusapp.eval.ui.eval.EvaluationActivity;
import de.fhb.campusapp.eval.ui.sendfragment.SendFragment;
import de.fhb.campusapp.eval.ui.sendfragment.SendPresenter;
import de.fhb.campusapp.eval.ui.splash.SplashActivity;
import de.fhb.campusapp.eval.ui.splash.SplashPresenter;
import de.fhb.campusapp.eval.utility.ClassMapper;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.eventpipelines.NetworkEventPipelines;

/**
 * Created by Sebastian Müller on 14.10.2016.
 */
@Singleton
@Component(modules = {ApplicationModule.class })
public interface ApplicationComponent {
    void bind(RetrofitHelper helper);
    void bind(PreferencesHelper preferencesHelper);
    void bind(CleanUpService cleanUpService);

    void bind(SplashPresenter splashPresenter);
    void bind(EvalPresenter evalPresenter);
    void bind(SendPresenter sendPresenter);
    void bind(DataManager dataManager);

    @ApplicationContext Context context();
    Application application();
    ClassMapper classMapper();
    Resources resources();
    IDataManager dataManager();
    NetworkEventPipelines eventPipelines();

    ActivityComponent activityComponent(ActivityModule activityModule);

}
