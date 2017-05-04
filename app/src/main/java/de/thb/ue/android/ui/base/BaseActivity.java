package de.thb.ue.android.ui.base;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import de.thb.ue.android.injection.component.ActivityComponent;
import de.thb.ue.android.injection.module.ActivityModule;
import de.thb.ue.android.ui.BaseApplication;
import de.thb.ue.android.utility.DialogFactory;
import icepick.Icepick;

public class BaseActivity extends AppCompatActivity implements MvpView{

    public ActivityComponent mActicityComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActicityComponent = BaseApplication.getApplicationComponent(this)
                .activityComponent(new ActivityModule(this));
        Icepick.restoreInstanceState(this, savedInstanceState);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * fixes orientation of this activity to portrait when called
     * in onCreate
     */
    protected void fixOrientationToPortrait(){
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }
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
