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
    void showCameraExplanation(PermissionRequest request);
    void showStorageExplanation(PermissionRequest request);
    void showInternetExplanation(PermissionRequest request);
    void showDebugMessage();
    void callSaveFinish();
    File zipPictureFiles(List<File> imageFileList);

}
