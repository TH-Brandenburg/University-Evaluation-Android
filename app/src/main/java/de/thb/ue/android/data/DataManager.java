package de.thb.ue.android.data;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.NonNull;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.inject.Inject;

import de.thb.ue.android.data.VOs.Answer;
import de.thb.ue.android.data.VOs.AnswersVO;
import de.thb.ue.android.data.VOs.ChoiceVO;
import de.thb.ue.android.data.VOs.ProcessedResponse;
import de.thb.ue.android.data.VOs.QuestionsVO;
import de.thb.ue.android.data.VOs.SingleChoiceAnswerVO;
import de.thb.ue.android.data.VOs.SingleChoiceQuestionVO;
import de.thb.ue.android.data.VOs.TextAnswerVO;
import de.thb.ue.android.data.VOs.TextQuestionVO;
import de.thb.ue.android.data.local.MappingHelper;
import de.thb.ue.android.data.local.PreferencesHelper;
import de.thb.ue.android.data.remote.RetrofitHelper;
import de.thb.ue.android.injection.ApplicationContext;
import de.thb.ue.android.utility.Connectivity;
import de.thb.ue.android.utility.TestQuestionnaire;
import de.thb.ue.android.utility.eventbus.EventBus;
import de.thb.ue.android.utility.eventbus.events.ConnectivityChangedEvent;
import io.reactivex.Single;
import retrofit2.Response;



public class DataManager implements IDataManager{
    private static final String TAG = "DATA_MANAGER";


    private final PreferencesHelper mPreferencesHelper;
    private final RetrofitHelper mRetrofitHelper;
    private final MappingHelper mMappingHelper;
    private final Context mContext;
    private final Connectivity mConnectivity;
    private final EventBus mEventBus;

    @Inject
    public DataManager(@ApplicationContext Context context, PreferencesHelper preferencesHelper, RetrofitHelper retrofitHelper,
                       MappingHelper mappingHelper , EventBus eventBus, Connectivity connectivity) {
        this.mRetrofitHelper = retrofitHelper;
        this.mPreferencesHelper = preferencesHelper;
        this.mMappingHelper = mappingHelper;
        this.mContext = context;
        this.mConnectivity = connectivity;
        this.mEventBus = eventBus;

        mContext.registerReceiver(new ConncectivityChangedReceiver(), new IntentFilter("android.net.conn.CONNECTIVITY_CHANGE"));
        mContext.registerReceiver(new ConncectivityChangedReceiver(), new IntentFilter("android.net.wifi.WIFI_STATE_CHANGED"));

    }

    public Single<ProcessedResponse<QuestionsVO>> getQuestions(String voteToken, String host){
        return Single.create(e ->{
            mPreferencesHelper.putDeviceId(UUID.randomUUID().toString());
            ProcessedResponse<QuestionsVO> response = new ProcessedResponse<>(200, TestQuestionnaire.getTestQuestionnaire());
            mPreferencesHelper.putQuestionsVO(response.getBody());
            mPreferencesHelper.putAnswersVO(initializeAnswersVO(voteToken));
            e.onSuccess(response);
        });
    }

    public Single<ProcessedResponse<Void>> sendAnswers(){
        return Single.create(e ->{
            ProcessedResponse<Void> vo = new ProcessedResponse<>(200, null);
            e.onSuccess(vo);
        });
    }

    //********Internal Data Operations*****************************

    /**
     * Returns the cached QuestionsVO object for this session.
     * Ensures that it the various lists contained therein are not null.
     *
     * Returns a newly created object when no object was previously stored.
     */
    public QuestionsVO getCachedQuestions(){
        QuestionsVO vo =  mPreferencesHelper.getQuestionsVO();

        if(vo == null){
            return new QuestionsVO(new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), false);
        }

        if (vo.getTextQuestions() == null){
            Log.e("QUESTIONS_VO", "List of text questions was null in cached questionsVO object.");
            vo.setTextQuestions(new ArrayList<>());
        }

        if(vo.getSingleChoiceQuestionVOs() == null) {
            Log.e("QUESTIONS_VO", "List of single choice questions was null in cached questionsVO object.");
            vo.setSingleChoiceQuestionVOs(new ArrayList<>());
        }

        if(vo.getStudyPaths() == null){
            Log.e("QUESTIONS_VO", "List of study paths was null in cached questionsVO object.");
            vo.setStudyPaths(new ArrayList<>());
        }

