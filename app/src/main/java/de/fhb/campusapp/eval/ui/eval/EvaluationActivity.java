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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.buchandersenn.android_permission_manager.PermissionManager;
import com.github.buchandersenn.android_permission_manager.PermissionRequest;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.fhb.ca.dto.AnswersDTO;
import de.fhb.ca.dto.ResponseDTO;
import de.fhb.campusapp.eval.custom.CustomFragmentStatePagerAdapter;
import de.fhb.campusapp.eval.custom.CustomScroller;
import de.fhb.campusapp.eval.custom.CustomViewPager;
import de.fhb.campusapp.eval.custom.CustomWindowPopupAdapter;
import de.fhb.campusapp.eval.data.local.RetrofitHelper;
import de.fhb.campusapp.eval.interfaces.PagerAdapterSetPrimary;
import de.fhb.campusapp.eval.interfaces.ProgressCommunicator;
import de.fhb.campusapp.eval.interfaces.RequestCommunicator;
import de.fhb.campusapp.eval.interfaces.RetroRespondService;
import de.fhb.campusapp.eval.ui.EvaluationApplication;
import de.fhb.campusapp.eval.ui.base.BaseActivity;
import de.fhb.campusapp.eval.ui.scan.ScanPresenter;
import de.fhb.campusapp.eval.ui.sendfragment.SendFragment;
import de.fhb.campusapp.eval.ui.textfragment.TextFragment;
import de.fhb.campusapp.eval.utility.ActivityUtil;
import de.fhb.campusapp.eval.utility.ClassMapper;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.DialogFactory;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.DisplayProgressOverlayEvent;
import de.fhb.campusapp.eval.utility.Events.HideProgressOverlayEvent;
import de.fhb.campusapp.eval.utility.Events.NetworkErrorEvent;
import de.fhb.campusapp.eval.utility.Events.PhotoTakenEvent;
import de.fhb.campusapp.eval.utility.Events.PreServerCommunicationEvent;
import de.fhb.campusapp.eval.utility.Events.RequestErrorEvent;
import de.fhb.campusapp.eval.utility.Events.RequestSuccessEvent;
import de.fhb.campusapp.eval.utility.Events.StartServerCommunicationEvent;
import de.fhb.campusapp.eval.utility.FeatureSwitch;
import de.fhb.campusapp.eval.utility.Observer.CreateUploadImageObservable;
import de.fhb.campusapp.eval.utility.Utility;
import de.fhb.campusapp.eval.utility.vos.ChoiceVO;
import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceAnswerVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceQuestionVO;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;
import de.fhb.campusapp.eval.utility.vos.TextQuestionVO;
import fhb.de.campusappevaluationexp.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
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
    private Retrofit mRetrofitRest;

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

//        if (id == R.id.camera_activation) {
//            mEvalPresenter.requestCameraPermission(mPermissionManager);
//            mEvalPresenter.requestStoragePermission(mPermissionManager);
//
//            mCurrentIntentImage = mEvalPresenter.startCameraIntent(mCurrentImageName);

//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void setIntentImage(File intentImage) {
        mCurrentIntentImage = intentImage;
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
////            ImageManager manager = new ImageManager();
////            manager.testForPossibility(getContentResolver(), mCurrentIntentImage);
//
//            TextFragment fragment = ((TextFragment) mCollectionPagerAdapter.getFragmentAtPosition(mViewPager.getCurrentItem()));
//            fragment.onPhotoTaken(DataHolder.getCurrentQuestion(), mCurrentIntentImage.getAbsolutePath());
//        }
//    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mPermissionManager.handlePermissionResult(requestCode, grantResults);
    }

    @Override
    public void showCameraExplanation(PermissionRequest request) {
        Dialog dialog  = DialogFactory.createSimpleOkErrorDialog(this
                , R.string.camera_permission_explanation_title
                , R.string.camera_permission_explanation_message
                , (dialogInterface, i) -> request.acceptPermissionRationale()
                , null
                , true);
        dialog.show();
    }

    @Override
    public void showStorageExplanation(PermissionRequest request) {
        Dialog dialog  = DialogFactory.createSimpleOkErrorDialog(this
                , R.string.storage_permission_explanation_title
                , R.string.storage_permission_explanation_message
                , (dialogInterface, i) -> request.acceptPermissionRationale()
                , null
                , true);
        dialog.show();
    }

    @Override
    public void showInternetExplanation(PermissionRequest request) {
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
                    , null, null, true).show();
    }

    @Override
    public void callSaveFinish() {
        ActivityUtil.saveFinish(this);
    }

    @Override
    public File zipPictureFiles(List<File> imageFileList) {
        return Utility.zipFiles(this,(ArrayList<File>) imageFileList);
    }


    //    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
