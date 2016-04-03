package de.thb.ue.android.activities;

import android.os.Bundle;
import android.support.annotation.Nullable;

import de.thb.ue.android.utility.EventBus;
import roboguice.activity.RoboActionBarActivity;

/**
 * Created by Admin on 14.12.2015.
 */
public abstract class BaseActivity extends RoboActionBarActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getEventBus().register(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventBus.getEventBus().unregister(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
