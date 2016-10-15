package de.fhb.campusapp.eval.injection.component;

import javax.inject.Singleton;

import dagger.Component;
import de.fhb.campusapp.eval.injection.module.ApplicationModule;

/**
 * Created by Sebastian Müller on 14.10.2016.
 */
@Singleton
@Component(modules = {ApplicationModule.class })
public interface ApplicationComponent {

}
