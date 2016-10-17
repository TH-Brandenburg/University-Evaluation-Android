package de.fhb.campusapp.eval.ui.textfragment;

import com.github.buchandersenn.android_permission_manager.PermissionRequest;

import java.io.File;

import de.fhb.campusapp.eval.ui.base.MvpView;

/**
 * Created by Sebastian Müller on 14.10.2016.
 */
public interface TextMvpView extends MvpView{
    void showCameraAndStorageExplanation(PermissionRequest request);
    boolean isCameraPermissionGranted();
    boolean isStoragePermissionGranted();
    File startCameraIntent();
}
