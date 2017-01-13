package de.fhb.campusapp.eval.ui.eval;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListPopupWindow;

import com.github.buchandersenn.android_permission_manager.PermissionManager;
import com.github.buchandersenn.android_permission_manager.PermissionRequest;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.tuple.Triple;

import java.io.File;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.fhb.ca.dto.ResponseDTO;
import de.fhb.campusapp.eval.custom.CustomFragmentStatePagerAdapter;
import de.fhb.campusapp.eval.custom.CustomScroller;
import de.fhb.campusapp.eval.custom.CustomViewPager;
import de.fhb.campusapp.eval.custom.CustomWindowPopupAdapter;
import de.fhb.campusapp.eval.data.local.RetrofitHelper;
import de.fhb.campusapp.eval.interfaces.PagerAdapterSetPrimary;
import de.fhb.campusapp.eval.interfaces.ProgressCommunicator;
import de.fhb.campusapp.eval.interfaces.RequestCommunicator;
import de.fhb.campusapp.eval.ui.base.BaseActivity;
import de.fhb.campusapp.eval.ui.scan.ScanActivity;
import de.fhb.campusapp.eval.ui.sendfragment.SendFragment;
import de.fhb.campusapp.eval.ui.textfragment.TextFragment;
import de.fhb.campusapp.eval.utility.ActivityUtil;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.DialogFactory;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.NetworkErrorEvent;
import de.fhb.campusapp.eval.utility.Events.PhotoTakenEvent;
import de.fhb.campusapp.eval.utility.Events.PreServerCommunicationEvent;
import de.fhb.campusapp.eval.utility.Events.RequestErrorEvent;
import de.fhb.campusapp.eval.utility.Events.RequestSuccessEvent;
import de.fhb.campusapp.eval.utility.Events.StartServerCommunicationEvent;
import de.fhb.campusapp.eval.utility.FeatureSwitch;
import de.fhb.campusapp.eval.utility.Observer.CreateUploadImageObservable;
import de.fhb.campusapp.eval.utility.Utility;
import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;
import de.fhb.campusapp.eval.utility.vos.TextQuestionVO;
import fhb.de.campusappevaluationexp.R;
import retrofit2.Retrofit;
import rx.android.schedulers.AndroidSchedulers;

