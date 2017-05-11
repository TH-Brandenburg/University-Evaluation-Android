//package de.thb.ue.android.utility.customized_classes;
//
//import android.annotation.TargetApi;
//import android.content.Context;
//import android.os.Build;
//import android.util.AttributeSet;
//import android.view.KeyEvent;
//import android.widget.EditText;
//
//import de.fhb.campusapp.eval.interfaces.PagerAdapterSetPrimary;
//import de.fhb.campusapp.eval.utility.FeatureSwitch;
//
///**
// * Created by Sebastian Mueller on 29.05.2015.
// */
//public class CustomEditText extends EditText {
//
//
//    private PagerAdapterSetPrimary pagerAdapter = null;
//
//    public CustomEditText(Context context) {
//        super(context);
//    }
//
//    public CustomEditText(Context context, AttributeSet attrs) {
//        super(context, attrs);
//    }
//
//    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr) {
//        super(context, attrs, defStyleAttr);
//    }
//
//    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
//    public CustomEditText(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
//        super(context, attrs, defStyleAttr, defStyleRes);
//    }
//
//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event)
//    {
//        if (FeatureSwitch.ENTER_JUMPS_TO_NEXT_QUESTION && keyCode== KeyEvent.KEYCODE_ENTER)
//        {
//            if(pagerAdapter != null){
//                pagerAdapter.incrementPrimaryFragment();
//                return true;
//            } else {
//                return super.onKeyDown(keyCode, event);
//            }
//
//        }
//        // Handle all other keys in the default way
//        return super.onKeyDown(keyCode, event);
//    }
//
//    public void setPagerAdapter(PagerAdapterSetPrimary pagerAdapter) {
//        this.pagerAdapter = pagerAdapter;
//    }
//}
