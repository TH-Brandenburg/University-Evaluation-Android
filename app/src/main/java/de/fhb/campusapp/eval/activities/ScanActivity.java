package de.fhb.campusapp.eval.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.support.v4.util.Pair;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.Instant;

import java.io.IOException;
import java.util.EnumSet;
import java.util.UUID;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.fhb.ca.dto.QuestionsDTO;
import de.fhb.ca.dto.RequestDTO;
import de.fhb.campusapp.eval.data.local.RetrofitHelper;
import de.fhb.campusapp.eval.interfaces.RetroRequestService;
import de.fhb.campusapp.eval.services.CleanUpService;
import de.fhb.campusapp.eval.ui.base.BaseActivity;
import de.fhb.campusapp.eval.ui.eval.EvaluationActivity;
import de.fhb.campusapp.eval.utility.ActivityUtil;
import de.fhb.campusapp.eval.utility.ClassMapper;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.DebugConfigurator;
import de.fhb.campusapp.eval.utility.DialogFactory;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.RequestErrorEvent;
import de.fhb.campusapp.eval.utility.Events.NetworkErrorEvent;
import de.fhb.campusapp.eval.utility.Events.RequestSuccessEvent;
import de.fhb.campusapp.eval.utility.Events.RestartQRScanningEvent;
import de.fhb.campusapp.eval.utility.FeatureSwitch;
import de.fhb.campusapp.eval.utility.QrPojo;
import de.fhb.campusapp.eval.utility.Utility;
import de.fhb.campusapp.eval.utility.vos.QuestionsVO;
import fhb.de.campusappevaluationexp.R;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

public class ScanActivity extends BaseActivity implements IScanResultHandler, ICameraManagerListener{

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
    private Retrofit mRetrofit;
//    private JacksonConverter mJacksonConverter;
    /*
    * The QrPojo object that was created from the last scanned QR code.
    * */
    private QrPojo mLastPojo;

    private int mZoom;
    private boolean mActivateScanning;
    private boolean mIsScanning;
    private boolean mCleanupServiceStarted = false;
    private boolean mRequestRunning = false;
    private RetrofitHelper mRetrofitHelper = new RetrofitHelper();

    @BindView(R.id.progress_overlay)
    View mProgressOverlay;

    @BindView(R.id.my_awesome_toolbar)
    Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);

        super.fixOrientationToPortrait();

        // DataHolder gets ability to freely serialize/deserialize its variables
        DataHolder.setPreferences(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));

        if(DataHolder.getAppStart() == null){
            DataHolder.setAppStart(new Instant());
        }

        //if data in shared preferences is still usable open evaluationactivity instead