public class EvaluationActivity extends BaseActivity implements ProgressCommunicator, PagerAdapterSetPrimary, ViewPager.PageTransformer,
        CustomViewPager.CustomViewPagerCommunicator, AdapterView.OnItemClickListener, TextFragment.TextFragmentCommunicator,
        RequestCommunicator, SendFragment.SendFragmentCommunicator, EvalMvpView{

    /**
     * Constant used to put and extract the state of mListPopupReopen from Bundle
     */
    private static final String MLIST_POPUP_REOPEN = "MLIST_POPUP_REOPEN";
    private static final String MLIST_POPUP_TOGGLE = "MLIST_POPUP_TOGGLE";
    private static final String NORMAL_MODE_DISPLAYED = "NORMAL_MODE_DISPLAYED";
    //    private static final String EDIT_MODE_DISPLAYED = "EDIT_MODE_DISPLAYED";
    private static final String CAMERA_MODE_DISPLAYED = "CAMERAL_MODE_DISPLAYED";
    private static final String RESTORE_TOOLBAR = "RESTORE_TOOLBAR";
    private static final int REQUEST_CAPTURE_IMAGE = 1;

    @BindView(R.id.button_pager)
    CustomViewPager mViewPager;

    @BindView(R.id.progress_overlay)
    View mProgressOverlay;

    @BindView(R.id.my_awesome_toolbar)
    Toolbar mToolbar;

    /**
     * The navigation utility directly below the toolbar.
     */
    @BindView(R.id.button_pager_tab_strip)
    PagerTabStrip mPagerTabStrip;

    @Inject
    RetrofitHelper mRetrofitHelper;

    @Inject
    Resources mResources;

    @Inject
    EvalPresenter mEvalPresenter;

    @Inject
    PermissionManager mPermissionManager;

    /**
     * Overlay ListView used to navigate within the app. Placed in ActionBar
     */
    private ListPopupWindow mListPopupWindow;

    /**
     * Adapter used for the ViewPager. see @CustomFragmentStatePagerAdapter
     */
    private CustomWindowPopupAdapter mListAdapter;
    //    protected SpiceManager spiceManager = new SpiceManager(CustomJsonSpiceService.class);
    private CustomFragmentStatePagerAdapter mCollectionPagerAdapter;
    private List<Uri> mPictureList = new ArrayList<>();
    /**
     *
     */
    private File mCurrentIntentImage = null;
    /**
     * represents the question displayed on the currently visible fragment.
     * Used to appropriatly name the pictures made on text based questions
     */
    private String mCurrentQuestionText;
    private String mCurrentImageName;

    //    private Retrofit mRetrofit;
    private Retrofit mRetrofit;

    /**
     * Used to create a toggle effect when clicking the ActionBar icon.
     * First click opens the window second one closes it again.
     */
    private boolean mListPopupToggle = false;

    /**
     * Used to reopen the ListView on perspective changes
     */
    private boolean mListPopupReopen = false;

    /**
     * used to identify occasions in which the actionBar must change.
     */
    private boolean mKeyboardNeeded = false;

    /**
     * Needed to detect changes in keyboard requirement
     */
    private boolean mIsKeyboardNeededOld = false;

    /**
     * Set to true ,if the action bar has to display the camera-symbol
     * Has to be false if mEditIconNeeded is true.
     */
    private boolean mCameraIconNeeded = false;

    /**
     * Used to restore the action bar to its proper state
     * for the current fragment after a change of perspective
     */
    private boolean mRestoreToolbar = false;

    /**
     * Used to properly restore the actionBar after perspective change
     */
    private boolean mIsCameraModeDisplayed = false;

    /**
     * Used to properly restore the actionBar after perspective change
     */
    private boolean mIsNormalModeDisplayed = false;

    public EvaluationActivity() {
        super();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_button);
        ButterKnife.bind(this);
        super.mActicityComponent.bind(this);

        mEvalPresenter.attachView(this);

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }

        // fixes the orientation to portrait
        super.fixOrientationToPortrait();

        setSupportActionBar(mToolbar);

        //just in case it became null thanks to android
        DataHolder.setPreferences(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        mCollectionPagerAdapter = new CustomFragmentStatePagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mCollectionPagerAdapter);
        mViewPager.setmCustomViewPagerCommunicator(this);
        mViewPager.setPageTransformer(true, this);

        // Manipulating the animation speed of the view pager is not easy.
        // Reflection is necessary. Either that or alter the class directly within the android support package.
        if (FeatureSwitch.CUSTOM_PAGER_ANIMATION) {
            try {
                Field mScroller;
                mScroller = ViewPager.class.getDeclaredField("mScroller");
                mScroller.setAccessible(true);
                CustomScroller scroller = new CustomScroller(mViewPager.getContext());
                // scroller.setFixedDuration(5000);
                mScroller.set(mViewPager, scroller);
            } catch (NoSuchFieldException | IllegalArgumentException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }

//        ArrayList<String> navigationEntrys = (ArrayList<String>) constructNavList();

        mListAdapter = new CustomWindowPopupAdapter(this, R.layout.nav_list);
        mListAdapter.setProgressCommunicator(this);
        mListPopupWindow = new ListPopupWindow(this);
        mListPopupWindow.setAdapter(mListAdapter);
        mListPopupWindow.setOnItemClickListener(this);

        //reopen ListView if perspective was changed while it was open
        if (savedInstanceState != null) {
            mListPopupReopen = savedInstanceState.getBoolean(MLIST_POPUP_REOPEN);
            mListPopupToggle = savedInstanceState.getBoolean(MLIST_POPUP_TOGGLE);
            mIsCameraModeDisplayed = savedInstanceState.getBoolean(CAMERA_MODE_DISPLAYED);
            mIsNormalModeDisplayed = savedInstanceState.getBoolean(NORMAL_MODE_DISPLAYED);
            mRestoreToolbar = savedInstanceState.getBoolean(RESTORE_TOOLBAR);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

//        spiceManager.start(this);

        // reopen ListPopup if it was closed due to orientation change
        if (mListPopupReopen) {
            final View view = findViewById(R.id.my_awesome_toolbar);
            // ListPopup can only be opened after all lifecycle methods have finished
            view.post(new Runnable() {
                @Override
                public void run() {
                    mListPopupWindow.setAnchorView(view);
                    mListPopupWindow.show();
                }
            });
        }

        // restores the mToolbar state when change of perspective occurs
        if (mRestoreToolbar) {
            if (mIsCameraModeDisplayed) {
                invalidateOptionsMenu();
            }
            if (mIsNormalModeDisplayed) {
                invalidateOptionsMenu();
            }

        }

        // do this to ensure that the camera symbol is also displayed
        // when TextQuestion is first type of question in a questionnaire
        if (isCameraSymbolNeeded()) {
            changeToolbarIcons(true);
        }

    }

    @Override
    protected void onResume() {
        // DataHolder gets ability to freely serialize/deserialize its variables
        // Android might clear variable in DataHolder while App is in background leading to shit.
        DataHolder.setPreferences(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        DataHolder.storeAllData();

        mEvalPresenter.registerToEventBus();

        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.action_bar_navigator_only, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mKeyboardNeeded) {
            menu.removeItem(R.id.question_search);
            getMenuInflater().inflate(R.menu.action_bar_camera, menu);
            mIsCameraModeDisplayed = true;
            mIsNormalModeDisplayed = false;
        } else if (mCameraIconNeeded) {
            //remove everything in order to avoid duplicate symbols
            menu.removeItem(R.id.camera_activation);
            menu.removeItem(R.id.question_search);
            getMenuInflater().inflate(R.menu.action_bar_camera, menu);
            mIsCameraModeDisplayed = true;
            mIsNormalModeDisplayed = false;
        }
        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.question_search && !mListPopupToggle) {
//            View view = findViewById(R.id.action_bar_navigator_only);
            View view = findViewById(R.id.my_awesome_toolbar);
            mListPopupWindow.setAnchorView(view);
            mListPopupWindow.show();
            mListPopupToggle = true;
        } else if (id == R.id.question_search) {
            mListPopupWindow.dismiss();
            mListPopupToggle = false;
            mListPopupReopen = false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setIntentImage(File intentImage) {
        mCurrentIntentImage = intentImage;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mPermissionManager.handlePermissionResult(requestCode, grantResults);
    }

    @Override
    public void showCameraExplanationDialog(PermissionRequest request) {
        Dialog dialog  = DialogFactory.createSimpleOkErrorDialog(this
                , R.string.camera_permission_explanation_title
                , R.string.camera_permission_explanation_message
                , (dialogInterface, i) -> request.acceptPermissionRationale()
                , null
                , true);
        dialog.show();
    }

    @Override
    public void showStorageExplanationDialog(PermissionRequest request) {
        Dialog dialog  = DialogFactory.createSimpleOkErrorDialog(this
                , R.string.storage_permission_explanation_title
                , R.string.storage_permission_explanation_message
                , (dialogInterface, i) -> request.acceptPermissionRationale()
                , null
                , true);
        dialog.show();
    }

    @Override
    public void showInternetExplanationDialog(PermissionRequest request) {
        Dialog dialog  = DialogFactory.createSimpleOkErrorDialog(this
                , R.string.internet_explanation_title
                , R.string.internet_explanation_message
                , (dialogInterface, i) -> request.acceptPermissionRationale()
                , null
                , true);
        dialog.show();
    }

    @Override
    public void showDebugMessage() {
            DialogFactory.createSimpleOkErrorDialog(this,
                    "Abort Debug", "Host not set and debug mode active. Aborting"
                    , (dialogInterface, i) -> restartApp()
                    , dialogInterface1 -> restartApp()
                    , true).show();
    }

    @Override
    public void showNetworkErrorDialog(String title, String message) {
        Dialog dialog  = DialogFactory.createAcceptDenyDialog(this
                , title
                , message
                , getString(R.string.retry_button)
                , getString(R.string.abort_button)
                , true
                , (dialogInterface, i) -> {
                    onStartServerCommunication();
                    showProgressOverlay();
                }
                , null
                , null);
        dialog.show();
    }

    @Override
    public void showSuccessDialog(){
        Dialog dialog  = DialogFactory.createSimpleOkErrorDialog(this
                , R.string.answers_transmission_success_title
                , R.string.answers_transmission_success_message
                , (dialogInterface, i) -> restartApp()
                , null
                , false);
        dialog.show();
    }

    @Override
    public void showRequestErrorRestartDialog(String title, String message) {
        Dialog dialog  = DialogFactory.createSimpleOkErrorDialog(this
                , title
                , message
                , (dialogInterface, i) -> restartApp()
                , null /* dialogInterface -> restartApp()*/
                , false);
        dialog.show();
    }

    @Override
    public void showRequestErrorRetryDialog(String title, String message) {
        Dialog dialog  = DialogFactory.createAcceptDenyDialog(this
                , title
                , message
                , getString(R.string.retry_button)
                , getString(R.string.abort_button)
                , true
                , (dialogInterface, i) -> {
                    onStartServerCommunication();
                    showProgressOverlay();
                }
                , null
                , null);
        dialog.show();
    }


    @Override
    public void callSaveTerminateTask() {
        ActivityUtil.saveTerminateTask(this);
    }

    @Override
    public File zipPictureFiles(List<File> imageFileList) {
        return Utility.zipFiles(this,(ArrayList<File>) imageFileList);
    }

    @Override
    public void restartApp() {
        Intent intent = new Intent(this, ScanActivity.class);
        intent.putExtra("GO_TO_SCAN", true);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mListPopupWindow.isShowing()) {
            mListPopupWindow.dismiss();
            mListPopupReopen = true;
        }
        mRestoreToolbar = true;
        mEvalPresenter.unregisterFromEventBus();
    }

    @Override
    protected void onStop() {
        DataHolder.storeAllData();
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        mEvalPresenter.detachView();

        super.onDestroy();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(MLIST_POPUP_REOPEN, mListPopupReopen);
        outState.putBoolean(MLIST_POPUP_TOGGLE, mListPopupToggle);
        outState.putBoolean(CAMERA_MODE_DISPLAYED, mIsCameraModeDisplayed);
        outState.putBoolean(NORMAL_MODE_DISPLAYED, mIsNormalModeDisplayed);
        outState.putBoolean(RESTORE_TOOLBAR, mRestoreToolbar);

        super.onSaveInstanceState(outState);
    }

    @Override
    public int getProgress() {
        return mViewPager.getCurrentItem();
    }

    @Override
    public void setPrimaryFragment(int newPosition) {
        mViewPager.setCurrentItem(newPosition);
    }

    @Override
    public void incrementPrimaryFragment() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
    }

    @Override
    public void decrementPrimaryFragment() {
        mViewPager.setCurrentItem(mViewPager.getCurrentItem() - 1);
    }

    @Override
    public void onBackPressed() {
        if (mListPopupWindow.isShowing()) {
            mListPopupWindow.dismiss();
            mListPopupToggle = false;
        } else if (mViewPager.getCurrentItem() > 0) {
            setPrimaryFragment(mViewPager.getCurrentItem() - 1);
        }
    }

    /**********************************************************
     * START CustomViewPagerCommunicator IMPLEMENTATION SECTION
     **********************************************************/

    @Override
    public boolean isKeyboardNeeded() {
        boolean needed = false;

        if (mCollectionPagerAdapter != null && mViewPager != null) {
            Fragment fragment = mCollectionPagerAdapter.getFragmentAtPosition(mViewPager.getCurrentItem());

            if (fragment != null && fragment.getClass() == TextFragment.class ) {
                // only show keyboard if textView was not written in before and no picture was previously taken
                TextFragment textFragment = (TextFragment) fragment;
                TextAnswerVO textAnswerVO =  DataHolder.isTextQuestionAnswered(textFragment.getmQuestion());
                ImageDataVO pathObj = DataHolder.getCommentaryImageMap().get(textFragment.getmQuestion());
                if((textAnswerVO == null || textAnswerVO.getAnswerText().equals("")) && pathObj == null){
                    needed = true;
                }
            }
        }
        return needed;
    }

    @Override
    public boolean isCameraSymbolNeeded() {
        boolean needed = false;

        if (mCollectionPagerAdapter != null && mViewPager != null && FeatureSwitch.TOOLBAR_CAMERA_ICON) {
            Fragment fragment = mCollectionPagerAdapter.getFragmentAtPosition(mViewPager.getCurrentItem());

            if (fragment != null && fragment.getClass() == TextFragment.class ) {
                TextFragment textFragment = (TextFragment) fragment;
                TextQuestionVO dto = DataHolder.retrieveTextQuestionVO(textFragment.getmQuestion());
                // numerical answers mustnt be answered with a photo. That would be dumb.
                if(!dto.getOnlyNumbers()){
                    needed = true;
                }
            }
        }
        return needed;
    }

    @Override
    public void hideKeyboard() {
        Utility.setKeyboardOverlapping(this);
        Fragment fragment = mCollectionPagerAdapter.getFragmentAtPosition(
                mViewPager.getCurrentItem());
        if (fragment != null && fragment.getView() != null) {
            Utility.hideSoftKeyboard(fragment.getView(), this);
        }

    }

    @Override
    public void showKeyboard(/*CustomViewPager.SwipeDirectionEnum direction*/) {

        Fragment fragment = mCollectionPagerAdapter.getFragmentAtPosition(
                mViewPager.getCurrentItem()/* + nextPage*/);
        if (fragment != null && fragment.getView() != null) {
            EditText editText = (EditText) fragment.getView().findViewById(R.id.edit_text);
            Utility.showSoftKeyboard(editText, this);
        } else {
            Log.d("EditTextError", "Fragment does not contain a EditTetx View or the provided ID is wrong. Keyboard cannot be opened.");
        }
    }

    @Override
    public void setLayoutResizing() {
        Utility.setKeyboardResizing(this);
    }

    @Override
    public void setLayoutOverlapping() {
        Utility.setKeyboardOverlapping(this);
    }

    @Override
    public void changeToolbarIcons(boolean isKeyboardNeeded) {
        if (isKeyboardNeeded && !mIsKeyboardNeededOld) {
            mIsKeyboardNeededOld = true;
            mKeyboardNeeded = true;
            invalidateOptionsMenu();
            // restore normal
        } else if (!isKeyboardNeeded && mIsKeyboardNeededOld) {
            mIsKeyboardNeededOld = false;
            mKeyboardNeeded = false;
            mCameraIconNeeded = false;
            invalidateOptionsMenu();
        }
    }

    /**********************************************************
     * END CustomViewPagerCommunicator IMPLEMENTATION SECTION
     **********************************************************/


    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        setPrimaryFragment(i);
        mListPopupWindow.dismiss();
        mListPopupToggle = false;
        mListPopupReopen = false;
    }

    /*
    *
    * Custom transition animation for ViewPager
    *
    * */
    @Override
    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        final float MIN_SCALE = 0.75f;
        if (position < -1) { // [-Infinity,-1)
            // This page is way off-screen to the left.
            view.setAlpha(0);
        } else if (position <= 0) { // [-1,0]
            // Use the default slide transition when moving to the left page

            view.setAlpha(1);
            view.setTranslationX(0);
            view.setScaleX(1);
            view.setScaleY(1);

        } else if (position <= 1) { // (0,1]

            view.setAlpha(1);
            view.setTranslationX(0);
            view.setScaleX(1);
            view.setScaleY(1);

        } else { // (1,+Infinity]
            // This page is way off-screen to the right.
            view.setAlpha(0);
        }
    }

    /**********************************************************
     * START MIXED IMPLEMENTATION SECTION
     **********************************************************/

    @Override
    public void onPreServerCommunication() {
        onPreServerCommunicationEvent(new PreServerCommunicationEvent());
    }

    @Override
    public void onRecolorUnansweredQuestions() {
       DataHolder.setRecolorNavigationList(true);
    }

    /**********************************************************
     * END MIXED IMPLEMENTATION SECTION
     **********************************************************/

    /**********************************************************
     * START TEXTFRAGMENT_COMMUNICATOR IMPLEMENTATION SECTION
     **********************************************************/

    @Override
    public void fragmentBecamePrimary(String question, String imageName) {
//        mCurrentQuestionText = question;
//        mCurrentImageName = imageName;
    }

    @Override
    public void displayProgressOverlay() {
        showProgressOverlay();
    }

    /**********************************************************
     * END TEXTFRAGMENT_COMMUNICATOR IMPLEMENTATION SECTION
     **********************************************************/


    /**********************************
     * START PRODUCER SECTION
     **********************************/
