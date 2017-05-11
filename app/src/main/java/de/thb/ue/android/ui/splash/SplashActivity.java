package de.thb.ue.android.ui.splash;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import com.github.buchandersenn.android_permission_manager.PermissionManager;
import com.github.buchandersenn.android_permission_manager.PermissionRequest;

import java.security.Permission;

import javax.inject.Inject;

import butterknife.ButterKnife;
import de.thb.ue.android.ui.base.BaseActivity;
import de.thb.ue.android.ui.scan.ScanActivity;
import de.thb.ue.android.utility.DialogFactory;
import thb.de.ue.android.R;

public class SplashActivity extends BaseActivity implements SplashMvpView{
    @Inject
    SplashPresenter mSplashPresenter;

    @Inject
    PermissionManager mPermissionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        ButterKnife.bind(this);
        super.mActicityComponent.bind(this);
        super.fixOrientationToPortrait();
        mSplashPresenter.attachView(this);

        mPermissionManager.with(Manifest.permission.CAMERA)
                .onPermissionDenied(this::finish)
                .onPermissionGranted(this::goToScanActivity)
                .onPermissionShowRationale(this::showCameraExplanation)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mPermissionManager.handlePermissionResult(requestCode, grantResults);
    }

    @Override
    protected void onDestroy() {
        mSplashPresenter.detachView();
        super.onDestroy();
    }

    private void goToScanActivity() {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
        this.finish();
    }

    public void showCameraExplanation(PermissionRequest request) {
        AlertDialog dialog = DialogFactory.createAcceptDenyDialog(this
                , R.string.camera_permission_explanation_title
                , R.string.camera_permission_explanation_message
                , (dialogInterface, i) -> request.acceptPermissionRationale()
                , (dialogInterface, i) -> this.finish());
        dialog.show();
    }
}
