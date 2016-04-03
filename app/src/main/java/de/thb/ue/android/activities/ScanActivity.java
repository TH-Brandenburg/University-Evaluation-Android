package de.thb.ue.android.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.abhi.barcode.frag.libv2.BarcodeFragment;
import com.abhi.barcode.frag.libv2.ICameraManagerListener;
import com.abhi.barcode.frag.libv2.IScanResultHandler;
import com.abhi.barcode.frag.libv2.ScanResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.camera.CameraManager;
import com.octo.android.robospice.SpiceManager;
import com.octo.android.robospice.exception.NoNetworkException;
//import com.squareup.okhttp.ResponseBody;
import com.squareup.otto.Subscribe;

import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.ConnectException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.EnumSet;
import java.util.UUID;

import de.thb.ca.dto.QuestionsDTO;
import de.thb.ca.dto.RequestDTO;
import de.thb.ca.dto.ResponseDTO;
import de.thb.ca.dto.util.ErrorType;
import de.thb.ue.android.custom.CustomJsonSpiceService;
import de.thb.ue.android.fragments.MessageFragment;
import de.thb.ue.android.interfaces.RetroRequestService;
import de.thb.ue.android.services.CleanUpService;
import de.thb.ue.android.utility.EventBus;
import de.thb.ue.android.utility.Events.NetworkFailureEvent;
import de.thb.ue.android.utility.Events.NetworkSuccessEvent;
import de.thb.ue.android.utility.Events.RestartQRScanningEvent;
import de.thb.ue.android.utility.Observer.DeleteImagesObservable;
import de.thb.ue.android.utility.QrPojo;
import de.thb.ue.android.utility.DataHolder;
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
import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import rx.android.schedulers.AndroidSchedulers;

@ContentView(R.layout.activity_scan)
public class ScanActivity extends BaseActivity implements IScanResultHandler, ICameraManagerListener, MessageFragment.MessageFragmentCommunicator {

    private static final String TAG = ScanActivity.class.getSimpleName();
    private static final String PACKAGE_NAME = ScanActivity.class.getPackage().getName();
    public final static String ACTIVATE_SCANNING = "ACTIVATE_SCANNING";
    private static final String CLEANUP_SERVICE_STARTED = "CLEANUP_SERVICE_STARTED";
    private static final String REQUEST_RUNNING = "REQUEST_RUNNING";

    // Retrieve the id, hash it and create a QuestionRequest with it.
//    private String DEVICE_ID = "";

    private Resources mResources;
    private BarcodeFragment mBarcodeFragment;
    private CameraManager mCameraManager;
    private Handler mDelayHandler;
    private Runnable mDelayRunnable;
    private ScaleGestureDetector mScaleDetector;
//    private Retrofit mRetrofit;
    private RestAdapter mRetrofitRestAdapter;
    private JacksonConverter mJacksonConverter;
    /*
    * The QrPojo object that was created from the last scanned QR code.
    * */
    private QrPojo mLastPojo;

    protected SpiceManager spiceManager = new SpiceManager(CustomJsonSpiceService.class);
    private int mZoom;
    private boolean mActivateScanning;
    private boolean mIsScanning;
    private boolean mCleanupServiceStarted = false;
    private boolean mRequestRunning = false;

    @InjectView(R.id.progress_overlay)
    private View mProgressOverlay;

    @InjectView(R.id.my_awesome_toolbar)
    private Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // fixes the orientation to portrait
        if(getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE){
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        } else {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        }