//    @Produce
//    public PhotoTakenEvent produceLastPhotoTakenEvent(){
//        if(mCurrentIntentImage != null){
//            return new PhotoTakenEvent(mCurrentIntentImage.getPath(), mCurrentQuestionText);
//        }
//        return null;
//    }

    /**********************************
     * END PRODUCER SECTION
     **********************************/

    /**********************************
     * START EVENT LISTENER SECTION
     **********************************/

//    @Subscribe
//    public void onFragmentGotPrimaryEvent(FragmentGotPrimaryEvent event){
//        mCurrentQuestionText = event.getQuestionText();
//        mCurrentImageName = event.getImageName();
//    }

    private void onPreServerCommunicationEvent(PreServerCommunicationEvent event){
        showProgressOverlay();

        CreateUploadImageObservable observable = new CreateUploadImageObservable();
        observable.prepareImageUploadInBackground(this).observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> DataHolder.getCommentaryImageMap().get(pair.first).setmUploadFilePath(pair.second)
                        , Throwable::printStackTrace
                        , this::onStartServerCommunication);
        mEvalPresenter.setUnasweredQuestions();
    }

    private void onStartServerCommunication() {
        mEvalPresenter.requestInternetPermissionAndConnectServer(mPermissionManager);
    }

    @Override
    public void hideProgressOverlay(){
        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);
    }

    @Override
    public void showProgressOverlay(){
        Utility.animateView(mProgressOverlay, View.VISIBLE, 0.8f, 100);
    }

//    public void onStartServerCommunication() {
//        EventBus.get().post(new StartServerCommunicationEvent());
//    }

    @Override
    public void performAnswerRequest() {
        mEvalPresenter.requestInternetPermissionAndConnectServer(mPermissionManager);
    }

    /**********************************
     * END EVENT LISTENER SECTION
     **********************************/


}


