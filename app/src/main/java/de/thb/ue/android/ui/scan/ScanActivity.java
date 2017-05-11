package de.thb.ue.android.ui.scan;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.widget.Toolbar;
import android.view.View;

import com.abhi.barcode.frag.libv2.BarcodeFragment;
import com.abhi.barcode.frag.libv2.ICameraManagerListener;
import com.abhi.barcode.frag.libv2.IScanResultHandler;
import com.abhi.barcode.frag.libv2.ScanResult;
import com.github.buchandersenn.android_permission_manager.PermissionManager;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.camera.CameraManager;

import java.util.EnumSet;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.thb.ue.android.ui.base.BaseActivity;
import de.thb.ue.android.ui.evaluation.EvaluationActivity;
import thb.de.ue.android.R;

public class ScanActivity extends BaseActivity implements ScanMvpView, IScanResultHandler, ICameraManagerListener{

    @BindView(R.id.the_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.progress_overlay)
    View mProgeressOverlay;

    @Inject
    ScanPresenter mScanPresenter;

    @Inject
    PermissionManager mPermissionManager;

    private BarcodeFragment mBarcodeFragment;
    private CameraManager mCameraManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        super.mActicityComponent.bind(this);
        super.fixOrientationToPortrait();
        mScanPresenter.attachView(this);

        setSupportActionBar(mToolbar);
        setSubtitle(R.string.scan_search);

        initBarcodeFragment();
    }

    /**
     * inititializes the barcode fragment
     */
    private void initBarcodeFragment(){
        if(mBarcodeFragment == null){
            mBarcodeFragment = (BarcodeFragment) getSupportFragmentManager().findFragmentById(R.id.scan_view);
            //mBarcodeFragment.setAlwaysDecodeOnResume(true); // Restore old behaviour.
            mBarcodeFragment.setScanResultHandler(this);
            mBarcodeFragment.setCameraManagerListener(this);
            mBarcodeFragment.setAlwaysDecodeOnResume(false);
            mBarcodeFragment.setDecodeFor(EnumSet.of(BarcodeFormat.QR_CODE));
            mBarcodeFragment.restart();
        }
    }


    @Override
    public void setCameraManager(CameraManager manager) {
        mCameraManager = manager;
    }

    @Override
    public void scanResult(ScanResult result) {
        mScanPresenter.processScanResult(result, mPermissionManager);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        mPermissionManager.handlePermissionResult(requestCode, grantResults);
    }

    @Override
    public void hideProgressOverlay() {
        mProgeressOverlay.animate()
                .alpha(0)
                .setDuration(200)
                .start();
    }

    @Override
    public void showProgressOverlay() {
        mProgeressOverlay.animate()
                .alpha(0.7f)
                .setDuration(200)
                .start();
    }

    @Override
    public void setSubtitle(@StringRes int subtitle) {
        if(getSupportActionBar() != null){
            getSupportActionBar().setSubtitle(subtitle);
        }
    }

    @Override
    public void restartScanning() {
        mBarcodeFragment.restart();
    }

    @Override
    public void goToEvaluation() {
        Intent intent = new Intent(this, EvaluationActivity.class);
        startActivity(intent);
        this.finish();
    }
}
