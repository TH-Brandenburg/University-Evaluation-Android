package de.fhb.campusapp.eval.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListPopupWindow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
//import com.octo.android.robospice.exception.NoNetworkException;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.ResponseBody;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

//import org.springframework.http.HttpStatus;

import org.apache.http.HttpStatus;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import de.fhb.ca.dto.AnswersDTO;
import de.fhb.ca.dto.ResponseDTO;
import de.fhb.ca.dto.util.ErrorType;
import de.fhb.campusapp.eval.custom.CustomFragmentStatePagerAdapter;
import de.fhb.campusapp.eval.custom.CustomScroller;
import de.fhb.campusapp.eval.custom.CustomViewPager;
import de.fhb.campusapp.eval.custom.CustomWindowPopupAdapter;
import de.fhb.campusapp.eval.fragments.MessageFragment;
import de.fhb.campusapp.eval.fragments.SendDialogFragment;
import de.fhb.campusapp.eval.fragments.SendFragment;
import de.fhb.campusapp.eval.fragments.TextFragment;
import de.fhb.campusapp.eval.interfaces.PagerAdapterSetPrimary;
import de.fhb.campusapp.eval.interfaces.ProgressCommunicator;
import de.fhb.campusapp.eval.interfaces.RequestCommunicator;
import de.fhb.campusapp.eval.interfaces.RetroRespondService;
import de.fhb.campusapp.eval.utility.ClassMapper;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.DisplayProgressOverlayEvent;
import de.fhb.campusapp.eval.utility.Events.HideProgressOverlayEvent;
import de.fhb.campusapp.eval.utility.Events.NetworkErrorEvent;
import de.fhb.campusapp.eval.utility.Events.NetworkFailureEvent;
import de.fhb.campusapp.eval.utility.Events.NetworkSuccessEvent;
import de.fhb.campusapp.eval.utility.Events.PhotoTakenEvent;
import de.fhb.campusapp.eval.utility.Events.PreServerCommunicationEvent;
import de.fhb.campusapp.eval.utility.Events.StartServerCommunicationEvent;
import de.fhb.campusapp.eval.utility.FeatureSwitch;
import de.fhb.campusapp.eval.utility.vos.ChoiceVO;
import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import de.fhb.campusapp.eval.utility.Observer.CreateUploadImageObservable;
import de.fhb.campusapp.eval.utility.Utility;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceAnswerVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceQuestionVO;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;
import de.fhb.campusapp.eval.utility.vos.TextQuestionVO;
import fhb.de.campusappevaluationexp.R;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;

