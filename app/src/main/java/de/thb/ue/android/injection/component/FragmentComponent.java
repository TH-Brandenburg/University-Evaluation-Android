package de.thb.ue.android.injection.component;

import dagger.Binds;
import dagger.Subcomponent;
import de.thb.ue.android.injection.module.FragmentModule;
import de.thb.ue.android.injection.scopes.PerFragment;
import de.thb.ue.android.ui.evaluation.choice.ButtonFragment;
import de.thb.ue.android.ui.evaluation.send.SendFragment;
import de.thb.ue.android.ui.evaluation.studypath.PathFragment;
import de.thb.ue.android.ui.evaluation.text.TextFragment;

/**
 * Created by scorp on 15.02.2017.
 */
@PerFragment
@Subcomponent(modules = {FragmentModule.class})
public interface FragmentComponent {
    void bind(SendFragment sendFragment);
    void bind(PathFragment pathFragment);
    void bind(ButtonFragment buttonFragment);
    void bind(TextFragment textFragment);

}
