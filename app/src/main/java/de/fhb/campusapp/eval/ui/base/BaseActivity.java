package de.fhb.campusapp.eval.ui.base;

import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import de.fhb.campusapp.eval.injection.component.ActivityComponent;
import de.fhb.campusapp.eval.injection.module.ActivityModule;
import de.fhb.campusapp.eval.ui.EvaluationApplication;
import de.fhb.campusapp.eval.utility.EventBus;


public class BaseActivity extends AppCompatActivity {

    public ActivityComponent mActicityComponent;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActicityComponent = EvaluationApplication.getApplicationComponent(this)
                .activityComponent(new ActivityModule(this));

    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.get().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.get().unregister(this);
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

}
