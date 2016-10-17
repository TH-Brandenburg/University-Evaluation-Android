package de.fhb.campusapp.eval.ui.splash;

import android.app.Activity;

import com.github.buchandersenn.android_permission_manager.PermissionRequest;

import de.fhb.campusapp.eval.ui.base.MvpView;

/**
 * Created by Admin on 09.10.2016.
 */
public interface SplashMvpView extends MvpView {
    void showCameraExplanation(PermissionRequest request);
    void startScanActivity();
    void callSaveFinish();
}
