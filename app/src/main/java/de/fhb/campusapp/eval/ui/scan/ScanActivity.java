package de.fhb.campusapp.eval.ui.scan;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.StringRes;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.abhi.barcode.frag.libv2.BarcodeFragment;
import com.abhi.barcode.frag.libv2.ICameraManagerListener;
import com.abhi.barcode.frag.libv2.IScanResultHandler;
import com.abhi.barcode.frag.libv2.ScanResult;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.buchandersenn.android_permission_manager.PermissionManager;
import com.github.buchandersenn.android_permission_manager.PermissionRequest;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.android.camera.CameraManager;
import com.squareup.otto.Subscribe;

import org.joda.time.Instant;

import java.io.IOException;
import java.util.EnumSet;
import java.util.UUID;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.fhb.campusapp.eval.data.local.RetrofitHelper;
import de.fhb.campusapp.eval.services.CleanUpService;
import de.fhb.campusapp.eval.ui.base.BaseActivity;
import de.fhb.campusapp.eval.ui.eval.EvaluationActivity;
import de.fhb.campusapp.eval.utility.ActivityUtil;
import de.fhb.campusapp.eval.utility.DataHolder;
import de.fhb.campusapp.eval.utility.DebugConfigurator;
import de.fhb.campusapp.eval.utility.DialogFactory;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.RestartQRScanningEvent;
import de.fhb.campusapp.eval.utility.FeatureSwitch;
import de.fhb.campusapp.eval.utility.Utility;
import de.fhb.campusapp.eval.utility.vos.QrDataVo;
import de.fhb.campusapp.eval.utility.vos.QuestionsVO;
import fhb.de.campusappevaluationexp.R;

public class ScanActivity extends BaseActivity implements IScanResultHandler, ICameraManagerListener, ScanMvpView{

    private static final String TAG = ScanActivity.class.getSimpleName();
    private static final String PACKAGE_NAME = ScanActivity.class.getPackage().getName();
    public final static String ACTIVATE_SCANNING = "ACTIVATE_SCANNING";
    private static final String CLEANUP_SERVICE_STARTED = "CLEANUP_SERVICE_STARTED";
    private static final String REQUEST_RUNNING = "REQUEST_RUNNING";

    // Retrieve the id, hash it and create a QuestionRequest with it.
//    private String DEVICE_ID = "";

    @Inject
    public Resources mResources;

    @Inject
    public RetrofitHelper mRetrofitHelper = new RetrofitHelper();

    @Inject
    public ScanPresenter mScanPresenter;

    @Inject
    public PermissionManager mPermissionManager;

    private BarcodeFragment mBarcodeFragment;
    private CameraManager mCameraManager;

//    private JacksonConverter mJacksonConverter;
    /*
    * The QrPojo object that was created from the last scanned QR code.
    * */
    private QrDataVo mLastPojo;
    private boolean mActivateScanning;
    private boolean mCleanupServiceStarted = false;
    private boolean mRequestRunning = false;

    @BindView(R.id.progress_overlay)
    View mProgressOverlay;

