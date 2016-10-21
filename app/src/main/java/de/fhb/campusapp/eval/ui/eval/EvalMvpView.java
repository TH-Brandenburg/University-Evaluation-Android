package de.fhb.campusapp.eval.ui.eval;

import android.app.Activity;

import com.github.buchandersenn.android_permission_manager.PermissionRequest;

import java.io.File;
import java.util.List;

import de.fhb.campusapp.eval.ui.base.MvpView;

/**
 * Created by Sebastian Müller on 09.10.2016.
 */
public interface EvalMvpView extends MvpView {
    void showProgressOverlay();
    void hideProgressOverlay();
    void showCameraExplanationDialog(PermissionRequest request);
    void showStorageExplanationDialog(PermissionRequest request);
    void showInternetExplanationDialog(PermissionRequest request);
    void showDebugMessage();
    void showNetworkErrorDialog(String title, String message);
    void showSuccessDialog();
    void showRequestErrorRestartDialog(String title, String message);
    void showRequestErrorRetryDialog(String title, String message);
    void callSaveTerminateTask();
    File zipPictureFiles(List<File> imageFileList);
    void restartApp();

}
