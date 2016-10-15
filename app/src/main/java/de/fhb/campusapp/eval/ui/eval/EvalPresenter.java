package de.fhb.campusapp.eval.ui.eval;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.content.ContextCompat;

import com.commonsware.cwac.cam2.CameraActivity;
import com.commonsware.cwac.cam2.Facing;
import com.github.buchandersenn.android_permission_manager.PermissionManager;

import java.io.File;

import de.fhb.campusapp.eval.ui.base.BasePresenter;
import de.fhb.campusapp.eval.utility.ActivityUtil;
import de.fhb.campusapp.eval.utility.Utility;

/**
 * Created by Sebastian Müller on 09.10.2016.
 */
public class EvalPresenter extends BasePresenter<EvaluationActivity>{

    private static final int REQUEST_CAPTURE_IMAGE = 111;

    public File startCameraIntent(String intentImageName){
        boolean permissionsGranted =
                PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getMvpView(), Manifest.permission.CAMERA)
             && PackageManager.PERMISSION_GRANTED == ContextCompat.checkSelfPermission(getMvpView(), Manifest.permission.WRITE_EXTERNAL_STORAGE);

        File intentImage = null;

        if(permissionsGranted){
           intentImage = startIntent(intentImageName);
        }

        return intentImage;
    }

    private File startIntent(String intentImageName){
        // create Intent to take a picture and return control to the calling application
        File intentImage =  Utility.createImageFile(intentImageName, getMvpView());
        Intent intent = new CameraActivity.IntentBuilder(getMvpView())
                .skipConfirm()
                .facing(Facing.BACK)
                .to(intentImage)
                .build();

        getMvpView().startActivityForResult(intent, REQUEST_CAPTURE_IMAGE);
        return intentImage;
    }

    public void requestCameraPermission(PermissionManager manager){
        manager.with(Manifest.permission.CAMERA)
                .onPermissionShowRationale(request -> getMvpView().showCameraExplanation(request))
                .request();
    }

    public void requestStoragePermission(PermissionManager manager){
        manager.with(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .onPermissionShowRationale(request -> getMvpView().showStorageExplanation(request))
                .request();
    }
}
