package de.fhb.campusapp.eval.ui.splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;

import com.github.buchandersenn.android_permission_manager.PermissionManager;
import com.github.buchandersenn.android_permission_manager.PermissionRequest;

import javax.inject.Inject;

import de.fhb.campusapp.eval.activities.ScanActivity;
import de.fhb.campusapp.eval.ui.EvaluationApplication;
import de.fhb.campusapp.eval.ui.base.BaseActivity;
import de.fhb.campusapp.eval.utility.ActivityUtil;
import de.fhb.campusapp.eval.utility.DialogFactory;
import fhb.de.campusappevaluationexp.R;

public class SplashActivity extends BaseActivity implements SplashMvpView {

    @Inject
    SplashPresenter mSplashPresenter;

    @Inject
    PermissionManager mPermissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.mActicityComponent.bind(this);
        super.fixOrientationToPortrait();


//        mPermissionManager = PermissionManager.create(this);

//        mSplashPresenter = new SplashPresenter();
        mSplashPresenter.attachView(this);
        mSplashPresenter.requestCameraPermission(mPermissionManager);

    }

    @Override
    protected void onDestroy() {
        mSplashPresenter.detachView();
        super.onDestroy();
    }

    @Override
    public void showCameraExplanation(PermissionRequest request) {
        AlertDialog dialog = DialogFactory.createAcceptDenyDialog(this
                ,R.string.camera_permission_explanation_title
                ,R.string.camera_permission_explanation_message
                ,(dialogInterface, i) -> request.acceptPermissionRationale()
                ,(dialogInterface, i) -> ActivityUtil.saveFinish(this));
        dialog.show();
    }

    @Override
    public void startScanActivity(){
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mPermissionManager.handlePermissionResult(requestCode, grantResults);
    }
}
