package de.fhb.campusapp.eval.ui.scan;

import android.support.annotation.StringRes;

import com.github.buchandersenn.android_permission_manager.PermissionRequest;

import javax.annotation.Resource;

import de.fhb.campusapp.eval.ui.base.MvpView;

/**
 * Created by Sebastian Müller on 16.10.2016.
 */
public interface ScanMvpView extends MvpView {
    void hideProgressOverlay();
    void showProgressOverlay();
    void changeToolbarTitle(String title);
    void changeToolbarTitle(@StringRes int title);
    void setRequestRunning(boolean running);
    void showInternetExplanation(PermissionRequest request);
    void callSaveTerminateTask();
    void showRetryScanDialog(String title, String message);
    void showRetryServerCommunicationDialog(String title, String message);
    void showNetworkErrorDialog(String errorTitle, String errorMessage);
    void startEvaluationActivity();

}
