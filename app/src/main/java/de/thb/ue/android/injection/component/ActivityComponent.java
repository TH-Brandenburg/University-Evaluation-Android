package de.thb.ue.android.injection.component;

import android.support.v4.app.FragmentManager;

import dagger.Subcomponent;
import de.thb.ue.android.injection.module.ActivityModule;
import de.thb.ue.android.injection.module.FragmentModule;
import de.thb.ue.android.injection.scopes.PerActivity;
import de.thb.ue.android.ui.splash.SplashActivity;

/**
 * Created by Sebastian Müller on 14.10.2016.
 */
@PerActivity
@Subcomponent(modules = {ActivityModule.class})
public interface ActivityComponent {
    void bind(SplashActivity splashActivity);

    FragmentComponent fragmentComponent(FragmentModule fragmentModule);

    FragmentManager fragmentManager();

}
