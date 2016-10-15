package de.fhb.campusapp.eval.injection.component;

import dagger.Component;
import dagger.Subcomponent;
import de.fhb.campusapp.eval.injection.module.ActivityModule;
import de.fhb.campusapp.eval.injection.scopes.PerActivity;
import de.fhb.campusapp.eval.ui.eval.EvaluationActivity;
import de.fhb.campusapp.eval.ui.sendfragment.SendFragment;
import de.fhb.campusapp.eval.ui.splash.SplashActivity;
import de.fhb.campusapp.eval.ui.textfragment.TextFragment;

/**
 * Created by Sebastian Müller on 14.10.2016.
 */
@PerActivity
@Subcomponent(modules = {ActivityModule.class})
public interface ActivityComponent {
    void bind(SplashActivity splashActivity);
    void bind(EvaluationActivity evaluationActivity);
    void bind(SendFragment sendFragment);
    void bind(TextFragment textFragment);

}