//        if(DataHolder.validateAllData()){
//            Intent evalIntent = new Intent(this, EvaluationActivity.class);
//            startActivity(evalIntent);
//        }

        //start the app
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
            Intent serviceIntent = new Intent(this, CleanUpService.class);
            startService(serviceIntent);
            mCleanupServiceStarted = true;
        }

        if (getIntent().getBooleanExtra("GO_TO_SCAN", false)) {
            DataHolder.deleteAllData();
        }

        //sets the uuid for this session
        DataHolder.setUuid(UUID.randomUUID().toString());

        initBarcodeFragment();

        mIsScanning = true;

        //close the application
        if (getIntent().getBooleanExtra("CLOSE", false)) {
            // delete all data and close application (as best as android lets you)
            DataHolder.deleteAllData();
            ActivityUtil.saveFinish(this);
        }
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
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        // DataHolder gets ability to freely serialize/deserialize its variables
        // Android might clear variable in DataHolder while App is in background leading to shit.
        DataHolder.setPreferences(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
        DataHolder.storeAllData();
        if(!mRequestRunning){
            mToolBar.setTitle(mResources.getText(R.string.scan_search));
        } else {
            mToolBar.setTitle(mResources.getText(R.string.scan_send));
        }
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(FeatureSwitch.DEBUG_ACTIVE){
            getMenuInflater().inflate(R.menu.action_bar_navigator_only, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

//    @Override
//    public boolean onPrepareOptionsMenu(Menu menu) {
//        return super.onPrepareOptionsMenu(menu);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.question_search) {
            DataHolder.setQuestionsVO(new QuestionsVO(
                    DebugConfigurator.getDemoStudyPaths(),
                    DebugConfigurator.getDemoTextQuestions(),
                    DebugConfigurator.getDemoMultipleChoiceQuestionDTOs(),
                    false
            ));

            DataHolder.getAnswersVO().setVoteToken(DebugConfigurator.genericVoteToken);
            DataHolder.getAnswersVO().setDeviceID(DebugConfigurator.genericID);

            Intent intent = new Intent(ScanActivity.this, EvaluationActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void scanResult(ScanResult result) {
        mIsScanning = false;
        try {
            ObjectMapper mapper = new ObjectMapper();
            mLastPojo = mapper.readValue(result.getRawResult().getText(), QrPojo.class);
            // store vote token within answerDTO
            if (mLastPojo != null && mLastPojo.getHost() != null && !mLastPojo.getHost().equals("") && mLastPojo.getVoteToken() != null && !mLastPojo.getVoteToken().equals("")) {
                DataHolder.getAnswersVO().setVoteToken(mLastPojo.getVoteToken());
            } else {
                throw new IOException("QRPojo was not initialized correctly");
            }

            DataHolder.getAnswersVO().setDeviceID(DataHolder.getUuid());

            // share host name between activities
            DataHolder.setHostName(mLastPojo.getHost());
            performQuestionRequest(/*mLastPojo, spiceManager*/);
            mCameraManager.stopPreview();
        } catch (IOException e) {
            Dialog dialog = DialogFactory.createSimpleOkErrorDialog(this
                    ,R.string.wrong_qr_code_error_title
                    ,R.string.wrong_qr_code_error_message
                    ,true);
            dialog.show();
            //            MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.wrong_qr_code_error_title), mResources.getString(R.string.wrong_qr_code_error_message), true, MessageFragment.Option.None);
//            fragment.show(getSupportFragmentManager(), "wrongQR");
            Log.e("ScanActivity.scanResult", e.getMessage());
        }
    }

    @Override
    public void setCameraManager(CameraManager manager) {
        mCameraManager = manager;
        if(mRequestRunning && mCameraManager != null){
            mCameraManager.stopPreview();
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putBoolean(ACTIVATE_SCANNING, mActivateScanning);
        outState.putBoolean(CLEANUP_SERVICE_STARTED, mCleanupServiceStarted);
        outState.putBoolean(REQUEST_RUNNING, mRequestRunning);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStop() {
        DataHolder.storeAllData();
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
        if(mRetrofit == null){
            mRetrofit = new Retrofit.Builder()
                    .baseUrl(mLastPojo.getHost())
                    .addConverterFactory(JacksonConverterFactory.create())
                    .build();
        }

        mToolBar.setTitle(mResources.getText(R.string.scan_send));
        Utility.animateView(mProgressOverlay, View.VISIBLE, 0.8f, 100);
        RequestDTO requestDTO = new RequestDTO(mLastPojo.getVoteToken(), DataHolder.getUuid());
        RetroRequestService requestService = mRetrofit.create(RetroRequestService.class);
        Call<QuestionsDTO> response = requestService.requestQuestions(requestDTO);
        response.enqueue(new RetrofitCallback());
        mRequestRunning = true;
    }

    /***********************************************
     * START MESSAGE_FRAGMENT_COMMUNICATOR SECTION
     ************************************************/

    public void onRestartQRScanning() {
        EventBus.get().post(new RestartQRScanningEvent());
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


    public void onStartServerCommunication() {
        performQuestionRequest();
    }
    /***********************************************
     * END MESSAGE_FRAGMENT_COMMUNICATOR SECTION
     ************************************************/
    /******************************
     * EVENT LISTENER SECTION START
     ******************************/
    @Subscribe
    @SuppressWarnings("unused")
    public void onRequestSuccess(RequestSuccessEvent<QuestionsDTO> event){
        QuestionsVO vo = ClassMapper.questionsDTOToQuestionsVOMapper(event.getRequestedObject());
        DataHolder.setQuestionsVO(vo);
        // Hide progress overlay (with animation):
        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);
        Intent intent = new Intent(ScanActivity.this, EvaluationActivity.class);
        startActivity(intent);
    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onNetworkError(NetworkErrorEvent event){
        mRequestRunning = false;
        Throwable t = event.getRetrofitError();
        Pair<String, String> errorText = mRetrofitHelper.processNetworkError(t, mResources);

        Dialog dialog  = DialogFactory.createSimpleOkErrorDialog(this
                , errorText.first
                , errorText.second
                , (dialogInterface, i) -> onRestartQRScanning()
                , dialogInterface -> onRestartQRScanning()
                , true);

        dialog.show();

        mToolBar.setTitle(mResources.getText(R.string.scan_search));
        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);


    }

    @Subscribe
    @SuppressWarnings("unused")
    public void onRequestError(RequestErrorEvent event){
        Triple<String, String, String> errorText = mRetrofitHelper.processRequestError(event.getResposne(), mResources, mRetrofit);
        Dialog dialog = null;
        if(errorText.getRight().equals("RETRY_SCAN")){
            dialog  = DialogFactory.createSimpleOkErrorDialog(this
                    , errorText.getLeft()
                    , errorText.getMiddle()
                    , (dialogInterface, i) -> onRestartQRScanning()
                    , dialogInterface -> onRestartQRScanning()
                    , true);
        } else if(errorText.getRight().equals("RETRY_COMMUNICATION")){
           dialog  = DialogFactory.createSimpleOkErrorDialog(this
                    , errorText.getLeft()
                    , errorText.getMiddle()
                    , (dialogInterface, i) -> onStartServerCommunication()
                    , dialogInterface -> onStartServerCommunication()
                    , true);
        }

        dialog.show();
        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);
    }

    private class RetrofitCallback implements Callback<QuestionsDTO> {

        @Override
        public void onResponse(Call<QuestionsDTO> call, Response<QuestionsDTO> response) {
            mToolBar.setTitle(mResources.getText(R.string.scan_search));
            mRequestRunning = false;
            int statusCode = response.code();

            if(response.isSuccessful()){
                EventBus.get().post(new RequestSuccessEvent<>(response.body(), response));
            } else {
                EventBus.get().post(new RequestErrorEvent<>(response));
            }
        }

        @Override
        public void onFailure(Call<QuestionsDTO> call, Throwable t) {
            EventBus.get().post(new NetworkErrorEvent(t));
        }
    }


//    /**
//     * Error handling outsourced from onResponse
//     */
//    private void ErrorResponseHandling(Response<QuestionsDTO> response) {
//        int statusCode = response.code();
//
//        try {
//            ResponseBody body = response.errorBody();
//            // annotation array must be created in order to prevent nullPointer
//            ResponseDTO dto = (ResponseDTO) mRetrofit.responseBodyConverter(ResponseDTO.class, new Annotation[1] ).convert(body);
//
//            if (dto != null && dto.getType() == ErrorType.INVALID_TOKEN) {
////                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.invalid_token_title), mResources.getString(R.string.invalid_token_message), true, MessageFragment.Option.RetryScan);
////                fragment.show(getSupportFragmentManager(), "InvalidToken");
//            } else if (dto != null && dto.getType() == ErrorType.TOKEN_ALLREADY_USED) {
////                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.token_already_used_title), mResources.getString(R.string.token_already_used_message), true, MessageFragment.Option.RetryScan);
////                fragment.show(getSupportFragmentManager(), "TokenAlreadyUsed");
//            } else if (dto != null && dto.getType() == ErrorType.EVALUATION_CLOSED) {
////                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.evaluation_closed_title), mResources.getString(R.string.evaluation_closed_message), true, MessageFragment.Option.RetryScan);
////                fragment.show(getSupportFragmentManager(), "EvaluationClosed");
//            } else if (dto != null && dto.getType() == ErrorType.UNKNOWN_ERROR) {
////                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.unknown_error_title), mResources.getString(R.string.unknown_error_message), true, MessageFragment.Option.RetryScan);
////                fragment.show(getSupportFragmentManager(), "UnknownError");
//            } else if (dto != null && dto.getType() == ErrorType.MALFORMED_REQUEST) {
////                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.unknown_error_title), mResources.getString(R.string.unknown_error_message), true, MessageFragment.Option.RetryScan);
////                fragment.show(getSupportFragmentManager(), "MalformedRequest");
//            } else {
//                // Check of status codes and display information to user
//                if (statusCode == HttpURLConnection.HTTP_BAD_GATEWAY || statusCode == HttpURLConnection.HTTP_INTERNAL_ERROR) {
////                    MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.error_500_502_title), mResources.getString(R.string.no_network_message), true, MessageFragment.Option.RetryCommunication);
////                    fragment.show(getSupportFragmentManager(), "500|502");
//                } else if (statusCode == HttpURLConnection.HTTP_UNAVAILABLE) {
////                    MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.error_503_title), mResources.getString(R.string.error_503_message), true, MessageFragment.Option.RetryCommunication);
////                    fragment.show(getSupportFragmentManager(), "503");
//                } else if (statusCode == HttpURLConnection.HTTP_FORBIDDEN || statusCode == HttpURLConnection.HTTP_NOT_FOUND) {
////                    MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.error_404_403_title), mResources.getString(R.string.error_404_403_message), true, MessageFragment.Option.RetryCommunication);
////                    fragment.show(getSupportFragmentManager(), "404|403");
//                } else if (statusCode == HttpURLConnection.HTTP_BAD_REQUEST) {
////                    MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.unknown_error_title), mResources.getString(R.string.unknown_error_message), true, MessageFragment.Option.RetryCommunication);
////                    fragment.show(getSupportFragmentManager(), "400");
//                }
//            }
//        } catch (IOException | IllegalArgumentException e ) {
//            e.printStackTrace();
//        }
//    }


//    @Subscribe @SuppressWarnings("unused")
//    public void onStartServerCommunication(StartServerCommunicationEvent event) {
//    }
//
//    @Subscribe @SuppressWarnings("unused")
//    public void onRestartQRScanning(RestartQRScanningEvent event) {
//    }

//    @Subscribe @SuppressWarnings("unused")
//    public void onRequestSuccess(NetworkSuccessEvent<QuestionsDTO> event){
//        mToolBar.setTitle(mResources.getText(R.string.scan_search));
//        mRequestRunning = false;
//
//        if (event.getRequestedObject() == null) {
//            try {
//               ResponseDTO dto = (ResponseDTO)mJacksonConverter.fromBody(event.getResponse().getBody(), ResponseDTO.class);
//                ErrorResponseHandling(dto, event.getResponse().getStatus());
//            } catch (ConversionException e) {
//                e.printStackTrace();
//            }
//
//        } else {
//            QuestionsVO vo = ClassMapper.questionsDTOToQuestionsVOMapper(event.getRequestedObject());
//            DataHolder.setQuestionsVO(vo);
//            Intent intent = new Intent(ScanActivity.this, EvaluationActivity.class);
//            startActivity(intent);
//        }
//        // Hide progress overlay (with animation):
//        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);
//    }

//    @Subscribe @SuppressWarnings("unused")
//    public void onNetworkError(NetworkFailureEvent event){
//        int statusCode = 0;
//        ResponseDTO dto = null;
//        mRequestRunning = false;
//        if(event.getRetrofitError().getResponse() != null){
//            statusCode = event.getRetrofitError().getResponse().getStatus();
//
//            if(event.getRetrofitError().getResponse().getBody() != null){
//                try {
//                    dto = (ResponseDTO)mJacksonConverter.fromBody(event.getRetrofitError().getResponse().getBody(), ResponseDTO.class);
//                } catch (ConversionException e) {
//                    e.printStackTrace();
//                }
//            }
//        }
//
//        if(dto != null){
//            ErrorResponseHandling(dto, event.getRetrofitError().getResponse().getStatus());
//        } else if (statusCode > 300) {
//            // Check of status codes and display information to user
//            if (statusCode == HttpStatus.BAD_GATEWAY.value() || statusCode == HttpStatus.INTERNAL_SERVER_ERROR.value()) {
//                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.error_500_502_title), mResources.getString(R.string.no_network_message), true, MessageFragment.Option.RetryCommunication);
//                fragment.show(getSupportFragmentManager(), "500|502");
//            } else if (statusCode == HttpStatus.SERVICE_UNAVAILABLE.value()) {
//                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.error_503_title), mResources.getString(R.string.error_503_message), true, MessageFragment.Option.RetryCommunication);
//                fragment.show(getSupportFragmentManager(), "503");
//            } else if (statusCode == HttpStatus.FORBIDDEN.value() || statusCode == HttpStatus.NOT_FOUND.value()) {
//                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.error_404_403_title), mResources.getString(R.string.error_404_403_message), true, MessageFragment.Option.RetryCommunication);
//                fragment.show(getSupportFragmentManager(), "404|403");
//            } else if (statusCode == HttpStatus.BAD_REQUEST.value()) {
//                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.unknown_error_title), mResources.getString(R.string.unknown_error_message), true, MessageFragment.Option.RetryCommunication);
//                fragment.show(getSupportFragmentManager(), "400");
//            }
//        } else if(event.getRetrofitError().getCause() != null){
//            event.getRetrofitError().printStackTrace();
//            mToolBar.setTitle(mResources.getText(R.string.scan_search));
//
//            if (event.getRetrofitError().getCause().getClass() == NoNetworkException.class || event.getRetrofitError().getCause().getClass() == ConnectException.class) {
//                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.no_network_title), mResources.getString(R.string.no_network_message), false, MessageFragment.Option.RetryCommunication);
//                fragment.show(getSupportFragmentManager(), "NoInternet");
//            /*} else if(event.getRetrofitError().getCause().getClass() == ConnectException.class){
//                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.connect_exception_title), mResources.getString(R.string.connect_exception_message), false, MessageFragment.Option.CloseApp);
//                fragment.show(getSupportFragmentManager(), "NetworkChanged");*/
//            } else if(event.getRetrofitError().getCause().getClass() == SocketTimeoutException.class || event.getRetrofitError().getCause().getClass() == SocketException.class){
//                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.socket_timeout_title), mResources.getString(R.string.socket_timeout_message), true, MessageFragment.Option.RetryCommunication);
//                fragment.show(getSupportFragmentManager(), "ServerNotResponding");
//            } else {
//                MessageFragment fragment = MessageFragment.newInstance(mResources.getString(R.string.some_network_error_title), mResources.getString(R.string.some_network_error_message), true, MessageFragment.Option.RetryCommunication);
//                fragment.show(getSupportFragmentManager(), "SomeError");
//            }
//        }
//        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);
//    }

    /******************************
     * EVENT LISTENER SECTION END
     ******************************/



    /*private class AsyncQuestionsRequest implements Callback<QuestionsDTO> {
        @Override
        public void success(QuestionsDTO questionsDTO, Response response) {
            EventBus.get().post(new NetworkSuccessEvent<QuestionsDTO>(questionsDTO, response));
        }

        @Override
        public void failure(RetrofitError error) {
            EventBus.get().post(new NetworkFailureEvent(error));
        }

//        @Override
//        public void onResponse(Response<QuestionsDTO> response, Retrofit retrofit) {
//            EventBus.get().post(new NetworkSuccessEvent<QuestionsDTO>(response, retrofit));
//        }
//
//        @Override
//        public void onFailure(Throwable e) {
//            EventBus.get().post(new NetworkFailureEvent(e));
//        }


    }*/
}