package de.fhb.campusapp.eval.custom;

import android.content.Context;
import android.os.Build;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import javax.inject.Inject;

import de.fhb.campusapp.eval.data.IDataManager;
import de.fhb.campusapp.eval.ui.eval.EvaluationActivity;

/**
 * Created by Sebastian Mueller on 18.05.2015.
 */
public class CustomViewPager extends ViewPager {

    private IDataManager mDataManager;

    public CustomViewPager(Context context) {
        super(context);
    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        // todo couples this class to evaluation activity. Better solution??
        ((EvaluationActivity) context).mActicityComponent.bind(this);
    }

    @Inject
    public void setmDataManager(IDataManager dataManager){
        mDataManager = dataManager;
        if(Build.VERSION.SDK_INT < 21){
            this.setOnPageChangeListener(new CustomOnPageChangeListener());
        } else {
            this.addOnPageChangeListener(new CustomOnPageChangeListener());
        }
    }

    private class CustomOnPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {

            // TODO is this actually ok??
            mDataManager.setmCurrentPagerPosition(position);
            mDataManager.broadcastSecondPagingEvent();

//            if(mCustomViewPagerCommunicator.isCameraSymbolNeeded()){
//                mCustomViewPagerCommunicator.changeToolbarIcons(true);
//            } else {
//                mCustomViewPagerCommunicator.changeToolbarIcons(false);
//            }

//            if (mCustomViewPagerCommunicator.isKeyboardNeeded()) {
//                if(FeatureSwitch.AUTO_KEYBOARD){
//                    mCustomViewPagerCommunicator.setLayoutResizing();
//                    mCustomViewPagerCommunicator.showKeyboard();
//                }
//            } else {
//                mCustomViewPagerCommunicator.setLayoutOverlapping();
//                mCustomViewPagerCommunicator.hideKeyboard();
//            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int position, float offset, int offsetPixels) {
        }
    }

//    public interface CustomViewPagerCommunicator {
//         boolean isKeyboardNeeded();
//
//         boolean isCameraSymbolNeeded();
//
//         void hideKeyboard();
//
//         void showKeyboard(/*SwipeDirectionEnum direction*/);
//
//         void setLayoutResizing();
//
//         void setLayoutOverlapping();
//
//         void changeToolbarIcons(boolean isCameraSymbolNeeded);
//    }

}