        // DataHolder gets ability to freely serialize/deserialize its variables
        DataHolder.setPreferences(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        DeleteImagesObservable observable = new DeleteImagesObservable();
        observable.deleteImageMapInBackground(DataHolder.getCommentaryImageMap()).observeOn(AndroidSchedulers.mainThread()).subscribe();
        // Ensure that all data of previous evaluations is deleted
        DataHolder.deleteAllData();

        Window window = getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        mResources = getResources();

        setSupportActionBar(mToolBar);
        mToolBar.setTitle(mResources.getText(R.string.scan_search));

        if (savedInstanceState != null) {
            mActivateScanning = savedInstanceState.getBoolean(ACTIVATE_SCANNING);
            mCleanupServiceStarted = savedInstanceState.getBoolean(CLEANUP_SERVICE_STARTED);
            mRequestRunning = savedInstanceState.getBoolean(REQUEST_RUNNING);
        }

        //if a request was running when orientation changed, display progress overlay
        if(mRequestRunning){
            Utility.animateView(mProgressOverlay, View.VISIBLE, 0.8f, 100);
        }

        //starts the cleanUpService -> deletes all images when app is closed
        if(!mCleanupServiceStarted){
            Intent intent = new Intent(this, CleanUpService.class);
            startService(intent);
            mCleanupServiceStarted = true;
        }

        if (getIntent().getBooleanExtra(MessageFragment.CLOSE, false)) {
            // delete all data and close application (as best as android lets you)
            DataHolder.deleteAllData();
            finish();
        }

        if (getIntent().getBooleanExtra(MessageFragment.GO_TO_SCAN, false)) {
            DataHolder.deleteAllData();
        }

        //sets the uuid for this session
        DataHolder.setUuid(UUID.randomUUID().toString());

        mScaleDetector = new ScaleGestureDetector(this, new ScaleGestureDetector.OnScaleGestureListener() {
            private float x;
            private final float factor = 0.5f;

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                x = detector.getScaleFactor();
                return true;
            }

            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                if (mIsScanning) {
                    float y = detector.getScaleFactor();
                    float z = (y - x) * factor;
                    // Cut off some jittery digits.
                    z = ((float) ((int) (z * 100.0f))) / 100.0f;
                    zoom(z);
                }

                return false;
            }
        });
        initBarcodeFragment();
        if(mRequestRunning){

        }

        mIsScanning = true;
    }

    /**
     * inititializes the barcode fragment
     */
    private void initBarcodeFragment(){
        if(mBarcodeFragment == null){
            mBarcodeFragment = (BarcodeFragment) getSupportFragmentManager().findFragmentById(R.id.scan_view);
            //mBarcodeFragment.setAlwaysDecodeOnResume(true); // Restore old behaviour.
            mBarcodeFragment.setScanResultHandler(this);
            mBarcodeFragment.setCameraManagerListener(this);
            mBarcodeFragment.setAlwaysDecodeOnResume(false);
            mBarcodeFragment.setDecodeFor(EnumSet.of(BarcodeFormat.QR_CODE));
        }
    }

    @Override
    protected void onResume() {
        // DataHolder gets ability to freely serialize/deserialize its variables
        // Android might clear variable in DataHolder while App is in background leading to shit.
        DataHolder.setPreferences(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        if(!mRequestRunning){
            mToolBar.setTitle(mResources.getText(R.string.scan_search));
        } else {
            mToolBar.setTitle(mResources.getText(R.string.scan_send));
        }
        super.onResume();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        mScaleDetector.onTouchEvent(event);
        return true;
    }

    @Override
    public void scanResult(ScanResult result) {
        mIsScanning = false;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mLastPojo = mapper.readValue(result.getRawResult().getText(), QrPojo.class);
            // store vote token within answerDTO
            if (mLastPojo != null && mLastPojo.getHost() != null && !mLastPojo.getHost().equals("") && mLastPojo.getVoteToken() != null && !mLastPojo.getVoteToken().equals("")) {
                DataHolder.getAnswersDTO().setVoteToken(mLastPojo.getVoteToken());
            } else {
                throw new IOException("QRPojo was not initialized correctly");
            }

            DataHolder.getAnswersDTO().setDeviceID(DataHolder.getUuid());

            // share host name between activities
            DataHolder.setHostName(mLastPojo.getHost());
            performQuestionRequest(/*mLastPojo, spiceManager*/);
            mCameraManager.stopPreview();
        } catch (IOException e) {
            MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.wrong_qr_code_error_title), mResources.getString(R.string.wrong_qr_code_error_message), true, MessageFragment.Option.None);
            fragment.show(getSupportFragmentManager(), "wrongQR");
            Log.e("ScanActivity.scanResult", e.getMessage());
        }
    }

    @Override
    public void setCameraManager(CameraManager manager) {
        mCameraManager = manager;
        if(mRequestRunning && mCameraManager != null){
            mCameraManager.stopPreview();
        }

        // Will be set to null onPause.
        if (mCameraManager != null) {
            // Set framing rect to fullscreen. Yes, always. Fuck it.
//            mCameraManager.setManualFramingRect(Integer.MAX_VALUE, Integer.MAX_VALUE);
            // Check if zooming is supported and if so, show zoom controls.
            if (mCameraManager.getMaxZoom() != 0) {
//                mZoomControls.setVisibility(View.VISIBLE);
            }
        } else {
//            mZoomControls.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(ACTIVATE_SCANNING, mActivateScanning);
        outState.putBoolean(CLEANUP_SERVICE_STARTED, mCleanupServiceStarted);
        outState.putBoolean(REQUEST_RUNNING, mRequestRunning);
        super.onSaveInstanceState(outState);
    }

    /**
     * Briefly shows the given text and switches back
     * to the default text after t seconds.
     *
     * @param text The text to display.
     * @param t    Delay in miliseconds.
     */

    private void displayText(String text, int t) {
//        mTextView.setText(text);
        mToolBar.setTitle(text);

        if (mDelayHandler != null) {
            mDelayHandler.removeCallbacks(mDelayRunnable);
            mDelayHandler = null;
        }

        mDelayHandler = new Handler();
        mDelayRunnable = new Runnable() {
            public void run() {
//                mTextView.setText(mResources.getText(R.string.scan_search));
                mToolBar.setTitle(mResources.getText(R.string.scan_search));

            }
        };

        mDelayHandler.postDelayed(mDelayRunnable, t);
    }

    /**
     * Zooms in/out.
     *
     * @param z Zoom factor.
     */
    private void zoom(float z) {
        int maxZoom, prevZoom, zoom;

        if (mCameraManager != null) {
            maxZoom = mCameraManager.getMaxZoom();
            if (maxZoom == 0) {
                displayText(mResources.getText(R.string.zoom_not_supported).toString(), 2000);
            } else {
                zoom = mZoom + Math.round(maxZoom * z);
                prevZoom = mZoom;

                if (zoom < 0) {
                    mZoom = 0;
                } else if (zoom > maxZoom) {
                    mZoom = maxZoom;
                } else {
                    mZoom = zoom;
                }

                if (prevZoom != zoom) {
                    //mCameraManager.stopSmoothZoom();
                    //mCameraManager.startSmoothZoom(mZoom);
                    mCameraManager.setZoom(mZoom);
                    displayText("Zoom: " + mZoom + " / " + maxZoom, 2000);
                }
            }
        }
    }



    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    /**
     * Executes request retrieving questions and choices from REST server
     */
    public void performQuestionRequest() {

        // test for internet connectivity
//        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
//        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
//        if(!(activeNetwork != null && activeNetwork.isConnectedOrConnecting())){
//            MessageFragment fragment = MessageFragment.newInstance(getResources().getString(R.string.no_network_title), getResources().getString(R.string.no_network_message), false, MessageFragment.Option.RetryCommunication);
//            fragment.show(getSupportFragmentManager(), "NO_INTERNET");
//            return;
//        }

        // create retrofit instance after receiving host address
        if(mRetrofitRestAdapter == null){
            if(mJacksonConverter == null){
                mJacksonConverter = new JacksonConverter();
            }
            mRetrofitRestAdapter = new RestAdapter.Builder()
                    .setConverter(mJacksonConverter)
                    .setEndpoint(mLastPojo.getHost())
                    .build();
        }

        mToolBar.setTitle(mResources.getText(R.string.scan_send));
        Utility.animateView(mProgressOverlay, View.VISIBLE, 0.8f, 100);
        RetroRequestService requestService = mRetrofitRestAdapter.create(RetroRequestService.class);
        RequestDTO requestDTO = new RequestDTO(mLastPojo.getVoteToken(), DataHolder.getUuid());
        requestService.requestQuestions(requestDTO, new AsyncQuestionsRequest());
        mRequestRunning = true;
    }

    /***********************************************
     * START MESSAGE_FRAGMENT_COMMUNICATOR SECTION
     ************************************************/
    @Override
    public void onRestartQRScanning() {
        EventBus.getEventBus().post(new RestartQRScanningEvent());
    }

    /**
     * Restarts the scanner.
     */
    @Subscribe
    public void scanAgain(RestartQRScanningEvent event) {
        mCameraManager.startPreview();
        mBarcodeFragment.restart();
        mIsScanning = true;
    }


    @Override
    public void onStartServerCommunication() {
        performQuestionRequest();
    }
    /***********************************************
     * END MESSAGE_FRAGMENT_COMMUNICATOR SECTION
     ************************************************/
    /******************************
     * EVENT LISTENER SECTION START
     ******************************/
//    @Subscribe @SuppressWarnings("unused")
//    public void onStartServerCommunication(StartServerCommunicationEvent event) {
//    }
//
//    @Subscribe @SuppressWarnings("unused")
//    public void onRestartQRScanning(RestartQRScanningEvent event) {
//    }

    @Subscribe @SuppressWarnings("unused")
    public void onNetworkSuccess(NetworkSuccessEvent<QuestionsDTO> event){
        mToolBar.setTitle(mResources.getText(R.string.scan_search));
        mRequestRunning = false;

        if (event.getRequestedObject() == null) {
            try {
               ResponseDTO dto = (ResponseDTO)mJacksonConverter.fromBody(event.getResponse().getBody(), ResponseDTO.class);
                ErrorResponseHandling(dto, event.getResponse().getStatus());
            } catch (ConversionException e) {
                e.printStackTrace();
            }

        } else {
            DataHolder.setQuestionsDTO(event.getRequestedObject());
            Intent intent = new Intent(ScanActivity.this, InnerSectionActivity.class);
            startActivity(intent);
        }
        // Hide progress overlay (with animation):
        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);
    }

    @Subscribe @SuppressWarnings("unused")
    public void onNetworkFailure(NetworkFailureEvent event){
        int statusCode = 0;
        ResponseDTO dto = null;
        mRequestRunning = false;
//        if(mBarcodeFragment == null){
//            initBarcodeFragment();
//        }

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
        } else if (statusCode > 300) {
            // Check of status codes and display information to user
            if (statusCode == HttpStatus.BAD_GATEWAY.value() || statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.error_500_502_title), mResources.getString(R.string.no_network_message), true, MessageFragment.Option.RetryCommunication);
                fragment.show(getSupportFragmentManager(), "500|502");
            } else if (statusCode == HttpStatus.SERVICE_UNAVAILABLE.value()) {
                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.error_503_title), mResources.getString(R.string.error_503_message), true, MessageFragment.Option.RetryCommunication);
                fragment.show(getSupportFragmentManager(), "503");
            } else if (statusCode == HttpStatus.FORBIDDEN.value() || statusCode == HttpStatus.NOT_FOUND.value()) {
                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.error_404_403_title), mResources.getString(R.string.error_404_403_message), true, MessageFragment.Option.RetryCommunication);
                fragment.show(getSupportFragmentManager(), "404|403");
            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.unknown_error_title), mResources.getString(R.string.unknown_error_message), true, MessageFragment.Option.RetryCommunication);
                fragment.show(getSupportFragmentManager(), "400");
            }
        } else if(event.getRetrofitError().getCause() != null){
            event.getRetrofitError().printStackTrace();
            mToolBar.setTitle(mResources.getText(R.string.scan_search));

            if (event.getRetrofitError().getCause().getClass() == NoNetworkException.class || event.getRetrofitError().getCause().getClass() == ConnectException.class) {
                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.no_network_title), mResources.getString(R.string.no_network_message), false, MessageFragment.Option.RetryCommunication);
                fragment.show(getSupportFragmentManager(), "NoInternet");
            /*} else if(event.getRetrofitError().getCause().getClass() == ConnectException.class){
                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.connect_exception_title), mResources.getString(R.string.connect_exception_message), false, MessageFragment.Option.CloseApp);
                fragment.show(getSupportFragmentManager(), "NetworkChanged");*/
            } else if(event.getRetrofitError().getCause().getClass() == SocketTimeoutException.class || event.getRetrofitError().getCause().getClass() == SocketException.class){
                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.socket_timeout_title), mResources.getString(R.string.socket_timeout_message), true, MessageFragment.Option.RetryCommunication);
                fragment.show(getSupportFragmentManager(), "ServerNotResponding");
            } else {
                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.some_network_error_title), mResources.getString(R.string.some_network_error_message), true, MessageFragment.Option.RetryCommunication);
                fragment.show(getSupportFragmentManager(), "SomeError");
            }
        }
        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);
    }

    /******************************
     * EVENT LISTENER SECTION END
     ******************************/

    /**
     * Error handling outsourced from onNetworkSuccess
     * @param statusCode
     */
    private void ErrorResponseHandling(ResponseDTO dto, int statusCode) {
        if (dto != null && dto.getType() == ErrorType.INVALID_TOKEN) {
            MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.invalid_token_title), mResources.getString(R.string.invalid_token_message), true, MessageFragment.Option.RetryScan);
            fragment.show(getSupportFragmentManager(), "InvalidToken");
        } else if (dto != null && dto.getType() == ErrorType.TOKEN_ALLREADY_USED) {
            MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.token_already_used_title), mResources.getString(R.string.token_already_used_message), true, MessageFragment.Option.RetryScan);
            fragment.show(getSupportFragmentManager(), "TokenAlreadyUsed");
        } else if (dto != null && dto.getType() == ErrorType.EVALUATION_CLOSED) {
            MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.evaluation_closed_title), mResources.getString(R.string.evaluation_closed_message), true, MessageFragment.Option.RetryScan);
            fragment.show(getSupportFragmentManager(), "EvaluationClosed");
        } else if (dto != null && dto.getType() == ErrorType.UNKNOWN_ERROR) {
            MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.unknown_error_title), mResources.getString(R.string.unknown_error_message), true, MessageFragment.Option.RetryScan);
            fragment.show(getSupportFragmentManager(), "UnknownError");
        } else if (dto != null && dto.getType() == ErrorType.MALFORMED_REQUEST) {
            MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.unknown_error_title), mResources.getString(R.string.unknown_error_message), true, MessageFragment.Option.RetryScan);
            fragment.show(getSupportFragmentManager(), "MalformedRequest");
        }
    }



    private class AsyncQuestionsRequest implements Callback<QuestionsDTO> {
        @Override
        public void success(QuestionsDTO questionsDTO, Response response) {
            EventBus.getEventBus().post(new NetworkSuccessEvent<QuestionsDTO>(questionsDTO, response));
        }

        @Override
        public void failure(RetrofitError error) {
            EventBus.getEventBus().post(new NetworkFailureEvent(error));
        }

//        @Override
//        public void onResponse(Response<QuestionsDTO> response, Retrofit retrofit) {
//            EventBus.getEventBus().post(new NetworkSuccessEvent<QuestionsDTO>(response, retrofit));
//        }
//
//        @Override
//        public void onFailure(Throwable e) {
//            EventBus.getEventBus().post(new NetworkFailureEvent(e));
//        }


    }
}