@ContentView(R.layout.activity_button)
public class EvaluationActivity extends BaseActivity implements ProgressCommunicator, PagerAdapterSetPrimary, ViewPager.PageTransformer,
        CustomViewPager.CustomViewPagerCommunicator, AdapterView.OnItemClickListener, TextFragment.TextFragmentCommunicator,
        RequestCommunicator, SendFragment.SendFragmentCommunicator, SendDialogFragment.SendDialogFragmentCommunicator, MessageFragment.MessageFragmentCommunicator
{

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

    @InjectView(R.id.button_pager)
    private CustomViewPager mViewPager;

    @InjectView(R.id.progress_overlay)
    private View mProgressOverlay;

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
    private File mCurrentImageFile = null;
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
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.show();
        }

        // fixes the orientation to portrait
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.my_awesome_toolbar);
        setSupportActionBar(toolbar);

        //just in case it became null thanks to android
        DataHolder.setPreferences(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        mCollectionPagerAdapter = new CustomFragmentStatePagerAdapter(getSupportFragmentManager(), this);
        mViewPager.setAdapter(mCollectionPagerAdapter);
        mViewPager.setmCustomViewPagerCommunicator(this);
        mViewPager.setPageTransformer(true, this);

        // Manipulating the animation speed of the view pager is not easy.
        // Reflection is necessary. Either that or alter the class directly within the android support package.
        if(FeatureSwitch.CUSTOM_PAGER_ANIMATION){
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
        if(isCameraSymbolNeeded()){
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
        getMenuInflater().inflate(R.menu.action_bar_normal, menu);
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
//            View view = findViewById(R.id.action_bar_normal);
            View view = findViewById(R.id.my_awesome_toolbar);
            mListPopupWindow.setAnchorView(view);
            mListPopupWindow.show();
            mListPopupToggle = true;
        } else if (id == R.id.question_search) {
            mListPopupWindow.dismiss();
            mListPopupToggle = false;
            mListPopupReopen = false;
        }

        if (id == R.id.camera_activation) {
            // create Intent to take a picture and return control to the calling application
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            mCurrentImageFile = Utility.createImageFile(mCurrentImageName, this);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(mCurrentImageFile));
            mPictureList.add(Uri.fromFile(mCurrentImageFile));

            fillPhotoList();

            // start the image capture Intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) { // ensure that there is an itent that supports the request
                startActivityForResult(takePictureIntent, REQUEST_CAPTURE_IMAGE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private void fillPhotoList(){
        DataHolder.getGalleryList().clear();
        String[] projection = { MediaStore.Images.ImageColumns.DISPLAY_NAME };
        Cursor cursor = null;
        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        if(uri != null){
            cursor = getContentResolver().query(uri, projection, null, null, null);
        }

        if(cursor != null && cursor.moveToFirst()){
            do {
                DataHolder.getGalleryList().add(cursor.getString(0));
            } while (cursor.moveToNext());
            cursor.close();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
//            Instant now = Instant.now();

            String[] projection = { MediaStore.Images.ImageColumns.SIZE,
                                    MediaStore.Images.ImageColumns.DISPLAY_NAME,
                                    MediaStore.Images.ImageColumns.DATA,
                                    MediaStore.Images.ImageColumns._ID};

            Cursor cursor = null;
            Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

            if(mCurrentImageFile != null){
                if (uri != null && mCurrentImageFile.length() > 0)
                {
                    cursor = getContentResolver().query(uri, projection, null, null, null);
                }

                if(cursor != null && cursor.moveToFirst()){
                    do{
                        boolean imageFound = false;
                        if(DataHolder.getGalleryList().contains(cursor.getString(1))){
                            imageFound = true;
                        }

                        if(!imageFound){
                            // if this is false -> we found the new Image!
                            File file = new File(cursor.getString(2));

                            if(file.exists() && mCurrentImageFile.length() < cursor.getLong(0) && file.delete()){
                                getContentResolver().delete(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI, BaseColumns._ID + "=" + cursor.getString(3), null);
                                cursor.close();
                                break;
                            }
                        }
                    } while (cursor.moveToNext());
                }
            }

            TextFragment fragment = ((TextFragment) mCollectionPagerAdapter.getFragmentAtPosition(mViewPager.getCurrentItem()));
            fragment.onPhotoTaken(mCurrentQuestionText, mCurrentImageFile.getAbsolutePath());
//            mCurrentImageFile.setLastModified(now.getMillis());
        }
    }

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
            } else if (fragment == null) {
                Log.e("IsKeyboardNeededError", "Fragment was null");
            }
        } else {
            Log.e("IsKeyboardNeededError", "Adapter is: " + mCollectionPagerAdapter + " and Pager is: " + mViewPager);
        }
        return needed;
    }

    @Override
    public boolean isCameraSymbolNeeded() {
        boolean needed = false;

        if (mCollectionPagerAdapter != null && mViewPager != null) {
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

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.FLAG_EDITOR_ACTION || keyCode == KeyEvent.KEYCODE_ENTER) {
            setPrimaryFragment(mViewPager.getCurrentItem() + 1);
        }
        return super.onKeyDown(keyCode, event);
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

  /*
  * Executes request retrieving questions and choices from REST server
  * */
    @Override
    public void performAnswerRequest() {


        if(mRetrofitRest == null){
            mRetrofitRest = new Retrofit.Builder()
                    .baseUrl(DataHolder.getHostName() + '/')
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }

        //zip commentary pictures if there are any
        ArrayList<File> imageFileList = new ArrayList<>();
        for(ImageDataVO pathObj : DataHolder.getCommentaryImageMap().values()){
            if(pathObj.getmUploadFilePath() != null){
                imageFileList.add(new File(pathObj.getmUploadFilePath()));
            }
        }
        File zippedImages = Utility.zipFiles(this, imageFileList);

//      manuel mapping to Json since I do not trust retrofit to do that
        ObjectMapper mapper = new ObjectMapper();
        String jsonAnswers = null;

        try {
            AnswersDTO dto = ClassMapper.answersVOToAnswerDTOMapper(DataHolder.getAnswersVO());
            jsonAnswers = mapper.writeValueAsString(dto);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        RequestBody fileBody = RequestBody.create(MediaType.parse("multipart/form-data"), zippedImages);
        RequestBody answerBody = RequestBody.create(MediaType.parse("multipart/form-data"), jsonAnswers);

        MultipartBody.Part filePart = MultipartBody.Part.createFormData("images", zippedImages.getName(), fileBody);

        RetroRespondService respondService = mRetrofitRest.create(RetroRespondService.class);
        Call<ResponseDTO> response = respondService.sendAnswersWithPictures(answerBody, filePart);

        response.enqueue(new RetrofitCallback());
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
        mCurrentQuestionText = question;
        mCurrentImageName = imageName;
    }

    @Override
    public void displayProgressOverlay() {
        onDisplayProgressOverlay(new DisplayProgressOverlayEvent());
    }

    /**********************************************************
     * END TEXTFRAGMENT_COMMUNICATOR IMPLEMENTATION SECTION
     **********************************************************/


    /**********************************
     * START PRODUCER SECTION
     **********************************/
    @Produce
    public PhotoTakenEvent produceLastPhotoTakenEvent(){
        if(mCurrentImageFile != null){
            return new PhotoTakenEvent(mCurrentImageFile.getPath(), mCurrentQuestionText);
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
        Utility.animateView(mProgressOverlay, View.VISIBLE, 0.8f, 100);;

        CreateUploadImageObservable observable = new CreateUploadImageObservable();
        observable.prepareImageUploadInBackground(this).observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> DataHolder.getCommentaryImageMap().get(pair.first).setmUploadFilePath(pair.second)
                        , e -> e.printStackTrace()
                        , () -> onStartServerCommunication(new StartServerCommunicationEvent()));
        setUnasweredQuestions();
    }

    private void onStartServerCommunication(StartServerCommunicationEvent event) {
        performAnswerRequest();
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onDisplayProgressOverlay(DisplayProgressOverlayEvent event){
        Utility.animateView(mProgressOverlay, View.VISIBLE, 0.8f, 100);    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onHideProgressOverlay(HideProgressOverlayEvent event){
        Utility.animateView(mProgressOverlay, View.GONE, 0.8f, 100);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNetworkSuccess(NetworkSuccessEvent event){
        // delete obsolete data
//        File picDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
//        File dcimDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
//
//        Utility.fileDelete(picDir, 1500, 0);
//        Utility.fileDelete(dcimDir, 1500, 0);

        Resources resources = getResources();
        MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.answers_transmission_success_title), resources.getString(R.string.answers_transmission_success_message), false, MessageFragment.Option.CloseApp);
        fragment.show(getSupportFragmentManager(), "Success");
        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNetworkFailure(NetworkFailureEvent event){
        Resources resources = getResources();
        Throwable t = event.getRetrofitError();
        boolean dialogsCanceable = true;
        int statusCode = 0;
        ResponseDTO dto = null;

      if(event.getClass() != null){
            t.printStackTrace();
            if (t.getClass() == ConnectException.class) {
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.no_network_title), resources.getString(R.string.no_network_message), false, MessageFragment.Option.RetryCommunication);
                fragment.show(getSupportFragmentManager(), "NoInternet");
            } else if (t.getClass() == SocketTimeoutException.class || t.getClass() == SocketException.class) {
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.socket_timeout_title), resources.getString(R.string.socket_timeout_message), true, MessageFragment.Option.RetryCommunication);
                fragment.show(getSupportFragmentManager(), "ServerNotResponding");
            } else {
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.some_network_error_title), resources.getString(R.string.some_network_error_message), true, MessageFragment.Option.RetryCommunication);
                fragment.show(getSupportFragmentManager(), "SomeError");
            }
        }
        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNetworkError(NetworkErrorEvent<ResponseDTO> event){
        ErrorResponseHandling(event.getResposne());
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

    private void ErrorResponseHandling(Response<ResponseDTO> response) {
        int statusCode = response.code();
        Resources resources = getResources();

        try {
            ResponseBody body = response.errorBody();
            // anootatoin array needed in order to prevent nullPointer
            ResponseDTO dto = (ResponseDTO) mRetrofitRest.responseBodyConverter(ResponseDTO.class, new Annotation[1]).convert(body);

            if (dto != null && dto.getType() == ErrorType.INVALID_TOKEN) {
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.invalid_token_title), resources.getString(R.string.invalid_token_message), true, MessageFragment.Option.RetryScan);
                fragment.show(getSupportFragmentManager(), "InvalidToken");
            } else if (dto != null && dto.getType() == ErrorType.TOKEN_ALLREADY_USED) {
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.token_already_used_title), resources.getString(R.string.token_already_used_message), true, MessageFragment.Option.RetryScan);
                fragment.show(getSupportFragmentManager(), "TokenAlreadyUsed");
            } else if (dto != null && dto.getType() == ErrorType.EVALUATION_CLOSED) {
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.evaluation_closed_title), resources.getString(R.string.evaluation_closed_message), true, MessageFragment.Option.RetryScan);
                fragment.show(getSupportFragmentManager(), "EvaluationClosed");
            } else if (dto != null && dto.getType() == ErrorType.UNKNOWN_ERROR) {
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.unknown_error_title), resources.getString(R.string.unknown_error_message), true, MessageFragment.Option.RetryScan);
                fragment.show(getSupportFragmentManager(), "UnknownError");
            } else if (dto != null && dto.getType() == ErrorType.MALFORMED_REQUEST) {
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.unknown_error_title), resources.getString(R.string.unknown_error_message), true, MessageFragment.Option.RetryScan);
                fragment.show(getSupportFragmentManager(), "MalformedRequest");
            } else {
                // Check of status codes and display information to user
                if (statusCode == HttpStatus.SC_BAD_GATEWAY || statusCode == HttpStatus.SC_INTERNAL_SERVER_ERROR) {
                    MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.error_500_502_title), resources.getString(R.string.no_network_message), true, MessageFragment.Option.RetryCommunication);
                    fragment.show(getSupportFragmentManager(), "500|502");
                } else if (statusCode == HttpStatus.SC_SERVICE_UNAVAILABLE) {
                    MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.error_503_title), resources.getString(R.string.error_503_message), true, MessageFragment.Option.RetryCommunication);
                    fragment.show(getSupportFragmentManager(), "503");
                } else if (statusCode == HttpStatus.SC_FORBIDDEN || statusCode == HttpStatus.SC_NOT_FOUND) {
                    MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.error_404_403_title), resources.getString(R.string.error_404_403_message), true, MessageFragment.Option.RetryCommunication);
                    fragment.show(getSupportFragmentManager(), "404|403");
                } else if (statusCode == HttpStatus.SC_BAD_REQUEST) {
                    MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.unknown_error_title), resources.getString(R.string.unknown_error_message), true, MessageFragment.Option.RetryCommunication);
                    fragment.show(getSupportFragmentManager(), "400");
                }
            }
        } catch (IOException | IllegalArgumentException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRestartQRScanning() {

    }

    @Override
    public void onStartServerCommunication() {
        EventBus.get().post(new StartServerCommunicationEvent());
    }

    private class RetrofitCallback implements Callback<ResponseDTO>{

        @Override
        public void onResponse(Call<ResponseDTO> call, Response<ResponseDTO> response) {
            if(response.isSuccessful()){
                EventBus.get().post(new NetworkSuccessEvent<>(response.body() ,response));
            } else {
                EventBus.get().post(new NetworkErrorEvent<>(response));
            }
        }

        @Override
        public void onFailure(Call<ResponseDTO> call, Throwable t) {
            EventBus.get().post(new NetworkFailureEvent(t));
        }
    }

    /**********************************
     * END EVENT LISTENER SECTION
     **********************************/


}