        return vo;
    }

    public Single<Boolean> putSCAnswer(String question, ChoiceVO answer){
        return Single.create(e -> {
            boolean found = false;

            //retrieve
            AnswersVO answersVO = mPreferencesHelper.getAnswersVO();

            //insert / update
            for(SingleChoiceAnswerVO storedAnswer : answersVO.getScAnswers()) {
                if (storedAnswer.getQuestionText().equals(question)) {
                    storedAnswer.setChoice(answer);
                    found = true;
                }
            }

            if(!found){
                answersVO.getScAnswers().add(new SingleChoiceAnswerVO(question, answer));
            }

            //persist
            mPreferencesHelper.putAnswersVO(answersVO);
            e.onSuccess(true);
        });

    }

    public Single<Boolean> putTextAnswer(String question, int questionId, String answer){
        return Single.create(e -> {
            boolean found = false;

            //retrieve
            AnswersVO answersVO = mPreferencesHelper.getAnswersVO();

            //insert / update
            for(TextAnswerVO storedTextAnswer : answersVO.getTextAnswers()) {
                if (storedTextAnswer.getQuestionText().equals(question)) {
                    storedTextAnswer.setAnswerText(answer);
                    found = true;
                }
            }

            if(!found){
                answersVO.getTextAnswers().add(new TextAnswerVO(questionId ,question, answer));
            }

            //persist
            mPreferencesHelper.putAnswersVO(answersVO);
            e.onSuccess(true);
        });

    }

    public Single<Boolean> putStudyPath(String studyPath){
        return Single.create(e -> {
            //retrieve
            AnswersVO answersVO = mPreferencesHelper.getAnswersVO();

            //insert / update
            answersVO.setStudyPath(studyPath);

            //persist
            mPreferencesHelper.putAnswersVO(answersVO);
            e.onSuccess(true);
        });
    }

    public ChoiceVO getCurrentScAnswer(@NonNull String question){
        AnswersVO answers = mPreferencesHelper.getAnswersVO();

        for(SingleChoiceAnswerVO scAnswer : answers.getScAnswers()){
            if(scAnswer.getQuestionText().equals(question)){
                return scAnswer.getChoice();
            }
        }
        return new ChoiceVO("", (short) 0);
    }

    public String getCurrentTextAnswer(@NonNull String question){
        AnswersVO answers = mPreferencesHelper.getAnswersVO();

        for(TextAnswerVO textAnswer : answers.getTextAnswers()){
            if(textAnswer.getQuestionText().equals(question)){
                return textAnswer.getAnswerText();
            }
        }
        return "";
    }

    public Set<String> getAnsweredQuestions(){
        AnswersVO answers = mPreferencesHelper.getAnswersVO();
        Set<String> result = new HashSet<>();

        for(TextAnswerVO textAnswer : answers.getTextAnswers()){
            if(textAnswer.getAnswerText() != null && !textAnswer.getAnswerText().isEmpty()){
                result.add(textAnswer.getQuestionText());
            }
        }
        for(SingleChoiceAnswerVO scAnswer : answers.getScAnswers()){
            if(!scAnswer.getChoice().getChoiceText().isEmpty()){
                result.add(scAnswer.getQuestionText());
            }
        }

        return result;
    }

    public String getStudyPath(){
        return mPreferencesHelper.getAnswersVO().getStudyPath();
    }

    private AnswersVO initializeAnswersVO(String voteToken){
        QuestionsVO questionsVO = mPreferencesHelper.getQuestionsVO();
        String deviceId = mPreferencesHelper.getDeviceId();

        AnswersVO answersVO = new AnswersVO(voteToken, "", new ArrayList<>(), new ArrayList<>(), deviceId);

        for(TextQuestionVO textQuestion : questionsVO.getTextQuestions()){
            answersVO.getTextAnswers().add(new TextAnswerVO(textQuestion.getQuestionID(), textQuestion.getQuestionText(), ""));
        }

        for(SingleChoiceQuestionVO scQuestion : questionsVO.getSingleChoiceQuestionVOs()){
            answersVO.getScAnswers().add(new SingleChoiceAnswerVO(scQuestion.getQuestion(), new ChoiceVO("", (short) 0)));
        }

        return answersVO;
    }

    @Override
    public boolean isConnected() {
        return mConnectivity.isConnected(mContext);
    }

    /**
     * Removes all data from shared preferences
     */
    public void removeCachedData(){
        mPreferencesHelper.clear();
    }

    @Override
    public List<String> getQuestionTexts() {
        QuestionsVO questions = mPreferencesHelper.getQuestionsVO();
        List<String> result = new ArrayList<>();

        if(questions.getTextQuestionsFirst()){
            for(TextQuestionVO textQuestion : questions.getTextQuestions()){
                result.add(textQuestion.getQuestionText());
            }
            for(SingleChoiceQuestionVO scQuestion : questions.getSingleChoiceQuestionVOs()){
                result.add(scQuestion.getQuestion());
            }
        } else {
            for(SingleChoiceQuestionVO scQuestion : questions.getSingleChoiceQuestionVOs()){
                result.add(scQuestion.getQuestion());
            }
            for(TextQuestionVO textQuestion : questions.getTextQuestions()){
                result.add(textQuestion.getQuestionText());
            }
        }
        return result;
    }

    private <T,V> ProcessedResponse<V> handleResponse(Response<T> response, V body, int expectedCode) throws IllegalAccessException, InstantiationException {
        if(expectedCode == response.code()){
            return new ProcessedResponse<>(response.code(), body);
        } else {
            return new ProcessedResponse<>(response.code(), body, response.message());
        }
    }

    private <T> Single<ProcessedResponse<T>> handleCache(T cachedObject, Class objectType){
        if (cachedObject != null){
            return Single.create(e -> e.onSuccess(new ProcessedResponse<>(200, cachedObject)));
        } else if (objectType != Void.TYPE){
            return Single.create(e -> e.onSuccess(new ProcessedResponse<>(400, (T) objectType.newInstance(), "No cached instance found")));
        } else {
            return Single.create(e -> e.onSuccess(new ProcessedResponse<>(400, null, "No cached instance found")));
        }
    }

    //*************************Observers************************************

    private class ConncectivityChangedReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            mEventBus.post(new ConnectivityChangedEvent(
                    mConnectivity.isConnected(mContext)
                    , mConnectivity.isConnectedMobile(mContext)
                    , mConnectivity.isConnectedWifi(mContext)
                    , mConnectivity.isConnectedFast(mContext)));
        }
    }

    //*************************Getter & Setter & Event Throwing ************

}