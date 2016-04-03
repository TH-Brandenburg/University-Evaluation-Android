package de.thb.ue.android.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
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
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListPopupWindow;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.octo.android.robospice.exception.NoNetworkException;
//import com.squareup.okhttp.RequestBody;
//import com.squareup.okhttp.MediaType;
//import com.squareup.okhttp.ResponseBody;
import com.squareup.otto.Produce;
import com.squareup.otto.Subscribe;

import org.springframework.http.HttpStatus;

import java.io.File;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.List;

import de.thb.ca.dto.MultipleChoiceQuestionDTO;
import de.thb.ca.dto.ResponseDTO;
import de.thb.ca.dto.util.ChoiceDTO;
import de.thb.ca.dto.util.ErrorType;
import de.thb.ca.dto.util.MultipleChoiceAnswerDTO;
import de.thb.ca.dto.util.TextAnswerDTO;
import de.thb.ca.dto.util.TextQuestionDTO;
import de.thb.ue.android.custom.CustomFragmentStatePagerAdapter;
import de.thb.ue.android.custom.CustomViewPager;
import de.thb.ue.android.custom.CustomWindowPopupAdapter;
import de.thb.ue.android.fragments.MessageFragment;
import de.thb.ue.android.fragments.SendDialogFragment;
import de.thb.ue.android.fragments.SendFragment;
import de.thb.ue.android.fragments.TextFragment;
import de.thb.ue.android.interfaces.PagerAdapterSetPrimary;
import de.thb.ue.android.interfaces.ProgressCommunicator;
import de.thb.ue.android.interfaces.RequestCommunicator;
import de.thb.ue.android.interfaces.RetroRespondService;
import de.thb.ue.android.utility.DataHolder;
import de.thb.ue.android.utility.EventBus;
import de.thb.ue.android.utility.Events.DisplayProgressOverlayEvent;
import de.thb.ue.android.utility.Events.HideProgressOverlayEvent;
import de.thb.ue.android.utility.Events.NetworkFailureEvent;
import de.thb.ue.android.utility.Events.NetworkSuccessEvent;
import de.thb.ue.android.utility.Events.PhotoTakenEvent;
import de.thb.ue.android.utility.Events.PreServerCommunicationEvent;
import de.thb.ue.android.utility.Events.StartServerCommunicationEvent;
import de.thb.ue.android.utility.ImagePathsVO;
import de.thb.ue.android.utility.Observer.CreateUploadImageObservable;
import de.thb.ue.android.utility.Utility;
import fhb.de.campusappevaluationexp.R;
//import retrofit.Call;
import retrofit.Callback;
//import retrofit.JacksonConverterFactory;
//import retrofit.Response;
//import retrofit.Retrofit;
import retrofit.RestAdapter;
import retrofit.RetrofitError;
import retrofit.client.Response;
import retrofit.converter.ConversionException;
import retrofit.converter.JacksonConverter;
import retrofit.mime.TypedFile;
import retrofit.mime.TypedString;
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
    private RestAdapter mRetrofitRestAdapter;
    private JacksonConverter mJacksonConverter;


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

        ArrayList<String> navigationEntrys = (ArrayList<String>) constructQuestionList();
        navigationEntrys.add(getResources().getString(R.string.send_button));

        mListAdapter = new CustomWindowPopupAdapter(this, R.layout.array_adapter, navigationEntrys);
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
            File picture = Utility.createImageFile(mCurrentImageName, this);
            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(picture)); // set the image file name
            mPictureList.add(Uri.fromFile(picture));
            mCurrentImageFile = picture;
            // start the image capture Intent
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) { // ensure that there is an itent that supports the request
                startActivityForResult(takePictureIntent, REQUEST_CAPTURE_IMAGE);
            }
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CAPTURE_IMAGE && resultCode == RESULT_OK) {
            TextFragment fragment = ((TextFragment) mCollectionPagerAdapter.getFragmentAtPosition(mViewPager.getCurrentItem()));
            fragment.onPhotoTaken(mCurrentQuestionText, mCurrentImageFile.getAbsolutePath());
//            EventBus.getEventBus().post(new PhotoTakenEvent(mCurrentImageFile.getPath(), mCurrentQuestionText));
//            ((TextFragment) mCollectionPagerAdapter.getFragmentAtPosition(mViewPager.getCurrentItem())).startLoadImageObserver(mCurrentImageFile);
//            View rootView = mCollectionPagerAdapter.getFragmentAtPosition(mViewPager.getCurrentItem()).getView();
//            View progressBar = rootView.findViewById(R.id.image_progress_bar);
//            View imageView = rootView.findViewById(R.id.comment_thumbnail);
            // hide old image
//            Utility.animateView(imageView, View.INVISIBLE, 0, 200);
//            progressBar.setVisibility(View.VISIBLE);
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
                TextAnswerDTO textAnswerDTO =  DataHolder.isTextQuestionAnswered(textFragment.getmQuestion());
                ImagePathsVO pathObj = DataHolder.getCommentaryImageMap().get(textFragment.getmQuestion());
                if((textAnswerDTO == null || textAnswerDTO.getAnswerText().equals("")) && pathObj == null){
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
                TextQuestionDTO dto = DataHolder.retrieveTextQuestionDTO(textFragment.getmQuestion());
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

    /**
     * Prepares the appeareance of the navigation list.
     * @return
     */
    private List<String> constructQuestionList() {
        ArrayList<String> questions = new ArrayList<>(22);
        int counter = 0;

        if(DataHolder.getQuestionsDTO().getTextQuestionsFirst()){
            // Add text questions first to navigation list mcQuestions second
            for (TextQuestionDTO question : DataHolder.getQuestionTexts()) {
                questions.add((counter + 1) + ". " + question.getQuestionText());
                counter++;
            }
            for (MultipleChoiceQuestionDTO questionDTO : DataHolder.getMCQuestionTexts()) {
                questions.add((counter + 1) + ". " + questionDTO.getQuestion());
                counter++;
            }
        } else {
            // add mc questions first to navigation list text questions second
            for (MultipleChoiceQuestionDTO questionDTO : DataHolder.getMCQuestionTexts()) {
                questions.add((counter + 1) + ". " + questionDTO.getQuestion());
                counter++;
            }
            for (TextQuestionDTO question : DataHolder.getQuestionTexts()) {
                questions.add((counter + 1) + ". " + question.getQuestionText());
                counter++;
            }
        }
        return questions;
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

//        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
//        if(!(activeNetwork != null && activeNetwork.isConnectedOrConnecting())){
//            MessageFragment fragment = MessageFragment.newInstance(getResources().getString(R.string.no_network_title), getResources().getString(R.string.no_network_message), false, MessageFragment.Option.RetryCommunication);
//            fragment.show(getSupportFragmentManager(), "NO_INTERNET");
//            return;
//        }


        if(mRetrofitRestAdapter == null){
            if(mJacksonConverter == null){
                mJacksonConverter = new JacksonConverter();
            }
            mRetrofitRestAdapter = new RestAdapter.Builder()
                    .setEndpoint(DataHolder.getHostName())
                    .setConverter(mJacksonConverter)
                    .build();
        }

        Utility.animateView(mProgressOverlay, View.VISIBLE, 0.8f, 100);

        //zip commentary pictures if there are any
        ArrayList<File> imageFileList = new ArrayList<>();
        for(ImagePathsVO pathObj : DataHolder.getCommentaryImageMap().values()){
            if(pathObj.getmUploadFilePath() != null){
                imageFileList.add(new File(pathObj.getmUploadFilePath()));
            }
        }
        File zippedImages = Utility.zipFiles(this, imageFileList);

//        manuel mapping to Json since I do not trust retrofit to do that
        ObjectMapper mapper = new ObjectMapper();
        String jsonAnswers = null;

        try {
            jsonAnswers = mapper.writeValueAsString(DataHolder.getAnswersDTO());
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        TypedFile typedFile = new TypedFile("multipart/form-data", zippedImages);
        TypedString typedAnswersDto = new TypedString(jsonAnswers);

        RetroRespondService respondService = mRetrofitRestAdapter.create(RetroRespondService.class);
        respondService.sendAnswersWithPictures(typedFile, typedAnswersDto, new AsyncAnswerResponse());
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

    /**********************************************************
     * START BUTTONFRAGMENT_COMMUNICATOR IMPLEMENTATION SECTION
     **********************************************************/

    /**********************************************************
    * END BUTTONFRAGMENT_COMMUNICATOR IMPLEMENTATION SECTION
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

    public void onPreServerCommunicationEvent(PreServerCommunicationEvent event){
        CreateUploadImageObservable observable = new CreateUploadImageObservable();
        observable.prepareImageUploadInBackground(this).observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> DataHolder.getCommentaryImageMap().get(pair.first).setmUploadFilePath(pair.second)
                        , e -> e.printStackTrace()
                        , () -> onStartServerCommunication(new StartServerCommunicationEvent()));
        setUnasweredQuestions();
    }

    public void onStartServerCommunication(StartServerCommunicationEvent event) {
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
        Resources resources = getResources();
        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);

        if (event.getRequestedObject() == null) {
            try {
                ResponseDTO dto = (ResponseDTO) mJacksonConverter.fromBody(event.getResponse().getBody(), ResponseDTO.class);
                ErrorResponseHandling(dto, event.getResponse().getStatus());

            } catch (ConversionException e) {
                e.printStackTrace();
            }
        } else {
            MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.answers_transmission_success_title), resources.getString(R.string.answers_transmission_success_message), false, MessageFragment.Option.CloseApp);
            fragment.show(getSupportFragmentManager(), "Success");
        }
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNetworkFailure(NetworkFailureEvent event){
        Resources resources = getResources();
        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);
        boolean dialogsCanceable = true;
        int statusCode = 0;
        ResponseDTO dto = null;

        if(event.getRetrofitError().getResponse() != null){
            statusCode = event.getRetrofitError().getResponse().getStatus();

            if(event.getRetrofitError().getResponse().getBody() != null){
                try {
                    dto = (ResponseDTO)mJacksonConverter.fromBody(event.getRetrofitError().getResponse().getBody(), ResponseDTO.class);
                } catch (ConversionException e) {
                    e.printStackTrace();
                }
            }
        }

        if(dto != null){
            ErrorResponseHandling(dto, event.getRetrofitError().getResponse().getStatus());
        } else if (statusCode >= 300) {
            // Check of status codes and displaying information to user
            if (statusCode == HttpStatus.BAD_GATEWAY.value() || statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.error_500_502_title), resources.getString(R.string.no_network_message), dialogsCanceable, MessageFragment.Option.None);
                fragment.show(getSupportFragmentManager(), "500|502");
            } else if (statusCode == HttpStatus.SERVICE_UNAVAILABLE.value()) {
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.error_503_title), resources.getString(R.string.error_503_message), dialogsCanceable, MessageFragment.Option.None);
                fragment.show(getSupportFragmentManager(), "503");
            } else if (statusCode == HttpStatus.FORBIDDEN.value() || statusCode == HttpStatus.NOT_FOUND.value()) {
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.error_404_403_title), resources.getString(R.string.error_404_403_message), dialogsCanceable, MessageFragment.Option.None);
                fragment.show(getSupportFragmentManager(), "404|403");
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.error_404_403_title), resources.getString(R.string.error_404_403_message), dialogsCanceable, MessageFragment.Option.None);
                fragment.show(getSupportFragmentManager(), "400");
            }
        } else if(event.getRetrofitError().getCause() != null){
            event.getRetrofitError().printStackTrace();
            if (event.getRetrofitError().getCause().getClass() == NoNetworkException.class || event.getRetrofitError().getCause().getClass() == ConnectException.class) {
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.no_network_title), resources.getString(R.string.no_network_message), false, MessageFragment.Option.RetryCommunication);
                fragment.show(getSupportFragmentManager(), "NoInternet");
            /*} else if(event.getRetrofitError().getCause().getClass() == ConnectException.class){
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.connect_exception_title), resources.getString(R.string.connect_exception_message), false, MessageFragment.Option.CloseApp);
                fragment.show(getSupportFragmentManager(), "NetworkChanged");*/
            } else if (event.getRetrofitError().getCause().getClass() == SocketTimeoutException.class || event.getRetrofitError().getCause().getClass() == SocketException.class) {
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.socket_timeout_title), resources.getString(R.string.socket_timeout_message), true, MessageFragment.Option.RetryCommunication);
                fragment.show(getSupportFragmentManager(), "ServerNotResponding");
            } else {
                MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.some_network_error_title), resources.getString(R.string.some_network_error_message), true, MessageFragment.Option.RetryCommunication);
                fragment.show(getSupportFragmentManager(), "SomeError");
            }
        }
    }

    /**
     * Gives every question a representation in AnswersDTO which never got one assigned by the user.
     */
    private void setUnasweredQuestions(){
        //server expects all questions to have an entry in answers dto. Add all the user did not set
        for(TextAnswerDTO answerDTO : DataHolder.getAnswersDTO().getTextAnswers()){
            if(DataHolder.isTextQuestionAnswered(answerDTO.getQuestionText()) == null){
                DataHolder.getAnswersDTO().getTextAnswers().add(new TextAnswerDTO(answerDTO.getQuestionID(), answerDTO.getQuestionText(), ""));
            }
        }

        //loop through all mcQuestions
        for(MultipleChoiceQuestionDTO questionDTO : DataHolder.getQuestionsDTO().getMultipleChoiceQuestionDTOs()){
            //test if any wasnt answered by the user
            if(DataHolder.isMcQuestionAnswered(questionDTO.getQuestion()) == null){
                ChoiceDTO noCommentChoice = DataHolder.retrieveChoiceByGrade(questionDTO.getQuestion(), 0);
                DataHolder.getAnswersDTO().getMcAnswers().add(new MultipleChoiceAnswerDTO(questionDTO.getQuestion(), noCommentChoice));
            }
        }
    }

    private void ErrorResponseHandling(ResponseDTO dto, int statusCode) {
        Resources resources = getResources();
//        ResponseDTO dto = null;
//        try {
//            if (responseDTO != null) {
//                ObjectMapper mapper = new ObjectMapper();
//                dto = mapper.readValue(responseDTO.bytes(), ResponseDTO.class);
//
//            }
//        } catch (IOException e1) {
//            e1.printStackTrace();
//        }
        if (dto != null && dto.getType() == ErrorType.INVALID_TOKEN) {
            MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.wrong_qr_code_error_title), resources.getString(R.string.wrong_qr_code_error_message), false, MessageFragment.Option.GoToScan);
            fragment.show(getSupportFragmentManager(), "InvalidToken");
        } else if (dto != null && dto.getType() == ErrorType.TOKEN_ALLREADY_USED) {
            MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.token_already_used_title), resources.getString(R.string.token_already_used_message), false, MessageFragment.Option.GoToScan);
            fragment.show(getSupportFragmentManager(), "TokenAlreadyUsed");
        } else if (dto != null && dto.getType() == ErrorType.EVALUATION_CLOSED) {
            MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.evaluation_closed_title), resources.getString(R.string.evaluation_closed_message), false, MessageFragment.Option.GoToScan);
            fragment.show(getSupportFragmentManager(), "EvaluationClosed");
        } else if (dto != null && dto.getType() == ErrorType.UNKNOWN_ERROR) {
            MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.unknown_error_title), resources.getString(R.string.unknown_error_message), true, MessageFragment.Option.None);
            fragment.show(getSupportFragmentManager(), "UnknownError");
        } else if (dto != null && dto.getType() == ErrorType.MALFORMED_REQUEST) {
            MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.malformed_request_title), resources.getString(R.string.malformed_request_message), true, MessageFragment.Option.None);
            fragment.show(getSupportFragmentManager(), "UnknownError");
        } else if (dto != null && dto.getType() == ErrorType.WRONG_DEVICE_ID) {
            MessageFragment fragment = MessageFragment.newInstance(resources.getString(R.string.device_id_title), resources.getString(R.string.device_id_message), true, MessageFragment.Option.None);
            fragment.show(getSupportFragmentManager(), "UnknownError");
        }
    }

    @Override
    public void onRestartQRScanning() {

    }

    @Override
    public void onStartServerCommunication() {
        EventBus.getEventBus().post(new StartServerCommunicationEvent());
    }

    /**********************************
     * END EVENT LISTENER SECTION
     **********************************/

    private class AsyncAnswerResponse implements Callback<ResponseDTO> {
        @Override
        public void success(ResponseDTO responseDTO, Response response) {
            EventBus.getEventBus().post(new NetworkSuccessEvent<ResponseDTO>(responseDTO, response));
        }

        @Override
        public void failure(RetrofitError error) {
            EventBus.getEventBus().post(new NetworkFailureEvent(error));
        }

//        @Override
//        public void onResponse(Response<ResponseDTO> response, Retrofit retrofit) {
//            EventBus.getEventBus().post(new NetworkSuccessEvent<ResponseDTO>(response, retrofit));
//        }
//
//        @Override
//        public void onFailure(Throwable e) {
//            EventBus.getEventBus().post(new NetworkFailureEvent(e));
//        }
    }
}


