package de.thb.ue.android.ui.base;

import android.content.Context;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import de.thb.ue.android.injection.component.FragmentComponent;
import de.thb.ue.android.injection.module.FragmentModule;
import de.thb.ue.android.utility.DialogFactory;
import icepick.Icepick;


/**
 * Created by Sebastian Müller on 15.12.2015.
 */
public abstract class BaseFragment extends DialogFragment implements MvpView{

    public FragmentComponent mFragmentComponent;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        Icepick.saveInstanceState(this, outState);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Icepick.restoreInstanceState(this, savedInstanceState);

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mFragmentComponent = ((BaseActivity) getActivity()).mActicityComponent
                .fragmentComponent(new FragmentModule(getActivity()));
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
    }


    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void displayToast(final String toastText) {
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(getActivity(), toastText, duration);
        toast.show();
    }

    @Override
    public void displayGenericErrorDialog(String title, String message) {
        DialogFactory.createSimpleOkErrorDialog(getActivity()
                ,title
                ,message
                ,null
                ,null
                ,true)
                .show();
    }

    public void configureAsDialog(View rootView){
        if(getDialog() != null){
            getDialog().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

            if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
                rootView.post(() -> {
                    // retrieve display dimensions
                    Point point = new Point();
                    Window window = getDialog().getWindow();
                    window.getWindowManager().getDefaultDisplay().getSize(point);

                    WindowManager.LayoutParams lp = new WindowManager.LayoutParams();
                    lp.copyFrom(getDialog().getWindow().getAttributes());
                    lp.width = (int)(point.x * 0.9f);
                    lp.height = (int)(point.y * 0.9f);

                    getDialog().getWindow().setAttributes(lp);
                });
            }
        }

    }
}