    @BindView(R.id.my_awesome_toolbar)
    Toolbar mToolBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.mActicityComponent.bind(this);
        setContentView(R.layout.activity_scan);
        ButterKnife.bind(this);
        mScanPresenter.attachView(this);

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
            showProgressOverlay();
        }

        //starts the cleanUpService -> deletes all images when app is closed
        if(!mCleanupServiceStarted){
            Intent serviceIntent = new Intent(this, CleanUpService.class);
            startService(serviceIntent);
            mCleanupServiceStarted = true;
        }

        //close the application
        if (getIntent().getBooleanExtra("CLOSE", false)) {
            // delete all data and close application (as best as android lets you)
            DataHolder.deleteAllData();
            ActivityUtil.saveTerminateTask(this);
        }

        if (getIntent().getBooleanExtra("GO_TO_SCAN", false)) {
            DataHolder.deleteAllData();
        }

        //sets the uuid for this session
        DataHolder.setUuid(UUID.randomUUID().toString());

        initBarcodeFragment();

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
            mBarcodeFragment.restart();
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

        mScanPresenter.registerToEventBus();

        if(!mRequestRunning){
            mToolBar.setTitle(mResources.getText(R.string.scan_search));
            hideProgressOverlay();
        } else {
            mToolBar.setTitle(mResources.getText(R.string.scan_send));
            showProgressOverlay();
        }
        super.onResume();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mScanPresenter.detachView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        if(FeatureSwitch.DEBUG_ACTIVE){
            getMenuInflater().inflate(R.menu.action_bar_mock_menu, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.mock_questionnaire) {
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
        } else if(id == R.id.mock_qr_code_reading){
            mScanPresenter.performQuestionRequest();
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void scanResult(ScanResult result) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            mLastPojo = mapper.readValue(result.getRawResult().getText(), QrDataVo.class);
            // store vote token within answerDTO
            if (mLastPojo != null && mLastPojo.getHost() != null && !mLastPojo.getHost().equals("") && mLastPojo.getVoteToken() != null && !mLastPojo.getVoteToken().equals("")) {
                DataHolder.getAnswersVO().setVoteToken(mLastPojo.getVoteToken());
            } else {
                throw new IOException("QRPojo was not initialized correctly");
            }

            DataHolder.getAnswersVO().setDeviceID(DataHolder.getUuid());

            // share host name between activities
            DataHolder.setHostName(mLastPojo.getHost());
            mScanPresenter.requestInternetPermissionAndConnectServer(mPermissionManager);
            mCameraManager.stopPreview();
        } catch (IOException e) {
            Dialog dialog = DialogFactory.createSimpleOkErrorDialog(this
                    ,R.string.wrong_qr_code_error_title
                    ,R.string.wrong_qr_code_error_message
                    ,true);
            dialog.show();
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
    protected void onPause() {
        super.onPause();
        mScanPresenter.unregisterFromEventBus();

    }

    @Override
    protected void onStop() {
        DataHolder.storeAllData();
        super.onStop();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        mPermissionManager.handlePermissionResult(requestCode, grantResults);
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
    }


    public void onStartServerCommunication() {
        mScanPresenter.requestInternetPermissionAndConnectServer(mPermissionManager);
    }
    /***********************************************
     * END MESSAGE_FRAGMENT_COMMUNICATOR SECTION
     ************************************************/
    /******************************
     * EVENT LISTENER SECTION START
     ******************************/

    @Override
    public void hideProgressOverlay() {
        Utility.animateView(mProgressOverlay, View.GONE, 0, 100);
    }

    @Override
    public void showProgressOverlay() {
        Utility.animateView(mProgressOverlay, View.VISIBLE, 0.8f, 100);
    }

    @Override
    public void changeToolbarTitle(String title) {
        mToolBar.setTitle(title);
    }

    @Override
    public void changeToolbarTitle(@StringRes int title) {
        mToolBar.setTitle(mResources.getString(title));
    }

    @Override
    public void setRequestRunning(boolean running) {
        mRequestRunning = running;
    }

    @Override
    public void showInternetExplanation(PermissionRequest request) {
        AlertDialog dialog = DialogFactory.createAcceptDenyDialog(this
                ,R.string.internet_explanation_title
                ,R.string.internet_explanation_message
                ,(dialogInterface, i) -> request.acceptPermissionRationale()
                ,(dialogInterface, i) -> ActivityUtil.saveTerminateTask(this));
        dialog.show();
    }

    @Override
    public void callSaveTerminateTask() {
        ActivityUtil.saveTerminateTask(this);
    }

    @Override
    public void showRetryScanDialog(String title, String message) {
        Dialog dialog  = DialogFactory.createSimpleOkErrorDialog(this
                , title
                , message
                , (dialogInterface, i) -> onRestartQRScanning()
                , dialogInterface -> onRestartQRScanning()
                , true);
        dialog.show();
    }

    @Override
    public void showRetryServerCommunicationDialog(String title, String message) {
        Dialog dialog  = DialogFactory.createAcceptDenyDialog(this
                , title
                , message
                , mResources.getString(R.string.retry_button)
                , mResources.getString(R.string.abort_button)
                , true
                , (dialogInterface, i) -> onStartServerCommunication()
                , (dialogInterface, i) -> onRestartQRScanning()
                , dialogInterface -> onRestartQRScanning());
        dialog.show();
    }

    @Override
    public void showNetworkErrorDialog(String title, String message) {
        Dialog dialog  = DialogFactory.createSimpleOkErrorDialog(this
                , title
                , message
                , (dialogInterface, i) -> onRestartQRScanning()
                , dialogInterface -> onRestartQRScanning()
                , true);

        dialog.show();

    }

    @Override
    public void startEvaluationActivity() {
        Intent intent = new Intent(this, EvaluationActivity.class);
        startActivity(intent);
    }
}