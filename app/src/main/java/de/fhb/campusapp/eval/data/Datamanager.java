package de.fhb.campusapp.eval.data;


import com.google.common.collect.Iterables;
import com.squareup.otto.Subscribe;

import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import de.fhb.ca.dto.QuestionsDTO;
import de.fhb.campusapp.eval.data.local.PreferencesHelper;
import de.fhb.campusapp.eval.data.local.RetrofitHelper;
import de.fhb.campusapp.eval.utility.ClassMapper;
import de.fhb.campusapp.eval.utility.Events.NetworkErrorEvent;
import de.fhb.campusapp.eval.utility.Events.RequestErrorEvent;
import de.fhb.campusapp.eval.utility.Events.RequestSuccessEvent;
import de.fhb.campusapp.eval.utility.eventpipelines.NetworkEventPipelines;
import de.fhb.campusapp.eval.utility.vos.AnswersVO;
import de.fhb.campusapp.eval.utility.vos.ChoiceVO;
import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceAnswerVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceQuestionVO;
import de.fhb.campusapp.eval.utility.vos.QuestionsVO;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;
import de.fhb.campusapp.eval.utility.vos.TextQuestionVO;
import retrofit2.Response;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class DataManager implements IDataManager{



    /**
        Defines how many fragments are placed in the pageViewer BEFORE the start of the questionnaire.
        Currently only one: The InnerSectionFragment. Increment this variable if you want to add more.
    */
    private static final int POSITION_OFFSET = 1;

    private QuestionsVO mQuestionsVO;
    private AnswersVO mAnswersVO;
    private String mUuid;
    private String mHostName;
    private HashMap<String, ImageDataVO> mImageMap;
    private boolean mRecolorNavigation = false;
    private HashSet<String> mGallerySet;
    private String mCurrentQuestion;
    private int mCurrentPagerPosition = -1;
    private Instant mAppStartTime;

    private final PreferencesHelper mPreferencesHelper;
    private final RetrofitHelper mRetrofitHelper;
    private final NetworkEventPipelines mNetworkEventPipelines;


    @Inject
    public DataManager(PreferencesHelper preferencesHelper, RetrofitHelper retrofitHelper, NetworkEventPipelines networkEventPipelines) {
        this.mRetrofitHelper = retrofitHelper;
        this.mPreferencesHelper = preferencesHelper;
        this.mNetworkEventPipelines = networkEventPipelines;

        this.mAnswersVO = new AnswersVO("", "", new ArrayList<>(), new ArrayList<>(), "");
    }

    //********Networking******************************************

    public void initAndObserveQuestionRequest(){
        mRetrofitHelper.performGetQuestionsRequest(mAnswersVO, mUuid, mHostName)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Subscriber<Response<QuestionsDTO>>() {
                    @Override
                    public void onCompleted() {
                        //do nothing
                    }

                    @Override
                    public void onError(Throwable t) {
                        mNetworkEventPipelines.broadcastNetworkError(
                                mRetrofitHelper.processNetworkError(t));
                    }

                    @Override
                    public void onNext(Response<QuestionsDTO> response) {
                        if(response.isSuccessful()){
                            mQuestionsVO = ClassMapper.questionsDTOToQuestionsVOMapper(response.body());
                            mNetworkEventPipelines.broadcastQuestionsVO(mQuestionsVO);
                        } else {
                            mNetworkEventPipelines.broadcastRequestError(
                                    mRetrofitHelper.processRequestError(response));
                        }
                    }
                });
    }


    //********Internal Data Operations*****************************

    /**
        Defines ho many fragments are placed in the pageViewer BEFORE the start of the questionnaire.
        Currently only one: The InnerSectionFragment. Increment this variable if you want to add more.
    */
    public int getPositionOffset() {
        return POSITION_OFFSET;
    }

    /**
     * Weather a question was answered before.
     * Loops through the list of answered text questions stored in AnswersDTO
     * @param question text of the question whose answer should be searched for
     * @return The AnswerDTO that stored the given question or null if none was found.
     */
    public boolean isTextQuestionAnswered(String question){
        return retrieveTextQuestionVO(question) != null;
    }

    /**
     * Weather a question was answered before.
     * Loops through the list of answered text questions stored in AnswersDTO
     * @param question text of the question whose answer should be searched for
     * @return The AnswerDTO that stored the given question or null if none was found.
     */
    public TextAnswerVO retrieveTextAnswerVO(String question){
        return Iterables.tryFind(mPreferencesHelper.getAnswersVO().getTextAnswers(),
                textAnswer -> textAnswer.getQuestionText().equals(question)).orNull();
    }


    /**
     * Weather a question was answered before.
     * @param question text of the question whose answer should be searched for
     * @return the MultipleChoiceAnswerDTO that stored the given question or null if none was found.
     */
    public boolean isMcQuestionAnswered(String question){
        return retrieveMcQuestion(question) != null;
    }

    /**
     * Returns a specific MultipleChoiceAnswerVO identified by question text
     * Loops through the list of answered multiple choice questions stored in AnswersDTO
     * @param question question text used to identify correct object
     * @return
     */
    public MultipleChoiceAnswerVO retrieveMcQuestion(String question){
        return Iterables.tryFind(mPreferencesHelper.getAnswersVO().getMcAnswers(),
                mcAnswer -> mcAnswer.getQuestionText().equals(question)).orNull();
    }

    /**
     * Returns a specific ChoiceDTO identified by question text.
     * A provided MultipleChoiceQuestionDTO is searched through.
     * @param choiceText
     * @return
     */
    public ChoiceVO retrieveChoiceVO(MultipleChoiceQuestionVO questionVO, String choiceText){
        for(ChoiceVO choiceVO : questionVO.getChoices()){
            if(choiceVO.getChoiceText().equals(choiceText)){
                return choiceVO;
            }
        }
        return null;
    }

    /**
     * Returns a specific ChoiceDTO identified by text.
     * Loops through all MultipleChoiceQuestionsDTOs stored in QuestionDTO
     * until it found the one which is identified by question parameter
     * @param choiceText
     * @param question
     * @return
     */
    public ChoiceVO retrieveChoiceVOByQuestionText(String question, String choiceText){
        //find mcChoiceQuestionDTO that matches with given question
        MultipleChoiceQuestionVO matchingVO =
                Iterables.tryFind(getmQuestionsVO().getMultipleChoiceQuestionVOs()
                        , mcQuestion -> mcQuestion.getQuestion().equals(question)).orNull();

        //retrieve the choiceDTO matching the given grade, null if none was found
        return Iterables.tryFind(matchingVO.getChoices(), choice -> choice.getChoiceText().equals(choiceText)).orNull();
    }

    /**
     * Returns a ChoiceDTO whose grade property matches the given parameter grade.
     * Returns null if no ChoiceDTO with given grade was found.
     * @param question
     * @param grade
     * @return
     */
    public ChoiceVO retrieveChoiceByGrade(String question, int grade){
        //find mcChoiceQuestionDTO that matches with given question
        MultipleChoiceQuestionVO matchingVO =
                Iterables.tryFind(getmQuestionsVO().getMultipleChoiceQuestionVOs(), mcQuestion -> mcQuestion.getQuestion().equals(question)).orNull();

        //retrieve the choiceDTO matching the given grade, null if none was found
        return Iterables.tryFind(matchingVO.getChoices(), choice -> choice.getGrade() == grade).orNull();
    }

    /**
     *
     * @param question
     * @return
     */
    public TextQuestionVO retrieveTextQuestionVO(String question){
        return Iterables.tryFind(getmQuestionsVO().getTextQuestions(),
                textQuestion -> textQuestion.getQuestionText().equals(question)).orNull();
    }

    /**
     * Weather a given question is answered or not. The type of given question does not matter.
     * All types will be searched with all possibilities to answer a question.
     * @param question questions that should be tested
     * @return true is questions was answered in any valid way
     */
    public boolean isQuestionAnswered(String question){
        boolean answered = false; // default is no
        if(question != null){
            answered = isMcQuestionAnswered(question); // if object is returned question is answered
            if(!answered){  //only test further if previous test failed
                answered = (isTextQuestionAnswered(question)
                        && !retrieveTextAnswerVO(question).getAnswerText().equals(""));
            }
            if(!answered){
                answered = getmImageMap().get(question) != null;
            }
        }
        return answered;
    }

    /**
     * Creates a list of all questions. It ensures that the returned
     * list is ordered in such a way that it matches how the questions are displayed in the app
     * @return ordered list of question texts
     */
    public List<String> retrieveAllQuestionTexts(){
        ArrayList<String> questions = new ArrayList<>(getTextQuestionTexts().size()
                + getMCQuestionTexts().size());

        if(getmQuestionsVO().getTextQuestionsFirst()){
            // textQuestions at the beginning
            for (TextQuestionVO question : getTextQuestionTexts()) {
                questions.add(question.getQuestionText());
            }
            for (MultipleChoiceQuestionVO questionVO : getMCQuestionTexts()) {
                questions.add(questionVO.getQuestion());
            }
        } else {
            // mcQuestions at the beginning
            for (MultipleChoiceQuestionVO questionVO : getMCQuestionTexts()) {
                questions.add(questionVO.getQuestion());
            }

            for (TextQuestionVO question : getTextQuestionTexts()) {
                questions.add(question.getQuestionText());
            }
        }
        return questions;
    }

    /**
     * Saves all data to shared preferences.
     */
    public void saveAllData() {
        mPreferencesHelper.putAnswersVO(mAnswersVO);
        mPreferencesHelper.putAppStartTime(mAppStartTime);
        mPreferencesHelper.putCurrentPagerPosition(mCurrentPagerPosition);
        mPreferencesHelper.putCurrentQuestion(mCurrentQuestion);
        mPreferencesHelper.putGallerySet(mGallerySet);
        mPreferencesHelper.putHostName(mHostName);
        mPreferencesHelper.putImageMap(mImageMap);
        mPreferencesHelper.putRecolorNavigation(mRecolorNavigation);
        mPreferencesHelper.putUUID(mUuid);
        mPreferencesHelper.putQuestionsVO(mQuestionsVO);
    }

    /**
     * Retrieves all data stored in shared preferences and puts them into variables
     * of Datamanager.
     */
    public void restoreAllData(){
        mAnswersVO = mPreferencesHelper.getAnswersVO();
        mQuestionsVO = mPreferencesHelper.getQuestionsVO();
        mAppStartTime = mPreferencesHelper.getAppStartTime();
        mCurrentPagerPosition = mPreferencesHelper.getCurrentPagerPosition();
        mCurrentQuestion = mPreferencesHelper.getCurrentQuestion();
        mGallerySet = mPreferencesHelper.getGallerySet();
        mHostName = mPreferencesHelper.getHostName();
        mImageMap = mPreferencesHelper.getImageMap();
        mRecolorNavigation = mPreferencesHelper.getRecolorNavigation();
        mUuid = mPreferencesHelper.getUUID();
    }

    /**
     * Removes all data from shared preferences
     */
    public void removeAllData(){
        mPreferencesHelper.removeAnswersVO();
        mPreferencesHelper.removeAppStartTime();
        mPreferencesHelper.removeCurrentPagerPosition();
        mPreferencesHelper.removeCurrentQuestion();
        mPreferencesHelper.removeGalleryList();
        mPreferencesHelper.removeHostName();
        mPreferencesHelper.removeRecolorNavigationList();
        mPreferencesHelper.removeUUID();
        mPreferencesHelper.removeQuestionsVO();
        mPreferencesHelper.removeImageMap();
    }

    public QuestionsVO getmQuestionsVO() {
        return mQuestionsVO;
    }

    public List<TextQuestionVO> getTextQuestionTexts(){
        if(mQuestionsVO != null && mQuestionsVO.getTextQuestions() != null){
            return mQuestionsVO.getTextQuestions();
        }
        return null;
    }

    public List<MultipleChoiceQuestionVO> getMCQuestionTexts(){
        if(mQuestionsVO != null && mQuestionsVO.getMultipleChoiceQuestionVOs() != null){
            return getmQuestionsVO().getMultipleChoiceQuestionVOs();
        }
        return null;
    }

    public void setmQuestionsVO(QuestionsVO mQuestionsVO) {
        this.mQuestionsVO = mQuestionsVO;
    }

    public AnswersVO getmAnswersVO() {
        return mAnswersVO;
    }

    public void setmAnswersVO(AnswersVO mAnswersVO) {
        this.mAnswersVO = mAnswersVO;
    }

    public String getmUuid() {
        return mUuid;
    }

    public void setmUuid(String mUuid) {
        this.mUuid = mUuid;
    }

    public String getmHostName() {
        return mHostName;
    }

    public void setmHostName(String mHostName) {
        this.mHostName = mHostName;
    }

    public HashMap<String, ImageDataVO> getmImageMap() {
        return mImageMap;
    }

    public void setmImageMap(HashMap<String, ImageDataVO> mImageMap) {
        this.mImageMap = mImageMap;
    }

    public boolean ismRecolorNavigation() {
        return mRecolorNavigation;
    }

    public void setmRecolorNavigation(boolean mRecolorNavigation) {
        this.mRecolorNavigation = mRecolorNavigation;
    }

    public HashSet<String> getmGallerySet() {
        return mGallerySet;
    }

    public void setmGallerySet(HashSet<String> mGallerySet) {
        this.mGallerySet = mGallerySet;
    }

    public String getmCurrentQuestion() {
        return mCurrentQuestion;
    }

    public void setmCurrentQuestion(String mCurrentQuestion) {
        this.mCurrentQuestion = mCurrentQuestion;
    }

    public int getmCurrentPagerPosition() {
        return mCurrentPagerPosition;
    }

    public void setmCurrentPagerPosition(int mCurrentPagerPosition) {
        this.mCurrentPagerPosition = mCurrentPagerPosition;
    }

    public Instant getmAppStartTime() {
        return mAppStartTime;
    }

    public void setmAppStartTime(Instant mAppStartTime) {
        this.mAppStartTime = mAppStartTime;
    }


   /* *//**
     *
     * @return
     *//*
    public QuestionsVO getmQuestionsVO() {
        if(mQuestionsVO == null){
           mQuestionsVO = retrieveFromStorage(QUESTIONS_VO_KEY, QuestionsVO.class);
        }
        if(mQuestionsVO == null){
            mQuestionsVO = new QuestionsVO(new ArrayList<String>()
                    , new ArrayList<TextQuestionVO>()
                    , new ArrayList<MultipleChoiceQuestionVO>()
                    , false);
            storeToStorage(QUESTIONS_VO_KEY, mQuestionsVO);
        }
        return mQuestionsVO;
    }

    *//**
     *
     * @param mQuestionsVO
     *//*
    public void setmQuestionsVO(QuestionsVO mQuestionsVO) {
        if(mQuestionsVO == null){
            DataManager.mQuestionsVO = new QuestionsVO(new ArrayList<String>()
                    , new ArrayList<TextQuestionVO>()
                    , new ArrayList<MultipleChoiceQuestionVO>()
                    , false);
            storeToStorage(QUESTIONS_VO_KEY, mQuestionsVO);
        } else {
            this.mQuestionsVO = mQuestionsVO;
        }
    }

    *//**
     *
     * @return
     *//*
    public List<TextQuestionVO> getQuestionTexts() {
        if(getmQuestionsVO() != null && getmQuestionsVO().getTextQuestions() != null){
            return getmQuestionsVO().getTextQuestions();
        } else {
            return null;
        }
    }

    *//**
     *
     * @return
     *//*
    public List<MultipleChoiceQuestionVO> getMCQuestionTexts() {
        if(getmQuestionsVO() != null && getmQuestionsVO().getMultipleChoiceQuestionVOs() != null){
            return getmQuestionsVO().getMultipleChoiceQuestionVOs();
        } else {
            return null;
        }
    }

    *//**
     *
     * @param preferences
     *//*
    public void setPreferences(SharedPreferences preferences) {
        DataManager.preferences = preferences;
    }

    *//**
     *
     * @return
     *//*
    public boolean isRecolorNavigationList() {
        Object obj = retrieveFromStorage(RECOLOR_NAVIGATION_LIST, Boolean.class);
        if (obj != null){
            mRecolorNavigation = (boolean) obj;
        }
        return mRecolorNavigation;
    }

    *//**
     *
     * @param mRecolorNavigation
     *//*
    public void setRecolorNavigationList(boolean mRecolorNavigation) {
        storeToStorage(RECOLOR_NAVIGATION_LIST, mRecolorNavigation);
        DataManager.mRecolorNavigation = mRecolorNavigation;
    }

    *//**
     *
     * @return
     *//*
    public String getUuid() {
        if(mUuid == null){
            mUuid = retrieveFromStorage(UUID_KEY, String.class);
        }
        return mUuid;
    }

    *//**
     *
     * @param mUuid
     *//*
    public void setUuid(String mUuid){
        if(mUuid != null){
            storeToStorage(UUID_KEY, mUuid);
        } else {
            removeFromStorage(UUID_KEY);
        }
        DataManager.mUuid = mUuid;
    }

    *//**
     *
     * @return
     *//*
    public String getHostName() {
        if(mHostName == null){
            mHostName = retrieveFromStorage(HOST_NAME_KEY, String.class);
        }
        return mHostName;
    }

    *//**
     *
     * @param mHostName
     *//*
    public void setHostName(String mHostName) {
        if(mHostName != null){
            storeToStorage(HOST_NAME_KEY, mHostName);
        } else {
            removeFromStorage(HOST_NAME_KEY);
        }
        DataManager.mHostName = mHostName;
    }

    *//**
     *
     * @return
     *//*
    public HashMap<String, ImageDataVO> getCommentaryImageMap() {
        if(mImageMap == null){
//            mImageMap =  retrieveFromStorage(IMAGE_MAP_KEY, HashMap.class);
            mImageMap = retrieveComplexType(IMAGE_MAP_KEY, MAP_TYPE, HashMap.class, String.class, ImageDataVO.class);
            if(mImageMap == null){
                mImageMap = new HashMap<>();
            }
        }
        return mImageMap;
    }

    *//**
     *
     * @param mImageMap
     *//*
    public void setCommentaryImageMap(HashMap<String, ImageDataVO> mImageMap) {
        if(mImageMap != null){
            storeToStorage(IMAGE_MAP_KEY, mImageMap);
        } else {
            removeFromStorage(IMAGE_MAP_KEY);
        }
        DataManager.mImageMap = mImageMap;
    }

    public Instant getmAppStartTime() {
        if(mAppStartTime == null){
            DataManager.mAppStartTime = DataManager.retrieveFromStorage(APP_STARTING_TIME_KEY, Instant.class);
        }
        return mAppStartTime;
    }

    public void setmAppStartTime(Instant mAppStartTime) {
        if(mAppStartTime != null){
            storeToStorage(APP_STARTING_TIME_KEY, mAppStartTime);
        } else {
            removeFromStorage(APP_STARTING_TIME_KEY);
        }
        DataManager.mAppStartTime = mAppStartTime;
    }

    public HashSet<String> getGallerySet() {
        if(mGallerySet == null) {
//            mGallerySet = retrieveFromStorage(GALLERY_LIST_KEY, HashSet.class);
            mGallerySet = retrieveComplexType(GALLERY_LIST_KEY, COLLECTION_TYPE, HashSet.class, String.class);
            if (mGallerySet == null) {
                mGallerySet = new HashSet<>();
            }
        }
        return mGallerySet;
    }

    public void setGallerySet(HashSet<String> mGallerySet) {
        if(mGallerySet != null){
            storeToStorage(GALLERY_LIST_KEY, mGallerySet);
        } else {
            removeFromStorage(GALLERY_LIST_KEY);
        }
        DataManager.mGallerySet = mGallerySet;
    }

    public String getCurrentQuestion() {
        if(mCurrentQuestion == null){
            mCurrentQuestion = retrieveFromStorage(CURRENT_QUESTION, String.class);
        }

        return mCurrentQuestion;
    }

    public void setCurrentQuestion(String mCurrentQuestion) {
        if(mCurrentQuestion != null){
            storeToStorage(CURRENT_QUESTION, mCurrentQuestion);
        } else {
            removeFromStorage(CURRENT_QUESTION);
        }

        DataManager.mCurrentQuestion = mCurrentQuestion;
    }

    public int getmCurrentPagerPosition() {
        if(mCurrentPagerPosition == -1){
            mCurrentPagerPosition = retrieveFromStorage(CURRENT_PAGER_POSITION, Integer.class);
        }

        return mCurrentPagerPosition;
    }

    public void setmCurrentPagerPosition(int mCurrentPagerPosition) {
        storeToStorage(CURRENT_PAGER_POSITION, mCurrentPagerPosition);
        DataManager.mCurrentPagerPosition = mCurrentPagerPosition;
    }

*/
}