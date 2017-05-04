package de.thb.ue.android.injection.component;

import dagger.Subcomponent;
import de.thb.ue.android.injection.module.FragmentModule;
import de.thb.ue.android.injection.scopes.PerFragment;

/**
 * Created by scorp on 15.02.2017.
 */
@PerFragment
@Subcomponent(modules = {FragmentModule.class})
public interface FragmentComponent {

}
