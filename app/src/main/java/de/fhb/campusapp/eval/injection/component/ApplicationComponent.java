package de.fhb.campusapp.eval.injection.component;

import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import javax.inject.Singleton;

import dagger.Binds;
import dagger.Component;
import de.fhb.campusapp.eval.data.local.RetrofitHelper;
import de.fhb.campusapp.eval.injection.ApplicationContext;
import de.fhb.campusapp.eval.injection.module.ActivityModule;
import de.fhb.campusapp.eval.injection.module.ApplicationModule;
import de.fhb.campusapp.eval.ui.button.ButtonPresenter;
import de.fhb.campusapp.eval.ui.eval.EvalPresenter;
import de.fhb.campusapp.eval.ui.eval.EvaluationActivity;
import de.fhb.campusapp.eval.ui.path.PathPresenter;
import de.fhb.campusapp.eval.ui.sendfragment.SendFragment;
import de.fhb.campusapp.eval.ui.sendfragment.SendPresenter;
import de.fhb.campusapp.eval.ui.splash.SplashActivity;
import de.fhb.campusapp.eval.ui.splash.SplashPresenter;
import de.fhb.campusapp.eval.utility.ClassMapper;
import de.fhb.campusapp.eval.utility.EventBus;

/**
 * Created by Sebastian Müller on 14.10.2016.
 */
@Singleton
@Component(modules = {ApplicationModule.class })
public interface ApplicationComponent {
    void bind(RetrofitHelper helper);
    void bind(SplashPresenter splashPresenter);
    void bind(EvalPresenter evalPresenter);
    void bind(SendPresenter sendPresenter);

    void bind(ButtonPresenter buttonPresenter);
    void bind(PathPresenter pathPresenter);

    @ApplicationContext Context context();
    Application application();
    ClassMapper classMapper();
    Resources resources();

    ActivityComponent activityComponent(ActivityModule activityModule);

}
