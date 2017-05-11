package de.thb.ue.android.ui.scan;

import android.support.annotation.StringRes;

import de.thb.ue.android.ui.base.MvpView;

/**
 * Created by scorp on 05.05.2017.
 */

public interface ScanMvpView extends MvpView {
    void hideProgressOverlay();
    void showProgressOverlay();
    void setSubtitle(@StringRes int subtitle);
    void restartScanning();
    void goToEvaluation();

}
