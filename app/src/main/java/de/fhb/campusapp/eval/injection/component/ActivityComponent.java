package de.fhb.campusapp.eval.injection.component;

import dagger.Subcomponent;
import de.fhb.campusapp.eval.injection.module.ActivityModule;

/**
 * Created by Sebastian Müller on 14.10.2016.
 */
@Subcomponent(modules = {ActivityModule.class})
public interface ActivityComponent {

}
