package de.fhb.campusapp.eval.custom;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;

import de.fhb.campusapp.eval.utility.FeatureSwitch;

/**
 * Created by Sebastian Mueller on 18.05.2015.
 */
public class CustomViewPager extends ViewPager {

    private float mStartDragX;
    private Activity mActivity;
    private CustomViewPagerCommunicator mCustomViewPagerCommunicator;
    private int mOldPosition = 0;

    public CustomViewPager(Context context) {
        super(context);
        this.setOnPageChangeListener(new CustomOnPageChangeListener());

    }

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setOnPageChangeListener(new CustomOnPageChangeListener());

    }

    public void setmCustomViewPagerCommunicator(CustomViewPagerCommunicator mCustomViewPagerCommunicator) {
        this.mCustomViewPagerCommunicator = mCustomViewPagerCommunicator;
    }


    private class CustomOnPageChangeListener implements OnPageChangeListener {
        @Override
        public void onPageSelected(int position) {
            SwipeDirectionEnum direction;
            direction = position < mOldPosition ? SwipeDirectionEnum.left : SwipeDirectionEnum.right;
            mOldPosition = position;
            if(mCustomViewPagerCommunicator.isCameraSymbolNeeded()){
                mCustomViewPagerCommunicator.changeToolbarIcons(true);
            } else {
                mCustomViewPagerCommunicator.changeToolbarIcons(false);
            }

            if (mCustomViewPagerCommunicator.isKeyboardNeeded()) {
                if(FeatureSwitch.AUTO_KEYBOARD){
                    mCustomViewPagerCommunicator.setLayoutResizing();
                    mCustomViewPagerCommunicator.showKeyboard();
                }
            } else {
                mCustomViewPagerCommunicator.setLayoutOverlapping();
                mCustomViewPagerCommunicator.hideKeyboard();
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
        }

        @Override
        public void onPageScrolled(int position, float offset, int offsetPixels) {
        }
    }

    public interface CustomViewPagerCommunicator {
         boolean isKeyboardNeeded();

         boolean isCameraSymbolNeeded();

         void hideKeyboard();

         void showKeyboard(/*SwipeDirectionEnum direction*/);

         void setLayoutResizing();

         void setLayoutOverlapping();

         void changeToolbarIcons(boolean isCameraSymbolNeeded);
    }

    public enum SwipeDirectionEnum {
        left,
        right
    }


}
