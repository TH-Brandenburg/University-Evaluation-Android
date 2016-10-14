package de.fhb.campusapp.eval.ui.splash;

import android.Manifest;

import com.github.buchandersenn.android_permission_manager.PermissionManager;

import de.fhb.campusapp.eval.ui.base.BasePresenter;
import de.fhb.campusapp.eval.utility.ActivityUtil;

/**
 * Created by Sebastian Müller on 09.10.2016.
 */
public class SplashPresenter extends BasePresenter<SplashActivity>{

    public void requestCameraPermission(PermissionManager manager){
        manager.with(Manifest.permission.CAMERA)
                .onPermissionGranted(() -> getMvpView().startScanActivity())
                .onPermissionDenied(() -> ActivityUtil.saveFinish(getMvpView()))
                .onPermissionShowRationale(request -> getMvpView().showCameraExplanation(request))
                .request();

    }


}
