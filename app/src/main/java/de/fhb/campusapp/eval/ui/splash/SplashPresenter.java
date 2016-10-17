package de.fhb.campusapp.eval.ui.splash;

import android.Manifest;
import android.content.Context;

import com.github.buchandersenn.android_permission_manager.PermissionManager;

import javax.inject.Inject;

import de.fhb.campusapp.eval.injection.ActivityContext;
import de.fhb.campusapp.eval.ui.base.BasePresenter;
import de.fhb.campusapp.eval.utility.ActivityUtil;

/**
 * Created by Sebastian Müller on 09.10.2016.
 */
public class SplashPresenter extends BasePresenter<SplashMvpView>{

    private final Context mActivityContext;

    @Inject
    public SplashPresenter(@ActivityContext Context context) {
        super();
        mActivityContext = context;
    }

    public void requestCameraPermission(PermissionManager manager){
        manager.with(Manifest.permission.CAMERA)
                .onPermissionGranted(() -> getMvpView().startScanActivity())
                .onPermissionDenied(() -> getMvpView().callSaveFinish())
                .onPermissionShowRationale(request -> getMvpView().showCameraExplanation(request))
                .request();

    }


}
