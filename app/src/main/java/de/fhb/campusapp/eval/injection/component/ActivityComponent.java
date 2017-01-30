package de.fhb.campusapp.eval.injection.component;

import android.support.v4.app.FragmentManager;

import dagger.Component;
import dagger.Subcomponent;
import de.fhb.campusapp.eval.custom.CustomFragmentStatePagerAdapter;
import de.fhb.campusapp.eval.custom.CustomViewPager;
import de.fhb.campusapp.eval.ui.button.ButtonFragment;
import de.fhb.campusapp.eval.ui.path.PathFragment;
import de.fhb.campusapp.eval.injection.module.ActivityModule;
import de.fhb.campusapp.eval.injection.scopes.PerActivity;
import de.fhb.campusapp.eval.ui.enlarge.EnlargeImageActivity;
import de.fhb.campusapp.eval.ui.eval.EvaluationActivity;
import de.fhb.campusapp.eval.ui.scan.ScanActivity;
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
    void bind(EnlargeImageActivity enlargeImageActivity);
    void bind(ScanActivity scanActivity);
    void bind(ButtonFragment buttonFragment);
    void bind(PathFragment pathFragment);
    void bind(SendFragment sendFragment);
    void bind(TextFragment textFragment);
    void bind(CustomFragmentStatePagerAdapter statePagerAdapter);
    void bind(CustomViewPager customViewPager);

    FragmentManager fragmentManager();
    CustomFragmentStatePagerAdapter statePagerAdapter();

}
