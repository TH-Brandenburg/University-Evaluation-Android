package de.fhb.campusapp.eval.ui.eval;

import android.app.Activity;

import com.github.buchandersenn.android_permission_manager.PermissionRequest;

import de.fhb.campusapp.eval.ui.base.MvpView;

/**
 * Created by Sebastian Müller on 09.10.2016.
 */
public interface EvalMvpView extends MvpView {
    void showCameraExplanation(PermissionRequest request);
    void showStorageExplanation(PermissionRequest request);

}