//
//            // array containing properties we want to know about the images on this system.
//            String[] projection = { MediaStore.Images.ImageColumns.SIZE,
//                                    MediaStore.Images.ImageColumns.DISPLAY_NAME,// the path to the image including name
//                                    MediaStore.Images.ImageColumns.DATA,
//                                    MediaStore.Images.ImageColumns._ID};
//
//            Cursor cursor = null;
//            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//
//            // 3 possibilities:
//            // - Some devices use it completely and skip the gallery.
//            // - Some devices ignore it completely and ONLY use the gallery.
//            // - Some devices really suck and save a full sized image to the gallery,
//            //   and save a thumbnail only to the location specified.
//            // All must be addressed and dealt with.
//            // Poss 1: Best case -> we dont need to do anything
//            // Poss 2: Copy the Gallery pick to the intended location.
//            // Poss 3: Delete the thumbnail and copy the full sized picture to specified location.
//
//            // test for poss 2
//            if(mCurrentIntentImage.exists() && mCurrentIntentImage.length() > 0){
//                // test passed -> the intent stored SOMETHING into the intended file
//
//                // test for poss 3 -> search for image in gallery
//                if (uri != null)
//                {
//                    cursor = getContentResolver().query(uri, projection, null, null, null);
//                }
//
//                if(cursor != null && cursor.moveToFirst()) {
//                    cursor = findNewImageInDatabase(cursor);
//                    if(pair.second != null && !pair.second.isEmpty()) {
//
//                    }
//                    // if this is false -> we found the new Image!
//                    File file = new File(cursor.getString(2));
//                    // if our image is smaller than the gallery one ours is only a thumbnail
//                    // delete and copy
//                    if (file.exists() && mCurrentIntentImage.length() < cursor.getLong(0) && file.delete()) {
//                        try{
//                            mCurrentIntentImage.createNewFile();
//                            FileChannel source = null;
//                            FileChannel destination = null;
//                            try {
//                                source = new FileInputStream(file).getChannel();
//                                destination = new FileOutputStream(mCurrentIntentImage).getChannel();
//                                destination.transferFrom(source, 0, source.size());
//                            } finally {
//                                if (source != null) {
//                                    source.close();
//                                }
//                                if (destination != null) {
//                                    destination.close();
//                                }
//                            }
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                        getContentResolver().delete(
//                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + cursor.getString(3), null);
//                        break;
//                    }
//                }
//
//
//                }
//            } else {
//                // the intent stored nothing inside the file
//            }
//
//
//
//            if(mCurrentIntentImage != null){
//                if (uri != null && mCurrentIntentImage.length() > 0)
//                {
//                    cursor = getContentResolver().query(uri, projection, null, null, null);
//                }
//
//                if(cursor != null && cursor.moveToFirst()){
//                    do{
//                        boolean imageFound = false;
//                        if(DataHolder.getGalleryList().contains(cursor.getString(1))){
//                            imageFound = true;
//                        }
//
//                        if(!imageFound){
//                            // if this is false -> we found the new Image!
//                            File file = new File(cursor.getString(2));
//
//                            // Delete it and remove its entry from the MediaStore Database.
//                            // Congrats. You have removed the offending duplicate.
//                            if(file.exists() && file.delete()){
//                                getContentResolver().delete(
//                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + cursor.getString(3), null);
//                                break;
//                            } else {
//                                Log.e("FILE_DELETE_FAILED", "Image could not be deleted!");
//                                throw new IllegalStateException("Could not delete file. Either it does not exist or its locked.");
//                            }
//                        }
//                    } while (cursor.moveToNext());
//                }
//                if(cursor != null){
//                    cursor.close();
//                }
//            }
//            TextFragment fragment = ((TextFragment) mCollectionPagerAdapter.getFragmentAtPosition(mViewPager.getCurrentItem()));
//            fragment.onPhotoTaken(mCurrentQuestionText, mCurrentIntentImage.getAbsolutePath());
//        }
//    }
//
//    private Cursor findNewImageInDatabase(Cursor cursor){
//        do {
//            if (!DataHolder.getGalleryList().contains(cursor.getString(1))) {
//                pathToImage = Pair.create(cursor,cursor.getString(1));
//            }
//        }while (cursor.moveToNext());
//
//        return pathToImage;
//    }

//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        // based on the result we either set the preview or show a quick toast splash.
//        if (resultCode != RESULT_OK) {
//            return;
//        }
//
//        // This is ##### ridiculous.  Some versions of Android save
//        // to the MediaStore as well.  Not sure why!  We don't know what
//        // name Android will give either, so we get to search for this
//        // manually and remove it.
//        String[] projection = {MediaStore.Images.ImageColumns.SIZE,
//                MediaStore.Images.ImageColumns.DISPLAY_NAME,
//                MediaStore.Images.ImageColumns.DATA,
//                BaseColumns._ID,};
//        //
//        // intialize the Uri and the Cursor, and the current expected size.
//        Cursor cursor = null;
//        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
//        //
//        if (mCurrentIntentImage != null) {
//            // Query the Uri to get the data path.  Only if the Uri is valid,
//            // and we had a valid size to be searching for.
//            if ((uri != null) && (mCurrentIntentImage.length() > 0)) {
//                cursor = getContentResolver().query(uri, projection, null, null, null);
//            }
//            //
//            // If we found the cursor and found a record in it (we also have the size).
//            if ((cursor != null) && (cursor.moveToFirst())) {
//                do {
//                    // Check each area in the gallary we built before.
//                    boolean imageFound = false;
//                    if (DataHolder.getGalleryList().contains(cursor.getString(1))) {
//                        imageFound = true;
//                    }
//                    //
//                    // To here we looped the full gallery.
//                    if (!imageFound) {
//                        // This is the NEW image.  If the size is bigger, copy it.
//                        // Then delete it!
//                        File file = new File(cursor.getString(2));
//
//                        // Ensure it's there, check size, and delete!
//                        if ((file.exists()) && (mCurrentIntentImage.length() < cursor.getLong(0)) && (mCurrentIntentImage.delete())) {
//                            // Finally we can stop the copy.
//                            try {
//                                mCurrentIntentImage.createNewFile();
//                                FileChannel source = null;
//                                FileChannel destination = null;
//                                try {
//                                    source = new FileInputStream(file).getChannel();
//                                    destination = new FileOutputStream(mCurrentIntentImage).getChannel();
//                                    destination.transferFrom(source, 0, source.size());
//                                } finally {
//                                    if (source != null) {
//                                        source.close();
//                                    }
//                                    if (destination != null) {
//                                        destination.close();
//                                    }
//                                }
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                            }
//                        }
//                        //
//                        ContentResolver cr = getContentResolver();
//                        cr.delete(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
//                                BaseColumns._ID + "=" + cursor.getString(3), null);
//                        break;
//                    }
//                }
//                while (cursor.moveToNext());
//                cursor.close();
//
//            }
//            TextFragment fragment = ((TextFragment) mCollectionPagerAdapter.getFragmentAtPosition(mViewPager.getCurrentItem()));
//            fragment.onPhotoTaken(mCurrentQuestionText, mCurrentIntentImage.getAbsolutePath());
//        }
//    }



    @Override
    protected void onPause() {
        super.onPause();
        if (mListPopupWindow.isShowing()) {
            mListPopupWindow.dismiss();
            mListPopupReopen = true;
        }
        mRestoreToolbar = true;
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

    /*

             */
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
//            else if (fragment == null) {
//                Log.e("IsKeyboardNeededError", "Fragment was null");
//            }
        }
