package de.fhb.campusapp.eval.data;


import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.util.Pair;
import android.util.Log;

import com.google.common.collect.Iterables;

import org.joda.time.Instant;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import javax.inject.Inject;

import de.fhb.ca.dto.QuestionsDTO;
import de.fhb.ca.dto.ResponseDTO;
import de.fhb.campusapp.eval.data.local.PreferencesHelper;
import de.fhb.campusapp.eval.data.local.RetrofitHelper;
import de.fhb.campusapp.eval.injection.ApplicationContext;
import de.fhb.campusapp.eval.utility.ClassMapper;
import de.fhb.campusapp.eval.utility.Utility;
import de.fhb.campusapp.eval.utility.eventpipelines.AppEventPipelines;
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
    private boolean mPageChanged;

    private final PreferencesHelper mPreferencesHelper;
    private final RetrofitHelper mRetrofitHelper;
    private final NetworkEventPipelines mNetworkEventPipelines;
    private final AppEventPipelines mAppEventPipelines;
    private final Context mContext;


    @Inject
    public DataManager(PreferencesHelper preferencesHelper, RetrofitHelper retrofitHelper,  @ApplicationContext Context context
            , NetworkEventPipelines networkEventPipelines, AppEventPipelines appEventPipelines) {
        this.mRetrofitHelper = retrofitHelper;
        this.mPreferencesHelper = preferencesHelper;
        this.mNetworkEventPipelines = networkEventPipelines;
        this.mAppEventPipelines = appEventPipelines;
        this.mContext = context;

        this.mAnswersVO = new AnswersVO("", "", new ArrayList<>(), new ArrayList<>(), "");
        this.mImageMap = new HashMap<>();
        this.mGallerySet = new HashSet<>();
    }

    //********Networking******************************************

    public void initAndObserveQuestionRequest(){
        mRetrofitHelper.performGetQuestionsRequest(mAnswersVO, mUuid, mHostName)
                .subscribeOn(Schedulers.io())
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

    public void initAndObserveAnswersResponse(){
        mRetrofitHelper.performPostAnswersRequest(mHostName, mAnswersVO, mImageMap, mContext)
            .subscribeOn(Schedulers.io())
            .subscribe(new Subscriber<Response<ResponseDTO>>() {
                @Override
                public void onCompleted() {

                }

                @Override
                public void onError(Throwable t) {
                    mNetworkEventPipelines.broadcastNetworkError(
                            mRetrofitHelper.processNetworkError(t));
                }

                @Override
                public void onNext(Response<ResponseDTO> response) {
                    if(response.isSuccessful()){
                        mNetworkEventPipelines.broadcastResponseDTO(response.body());
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
     * Weather a text question was answered before.
     * Tests for both text answers and image answers
     * @param question text of the question whose answer should be searched for
     * @return The AnswerDTO that stored the given question or null if none was found.
     */
    public boolean isTextQuestionAnswered(String question){
        TextAnswerVO textAnswer = retrieveTextAnswerVO(question);
        return (textAnswer != null
                && !textAnswer.getAnswerText().isEmpty())
                || mImageMap.get(question) != null;
    }

    /**
     * Returns TextAnswerVO specified by question text
     * @param question text of the question whose object should be returned
     * @return The TextAnswerVO that stored the given question or null if none was found.
     */
    public TextAnswerVO retrieveTextAnswerVO(String question){
        List<TextAnswerVO> textAnswers = mAnswersVO.getTextAnswers();

        for(int i = 0; i < textAnswers.size(); i++){
            if(textAnswers.get(i).getQuestionText().equals(question)){
                return textAnswers.get(i);
            }
        }

        return null;

//
//        return Iterables.tryFind(mAnswersVO.getTextAnswers(),
//                textAnswer -> textAnswer.getQuestionText().equals(question)).orNull();
    }


    /**
     * Weather a mc question was answered before.
     * @param question text of the question whose answer should be searched for
     * @return the MultipleChoiceAnswerDTO that stored the given question or null if none was found.
     */
    public boolean isMcQuestionAnswered(String question){
        MultipleChoiceAnswerVO vo = retrieveMcAnswer(question);
        return vo != null && !vo.getChoice().getChoiceText().isEmpty();
    }

    /**
     * Returns a specific MultipleChoiceAnswerVO identified by question text
     * Loops through the list of answered multiple choice questions stored in AnswersVO.
     *
     * @param question question text used to identify correct object
     * @return
     */
    public MultipleChoiceAnswerVO retrieveMcAnswer(String question){
        List<MultipleChoiceAnswerVO> mcAnswers = mAnswersVO.getMcAnswers();

        for(int i = 0; i < mcAnswers.size(); i++){
            if(mcAnswers.get(i).getQuestionText().equals(question)){
                return mcAnswers.get(i);
            }
        }

        return null;

//
//        return Iterables.tryFind(mAnswersVO.getMcAnswers(),
//                mcAnswer -> mcAnswer.getQuestionText().equals(question)).orNull();
    }

    /**
     * Returns a specific ChoiceVO identified by question text.
     * A provided MultipleChoiceQuestionVO is searched through.
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
     * Returns a specific ChoiceVO identified by text.
     * Loops through all MultipleChoiceQuestionsVOs stored in QuestionVO
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

        //retrieve the choiceVO matching the given grade, null if none was found
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
     * @param question question that should be tested
     * @return true if question was answered in any valid way
     */
    public boolean isQuestionAnswered(String question){
        boolean answered = false; // default is no
        if(question != null){
            answered = isMcQuestionAnswered(question);
            if(!answered){  //only test further if previous test failed
                answered =  isTextQuestionAnswered(question);
            }
        }
        return answered;
    }

    public boolean isTextQuestion(String question){
        return retrieveTextQuestionVO(question) != null;
    }

    public boolean isMcQuestion(String question){
        return retrieveMcAnswer(question) != null;
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

    //*************************Observers************************************

    /**
     *
     * @return
     */
    @Override
    public Observable<Pair<String, String>> prepareImageUploadInBackground(){

        Observable<Pair<String, String>> observable = Observable.create(new Observable.OnSubscribe<Pair<String, String>>(){
            @Override
            public void call(Subscriber<? super Pair<String, String>> subscriber) {

                //loop through all text questions and test if their is an entry in
                //the commentaryImageMap. If a match is found use the path to the original image
                //to compress it and store the path to the other image paths
                for(TextQuestionVO textQuestionVO : getmQuestionsVO().getTextQuestions()){
                    if(mImageMap.containsKey(textQuestionVO.getQuestionText())){
                        ImageDataVO pathsObj = mImageMap.get(textQuestionVO.getQuestionText());
                        try {
                            File uploadFile = Utility.createImageFile(Utility.removeSpecialCharacters(textQuestionVO.getQuestionText()), mContext);
                            Bitmap resizedImage = Utility.resizeImage(pathsObj.getmLargeImageFilePath(), 800, 768);
                            FileOutputStream os = new FileOutputStream(uploadFile);
                            resizedImage.compress(Bitmap.CompressFormat.JPEG, 70, os);
                            os.close();
                            subscriber.onNext(new Pair<>(textQuestionVO.getQuestionText(), uploadFile.getPath()));
                        } catch (IOException e) {
                            subscriber.onError(e);
                        }
                    }
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
        return observable;
    }

    /**
     *
     * @param pathObj
     * @return
     */
    public Observable<Void> deleteImagePairInBackground(final ImageDataVO pathObj){

        Observable<Void> observable = Observable.create(new Observable.OnSubscribe<Void>(){
            @Override
            public void call(Subscriber<? super Void> subscriber) {
                if(pathObj.getmThumbnailFilePath() != null){
                    File thumbnail = new File(pathObj.getmThumbnailFilePath());
                    thumbnail.delete();
                }
                if(pathObj.getmLargeImageFilePath() != null){
                    File largeImage = new File(pathObj.getmLargeImageFilePath());
                    largeImage.delete();
                }
                if(pathObj.getmUploadFilePath() != null){
                    File uploadImage = new File(pathObj.getmUploadFilePath());
                    uploadImage.delete();
                }
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.io());
        return observable;
    }

    //*************************Getter & Setter & Event Throwing ************


    public void broadcastBeforeServerCommunication(){
        mAppEventPipelines.broadcastBeforeServerCoomunication();
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

    private HashMap<String, ImageDataVO> getmImageMap() {
        return mImageMap;
    }

    public ImageDataVO getFromImageMap(String key){
        return mImageMap.get(key);
    }

    public ImageDataVO removeFromImageMap(String key){
       return mImageMap.remove(key);
    }

    public boolean isInImageMap(String key){
        return mImageMap.containsKey(key);
    }

    public ImageDataVO putIntoImageMap(String key, ImageDataVO value){
        Log.d("INFO", "New Image path: " + value.getmLargeImageFilePath());
        mAppEventPipelines.broadcastPictureTaken(value);
        return mImageMap.put(key, value);
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
        Log.d("INFO", "Current Question: " + mCurrentQuestion);
        this.mCurrentQuestion = mCurrentQuestion;
    }

    public int getmCurrentPagerPosition() {
        return mCurrentPagerPosition;
    }

    public void setmCurrentPagerPosition(int newPosition) {
        Log.d("INFO", "Current PagerPosition: " + newPosition);
        mAppEventPipelines.broadcastFirstPagingEvent(newPosition, mCurrentPagerPosition );
        this.mCurrentPagerPosition = newPosition;
    }

    public Instant getmAppStartTime() {
        return mAppStartTime;
    }

    public void setmAppStartTime(Instant mAppStartTime) {
        this.mAppStartTime = mAppStartTime;
    }

    public void broadcastSecondPagingEvent() {
        mAppEventPipelines.broadcastSecondPagingEvent();
    }
}