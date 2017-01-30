package de.fhb.campusapp.eval.ui.eval;

import android.Manifest;
import android.content.res.Resources;
import android.support.v4.util.Pair;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.buchandersenn.android_permission_manager.PermissionManager;
import com.squareup.otto.Subscribe;

import org.apache.commons.lang3.tuple.Triple;

import java.io.File;
import java.util.ArrayList;

import javax.inject.Inject;

import de.fhb.ca.dto.AnswersDTO;
import de.fhb.ca.dto.ResponseDTO;
import de.fhb.campusapp.eval.data.IDataManager;
import de.fhb.campusapp.eval.data.local.RetrofitHelper;
import de.fhb.campusapp.eval.interfaces.RetroRespondService;
import de.fhb.campusapp.eval.ui.base.BasePresenter;
import de.fhb.campusapp.eval.utility.ClassMapper;
import de.fhb.campusapp.eval.data.DataManager;
import de.fhb.campusapp.eval.utility.EventBus;
import de.fhb.campusapp.eval.utility.Events.NetworkErrorEvent;
import de.fhb.campusapp.eval.utility.Events.RequestErrorEvent;
import de.fhb.campusapp.eval.utility.Events.RequestSuccessEvent;
import de.fhb.campusapp.eval.utility.FeatureSwitch;
import de.fhb.campusapp.eval.utility.Observer.CreateUploadImageObservable;
import de.fhb.campusapp.eval.utility.vos.ChoiceVO;
import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceAnswerVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceQuestionVO;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sebastian Müller on 09.10.2016.
 */
public class EvalPresenter extends BasePresenter<EvalMvpView>{

    @Inject
    Resources mResources;

    @Inject
    IDataManager mDataManager;

    @Inject
    public EvalPresenter(Resources resources, IDataManager dataManager) {
        super();
        this.mResources = resources;
        this.mDataManager = dataManager;
    }

    /*
    * Executes request retrieving questions and choices from REST server
    * */
    private void performAnswerRequest() {

        if(FeatureSwitch.DEBUG_ACTIVE && mDataManager.getmHostName() == null){
            getMvpView().hideProgressOverlay();
            getMvpView().showDebugMessage();
            return;
        }

        mDataManager.initAndObserveAnswersResponse();
    }

    void setNewPagerPosition(int newPosition){
        mDataManager.setmCurrentPagerPosition(newPosition);
    }

    public void requestInternetPermissionAndConnectServer(PermissionManager manager){
        manager.with(Manifest.permission.INTERNET)
                .onPermissionGranted(() -> performAnswerRequest())
                .onPermissionDenied(() -> getMvpView().callSaveTerminateTask())
                .onPermissionShowRationale(request -> getMvpView().showInternetExplanationDialog(request))
                .request();
    }

    public void saveAllData(){
    }

    public boolean isQuestionTextQuestion(){
        return mDataManager.isTextQuestion(mDataManager.getmCurrentQuestion());
    }

    public boolean isCurrentQuestionAnswered(){
        return mDataManager.isQuestionAnswered(mDataManager.getmCurrentQuestion());
    }

    /**
     * Gives every question a representation in AnswersDTO which never got one assigned by the user.
     */
    public void setUnasweredQuestions(){
        //server expects all questions to have an entry in answers dto. Add all the user did not set
        for(TextAnswerVO answerVO : mDataManager.getmAnswersVO().getTextAnswers()){
            if(mDataManager.isTextQuestionAnswered(answerVO.getQuestionText())){
                mDataManager.getmAnswersVO().getTextAnswers().add(new TextAnswerVO(answerVO.getQuestionID(), answerVO.getQuestionText(), ""));
            }
        }

        //loop through all mcQuestions
        for(MultipleChoiceQuestionVO questionVO : mDataManager.getmQuestionsVO().getMultipleChoiceQuestionVOs()){
            //test if any wasnt answered by the user
            if(mDataManager.isMcQuestionAnswered(questionVO.getQuestion())){
                ChoiceVO noCommentChoice = mDataManager.retrieveChoiceByGrade(questionVO.getQuestion(), 0);
                mDataManager.getmAnswersVO().getMcAnswers().add(new MultipleChoiceAnswerVO(questionVO.getQuestion(), noCommentChoice));
            }
        }
    }

    public void prepareImagesForUpload(PermissionManager permissionManager){
        mDataManager.prepareImageUploadInBackground()
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(pair -> mDataManager.getFromImageMap(pair.first).setmUploadFilePath(pair.second)
                        , Throwable::printStackTrace
                        , () -> requestInternetPermissionAndConnectServer(permissionManager));
    }

    public void recolorNavigationList(boolean recolor){
        mDataManager.setmRecolorNavigation(recolor);
    }
}