//        else {
//            Log.e("IsKeyboardNeededError", "Adapter is: " + mCollectionPagerAdapter + " and Pager is: " + mViewPager);
//        }
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
            } else if (fragment == null) {
                Log.e("IsKeyboardNeededError", "Fragment was null");
            }
        } else {
            Log.e("IsKeyboardNeededError", "Adapter is: " + mCollectionPagerAdapter + " and Pager is: " + mViewPager);
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

//    @Override
//    public boolean onKeyDown(int keyCode, KeyEvent event) {
//        if (keyCode == KeyEvent.FLAG_EDITOR_ACTION || keyCode == KeyEvent.KEYCODE_ENTER) {
//            setPrimaryFragment(mViewPager.getCurrentItem() + 1);
//        }
//        return super.onKeyDown(keyCode, event);
//    }

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

//  /*
//  * Executes request retrieving questions and choices from REST server
//  * */
//    @Override
//    public void performAnswerRequest() {
//
//        if(FeatureSwitch.DEBUG_ACTIVE && DataHolder.getHostName() == null){
//            Utility.animateView(mProgressOverlay, View.GONE, 0.8f, 100);
//
//            DialogFactory.createSimpleOkErrorDialog(this,
//                    "Abort Debug", "Host not set and debug mode active. Aborting"
//                    , null, null, true).show();
//            return;
//        }
//
//        if(mRetrofitRest == null){
//            mRetrofitRest = new Retrofit.Builder()
//                    .baseUrl(DataHolder.getHostName() + '/')
//                    .addConverterFactory(JacksonConverterFactory.create())
//                    .build();
//        }
//
//        //zip commentary pictures if there are any
//        ArrayList<File> imageFileList = new ArrayList<>();
//        for(ImageDataVO pathObj : DataHolder.getCommentaryImageMap().values()){
//            if(pathObj.getmUploadFilePath() != null){
//                imageFileList.add(new File(pathObj.getmUploadFilePath()));
//            }
//        }
//        File zippedImages = Utility.zipFiles(this, imageFileList);
//
////      manuel mapping to Json since I do not trust retrofit to do that
//        ObjectMapper mapper = new ObjectMapper();
//        String jsonAnswers = null;
//
//        try {
//            AnswersDTO dto = ClassMapper.answersVOToAnswerDTOMapper(DataHolder.getAnswersVO());
//            jsonAnswers = mapper.writeValueAsString(dto);
//        } catch (JsonProcessingException e) {
//            e.printStackTrace();
//        }
//
//        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), zippedImages);
//        RequestBody answerBody = RequestBody.create(MediaType.parse("multipart/form-data"), jsonAnswers);
//
//        MultipartBody.Part filePart = MultipartBody.Part.createFormData("images", zippedImages.getName(), fileBody);
//
//        RetroRespondService respondService = mRetrofitRest.create(RetroRespondService.class);
//        Call<ResponseDTO> response = respondService.sendAnswersWithPictures(answerBody, filePart);
//
//        response.enqueue(new RetrofitCallback());
//    }

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
    @Produce
    public PhotoTakenEvent produceLastPhotoTakenEvent(){
        if(mCurrentIntentImage != null){
            return new PhotoTakenEvent(mCurrentIntentImage.getPath(), mCurrentQuestionText);
        }
        return null;
    }

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
        Utility.animateView(mProgressOverlay, View.VISIBLE, 0.8f, 100);

        CreateUploadImageObservable observable = new CreateUploadImageObservable();
        observable.prepareImageUploadInBackground(this).observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> DataHolder.getCommentaryImageMap().get(pair.first).setmUploadFilePath(pair.second)
                        , e -> e.printStackTrace()
                        , () -> onStartServerCommunication(new StartServerCommunicationEvent()));
        setUnasweredQuestions();
    }

    private void onStartServerCommunication(StartServerCommunicationEvent event) {
        mEvalPresenter.requestInternetPermissionAndConnectServer(mPermissionManager);
    }

    @Override
    public void hideProgressOverlay(){
        Utility.animateView(mProgressOverlay, View.VISIBLE, 0.8f, 100);    }

    @Override
    public void showProgressOverlay(){
        Utility.animateView(mProgressOverlay, View.GONE, 0.8f, 100);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onRequestSuccess(RequestSuccessEvent event){
        // delete obsolete data
//        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//
//        Utility.fileDelete(picDir, 1500, 0);
//        Utility.fileDelete(dcimDir, 1500, 0);
        Dialog dialog  = DialogFactory.createSimpleOkErrorDialog(this
                , R.string.answers_transmission_success_title
                , R.string.answers_transmission_success_message
                , (dialogInterface, i) -> ActivityUtil.saveFinish(this)
                , dialogInterface -> ActivityUtil.saveFinish(this)
                , true);
        dialog.show();

//        Resources resources = getResources();
//        MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.answers_transmission_success_title), resources.getString(R.string.answers_transmission_success_message), false, MessageFragment.Option.CloseApp);
//        fragment.show(getSupportFragmentManager(), "Success");
        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNetworkError(NetworkErrorEvent event){
        Resources resources = getResources();
        Throwable t = event.getRetrofitError();
        boolean dialogsCanceable = true;
        int statusCode = 0;
        ResponseDTO dto = null;

        Pair<String, String> errorText = mRetrofitHelper.processNetworkError(t, mResources);
        Dialog dialog  = DialogFactory.createSimpleOkErrorDialog(this
                , errorText.first
                , errorText.second
                , (dialogInterface, i) -> onStartServerCommunication()
                , dialogInterface -> onStartServerCommunication()
                , true);
        dialog.show();

//      if(event.getClass() != null){
//            t.printStackTrace();
//            if (t.getClass() == ConnectException.class) {
//                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.no_network_title), resources.getString(R.string.no_network_message), false, MessageFragment.Option.RetryCommunication);
//                fragment.show(getSupportFragmentManager(), "NoInternet");
//            } else if (t.getClass() == SocketTimeoutException.class || t.getClass() == SocketException.class) {
//                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.socket_timeout_title), resources.getString(R.string.socket_timeout_message), true, MessageFragment.Option.RetryCommunication);
//                fragment.show(getSupportFragmentManager(), "ServerNotResponding");
//            } else {
//                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.some_network_error_title), resources.getString(R.string.some_network_error_message), true, MessageFragment.Option.RetryCommunication);
//                fragment.show(getSupportFragmentManager(), "SomeError");
//            }
//        }
        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNetworkError(RequestErrorEvent<ResponseDTO> event){
//        ErrorResponseHandling(event.getResposne());
        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);
    }

    /**
     * Gives every question a representation in AnswersDTO which never got one assigned by the user.
     */
    private void setUnasweredQuestions(){
        //server expects all questions to have an entry in answers dto. Add all the user did not set
        for(TextAnswerVO answerVO : DataHolder.getAnswersVO().getTextAnswers()){
            if(DataHolder.isTextQuestionAnswered(answerVO.getQuestionText()) == null){
                DataHolder.getAnswersVO().getTextAnswers().add(new TextAnswerVO(answerVO.getQuestionID(), answerVO.getQuestionText(), ""));
            }
        }

        //loop through all mcQuestions
        for(MultipleChoiceQuestionVO questionVO : DataHolder.getQuestionsVO().getMultipleChoiceQuestionVOs()){
            //test if any wasnt answered by the user
            if(DataHolder.isMcQuestionAnswered(questionVO.getQuestion()) == null){
                ChoiceVO noCommentChoice = DataHolder.retrieveChoiceByGrade(questionVO.getQuestion(), 0);
                DataHolder.getAnswersVO().getMcAnswers().add(new MultipleChoiceAnswerVO(questionVO.getQuestion(), noCommentChoice));
            }
        }
    }

//    private void ErrorResponseHandling(Response<ResponseDTO> response) {
//        int statusCode = response.code();
//        Resources resources = getResources();
//        try {
//            ResponseBody body = response.errorBody();
//            // anootatoin array needed in order to prevent nullPointer
//            ResponseDTO dto = (ResponseDTO) mRetrofitRest.responseBodyConverter(ResponseDTO.class, new Annotation[1]).convert(body);
//
//            if (dto != null && dto.getType() == ErrorType.INVALID_TOKEN) {
//                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.invalid_token_title), resources.getString(R.string.invalid_token_message), true, MessageFragment.Option.RetryScan);
//                fragment.show(getSupportFragmentManager(), "InvalidToken");
//            } else if (dto != null && dto.getType() == ErrorType.TOKEN_ALLREADY_USED) {
//                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.token_already_used_title), resources.getString(R.string.token_already_used_message), true, MessageFragment.Option.RetryScan);
//                fragment.show(getSupportFragmentManager(), "TokenAlreadyUsed");
//            } else if (dto != null && dto.getType() == ErrorType.EVALUATION_CLOSED) {
//                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.evaluation_closed_title), resources.getString(R.string.evaluation_closed_message), true, MessageFragment.Option.RetryScan);
//                fragment.show(getSupportFragmentManager(), "EvaluationClosed");
//            } else if (dto != null && dto.getType() == ErrorType.UNKNOWN_ERROR) {
//                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.unknown_error_title), resources.getString(R.string.unknown_error_message), true, MessageFragment.Option.RetryScan);
//                fragment.show(getSupportFragmentManager(), "UnknownError");
//            } else if (dto != null && dto.getType() == ErrorType.MALFORMED_REQUEST) {
//                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.unknown_error_title), resources.getString(R.string.unknown_error_message), true, MessageFragment.Option.RetryScan);
//                fragment.show(getSupportFragmentManager(), "MalformedRequest");
//            } else {
//                // Check of status codes and display information to user
//                if (statusCode == HttpURLConnection.HTTP_BAD_GATEWAY || statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
//                    MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.error_500_502_title), resources.getString(R.string.no_network_message), true, MessageFragment.Option.RetryCommunication);
//                    fragment.show(getSupportFragmentManager(), "500|502");
//                } else if (statusCode == HttpURLConnection.HTTP_UNAVAILABLE) {
//                    MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.error_503_title), resources.getString(R.string.error_503_message), true, MessageFragment.Option.RetryCommunication);
//                    fragment.show(getSupportFragmentManager(), "503");
//                } else if (statusCode == HttpURLConnection.HTTP_FORBIDDEN || statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
//                    MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.error_404_403_title), resources.getString(R.string.error_404_403_message), true, MessageFragment.Option.RetryCommunication);
//                    fragment.show(getSupportFragmentManager(), "404|403");
//                } else if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
//                    MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.unknown_error_title), resources.getString(R.string.unknown_error_message), true, MessageFragment.Option.RetryCommunication);
//                    fragment.show(getSupportFragmentManager(), "400");
//                }
//            }
//        } catch (IOException | IllegalArgumentException e) {
//            e.printStackTrace();
//        }
//    }

    public void onStartServerCommunication() {
        EventBus.get().post(new StartServerCommunicationEvent());
    }

    @Override
    public void performAnswerRequest() {
        mEvalPresenter.requestInternetPermissionAndConnectServer(mPermissionManager);
    }

    /**********************************
     * END EVENT LISTENER SECTION
     **********************************/


}


