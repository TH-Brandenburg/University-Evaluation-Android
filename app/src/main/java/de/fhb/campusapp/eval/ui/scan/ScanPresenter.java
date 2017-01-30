package de.fhb.campusapp.eval.ui.scan;

import android.Manifest;
import android.content.res.Resources;
import android.support.v4.util.Pair;

import com.github.buchandersenn.android_permission_manager.PermissionManager;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.tuple.Triple;
import org.joda.time.Instant;

import java.util.concurrent.TimeUnit;

import javax.inject.Inject;

import de.fhb.ca.dto.QuestionsDTO;
import de.fhb.ca.dto.RequestDTO;
import de.fhb.campusapp.eval.data.IDataManager;
import de.fhb.campusapp.eval.interfaces.RetroRequestService;
import de.fhb.campusapp.eval.ui.base.BasePresenter;
import de.fhb.campusapp.eval.utility.ClassMapper;
import de.fhb.campusapp.eval.data.DataManager;
import de.fhb.campusapp.eval.utility.DebugConfigurator;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.NetworkErrorEvent;
import de.fhb.campusapp.eval.utility.Events.RequestErrorEvent;
import de.fhb.campusapp.eval.utility.Events.RequestSuccessEvent;
import de.fhb.campusapp.eval.utility.vos.AnswersVO;
import de.fhb.campusapp.eval.utility.vos.QuestionsVO;
import fhb.de.campusappevaluationexp.R;
import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

/**
 * Created by Sebastian Müller on 16.10.2016.
 */
public class ScanPresenter extends BasePresenter<ScanMvpView> {

    final Resources mResources;

    final IDataManager mDataManager;

    @Inject
    public ScanPresenter(Resources mResources, IDataManager mDataManager) {
        super();
        this.mResources = mResources;
        this.mDataManager = mDataManager;
    }

    /**
     * Executes request retrieving questions and choices from REST server
     */
    public void performQuestionRequest() {
        getMvpView().changeToolbarTitle(mResources.getString(R.string.scan_send));
        getMvpView().showProgressOverlay();

        mDataManager.initAndObserveQuestionRequest();

    }

    public void requestInternetPermissionAndConnectServer(PermissionManager manager){
        manager.with(Manifest.permission.INTERNET)
                .onPermissionGranted(() -> performQuestionRequest())
                .onPermissionDenied(() -> getMvpView().callSaveTerminateTask())
                .onPermissionShowRationale(request -> getMvpView().showInternetExplanation(request))
                .request();
    }

    public void debugConfiguration() {
        mDataManager.setmQuestionsVO(new QuestionsVO(
                DebugConfigurator.getDemoStudyPaths(),
                DebugConfigurator.getDemoTextQuestions(),
                DebugConfigurator.getDemoMultipleChoiceQuestionDTOs(),
                false
        ));

        mDataManager.getmAnswersVO().setVoteToken(DebugConfigurator.genericVoteToken);
        mDataManager.getmAnswersVO().setDeviceID(DebugConfigurator.genericID);
    }

    public void initAnswersVO(String voteToken, String hostName) {
        mDataManager.getmAnswersVO().setVoteToken(voteToken);
        mDataManager.getmAnswersVO().setDeviceID(mDataManager.getmUuid());
        mDataManager.setmHostName(hostName);
    }

    public void saveAllData(){
        mDataManager.saveAllData();
    }

    public void removeAllData(){
        mDataManager.removeAllData();
    }

    public void setUuid(String uuid){
        mDataManager.setmUuid(uuid);
    }

    public void setStartTime(Instant now){
        mDataManager.setmAppStartTime(now);
    }

    public Instant getStartTime(){
        return mDataManager.getmAppStartTime();
    }
}
