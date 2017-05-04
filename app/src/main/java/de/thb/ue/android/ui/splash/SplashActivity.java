package de.thb.ue.android.ui.splash;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.github.buchandersenn.android_permission_manager.PermissionManager;

import javax.inject.Inject;

import butterknife.ButterKnife;
import de.thb.ue.android.ui.base.BaseActivity;
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

    @Override
    public void displayToast(final String toastText) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(this, toastText, duration);
        toast.show();
    }

    @Override
    public void displayGenericErrorDialog(String title, String message) {
        DialogFactory.createSimpleOkErrorDialog(this
                ,title
                ,message
                ,null
                ,null
                ,true)
                .show();
    }
}
