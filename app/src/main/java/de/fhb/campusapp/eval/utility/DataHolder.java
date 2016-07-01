package de.fhb.campusapp.eval.utility;


import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.joda.time.Instant;
import org.roboguice.shaded.goole.common.collect.Iterables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.fhb.ca.dto.MultipleChoiceQuestionDTO;
import de.fhb.ca.dto.util.ChoiceDTO;
import de.fhb.campusapp.eval.utility.vos.AnswersVO;
import de.fhb.campusapp.eval.utility.vos.ChoiceVO;
import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceAnswerVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceQuestionVO;
import de.fhb.campusapp.eval.utility.vos.QuestionsVO;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;
import de.fhb.campusapp.eval.utility.vos.TextQuestionVO;

public class DataHolder {

    private static final String QUESTIONS_VO_KEY = "QUESTIONS_VO_KEY";
    private static final String ANSWER_VO_KEY = "ANSWER_VO_KEY";
    private static final String UUID_KEY = "UUID_KEY";
    private static final String HOST_NAME_KEY = "HOST_NAME_KEY";
    private static final String IMAGE_MAP_KEY = "IMAGE_MAP_KEY";
    private static final String RECOLOR_NAVIGATION_LIST = "RECOLOR_NAVIGATION_LIST";
    private static final String APP_STARTING_TIME_KEY = "APP_STARTING_TIME";
    private static final String GALLERY_LIST_KEY = "GALLERY_LIST_KEY";

    /*
        Defines ho many fragments are placed in the pageViewer BEFORE the start of the questionnaire.
        Currently only one: The InnerSectionFragment. Increment this variable if you want to add more.
         */
    private static final int POSITION_OFFSET = 1;

    private static QuestionsVO questionsVO;
    private static AnswersVO answersVO;
    private static String uuid;
    private static String hostName;
    private static HashMap<String, ImageDataVO> commentaryImageMap;
    private static boolean recolorNavigationList = false;
    private static HashSet<String> galleryList;


    private static Instant appStart;


    private static ObjectMapper mapper;
    private static SharedPreferences preferences;


    /**
        Defines ho many fragments are placed in the pageViewer BEFORE the start of the questionnaire.
        Currently only one: The InnerSectionFragment. Increment this variable if you want to add more.
    */
    public static int getPositionOffset() {
        return POSITION_OFFSET;
    }

    /**
     *
     * @return
     */
    public static AnswersVO getAnswersVO() {
        if(answersVO == null){
            answersVO = retrieveFromStorage(ANSWER_VO_KEY, AnswersVO.class);
        }
        if(answersVO == null){
            answersVO = new AnswersVO("", "", new ArrayList<TextAnswerVO>(), new ArrayList<MultipleChoiceAnswerVO>(), "");
            storeToStorage(ANSWER_VO_KEY, answersVO);
        }
        return answersVO;
    }

    /**
     * Weather a question was answered before.
     * Loops through the list of answered text questions stored in AnswersDTO
     * @param question text of the question whose answer should be searched for
     * @return The AnswerDTO that stored the given question or null if none was found.
     */
    public static TextAnswerVO isTextQuestionAnswered(String question){
        return Iterables.tryFind(DataHolder.getAnswersVO().getTextAnswers(),
                textAnswer -> textAnswer.getQuestionText().equals(question)).orNull();
    }

    /**
     * Weather a question was answered before.
     * Loops through the list of answered multiple choice questions stored in AnswersDTO
     * @param question text of the question whose answer should be searched for
     * @return the MultipleChoiceAnswerDTO that stored the given question or null if none was found.
     */
    public static MultipleChoiceAnswerVO isMcQuestionAnswered(String question){
        return Iterables.tryFind(DataHolder.getAnswersVO().getMcAnswers(),
                mcAnswer -> mcAnswer.getQuestionText().equals(question)).orNull();
    }

    /**
     * Returns a specific ChoiceDTO identified by text.
     * A provided MultipleChoiceQuestionDTO is searched through.
     * @param choiceText
     * @return
     */
    public static ChoiceDTO retrieveChoiceDTO(MultipleChoiceQuestionDTO questionDTO, String choiceText){
        for(ChoiceDTO choiceDTO : questionDTO.getChoices()){
            if(choiceDTO.getChoiceText().equals(choiceText)){
                return choiceDTO;
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
    public static ChoiceVO retrieveChoiceVO(String question, String choiceText){
        //find mcChoiceQuestionDTO that matches with given question
        MultipleChoiceQuestionVO matchingVO =
                Iterables.tryFind(DataHolder.getQuestionsVO().getMultipleChoiceQuestionVOs(), mcQuestion -> mcQuestion.getQuestion().equals(question)).orNull();

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
    public static ChoiceVO retrieveChoiceByGrade(String question, int grade){
        //find mcChoiceQuestionDTO that matches with given question
        MultipleChoiceQuestionVO matchingVO =
                Iterables.tryFind(DataHolder.getQuestionsVO().getMultipleChoiceQuestionVOs(), mcQuestion -> mcQuestion.getQuestion().equals(question)).orNull();

        //retrieve the choiceDTO matching the given grade, null if none was found
        return Iterables.tryFind(matchingVO.getChoices(), choice -> choice.getGrade() == grade).orNull();
    }

    /**
     *
     * @param question
     * @return
     */
    public static TextQuestionVO retrieveTextQuestionVO(String question){
        return Iterables.tryFind(DataHolder.getQuestionsVO().getTextQuestions(),
                textQuestion -> textQuestion.getQuestionText().equals(question)).orNull();
    }

    /**
     * Weather a given question is answered or not. The type of given question does not matter.
     * All types will be searched with all possibilities to answer a question.
     * @param question questions that should be tested
     * @return true is questions was answered in any valid way
     */
    public static boolean isQuestionAnswered(String question){
        boolean answered = false; // default is no
        if(question != null){
            answered = DataHolder.isMcQuestionAnswered(question) != null; // if object is returned question is answered
            if(!answered){  //only test further if previous test failed
                answered = (DataHolder.isTextQuestionAnswered(question) != null //
                        && !DataHolder.isTextQuestionAnswered(question).getAnswerText().equals(""));
            }
            if(!answered){
                answered = DataHolder.getCommentaryImageMap().get(question) != null;
            }
        }

        return answered;
    }

    /**
     * Creates a list of all questions. It ensures that the returned
     * list is ordered in such a way that it matches how the questions are displayed in the app
     * @return
     */
    public static List<String> retrieveAllQuestionTexts(){
        ArrayList<String> questions = new ArrayList<>(DataHolder.getQuestionTexts().size() + DataHolder.getMCQuestionTexts().size());

        if(DataHolder.getQuestionsVO().getTextQuestionsFirst()){
            // textQuestions at the beginning
            for (TextQuestionVO question : DataHolder.getQuestionTexts()) {
                questions.add(question.getQuestionText());
            }
            for (MultipleChoiceQuestionVO questionVO : DataHolder.getMCQuestionTexts()) {
                questions.add(questionVO.getQuestion());
            }
        } else {
            // mcQuestions at the beginning
            for (MultipleChoiceQuestionVO questionVO : DataHolder.getMCQuestionTexts()) {
                questions.add(questionVO.getQuestion());
            }

            for (TextQuestionVO question : DataHolder.getQuestionTexts()) {
                questions.add(question.getQuestionText());
            }
        }
        return questions;
    }

    /**
     *
     */
    public static void setAnswersDTOToNull(){
        answersVO = null;
        removeFromStorage(ANSWER_VO_KEY);
    }

    /**
     *
     * @return
     */
    public static QuestionsVO getQuestionsVO() {
        if(questionsVO == null){
           questionsVO = retrieveFromStorage(QUESTIONS_VO_KEY, QuestionsVO.class);
        }
        return questionsVO;
    }

    /**
     *
     * @param questionsVO
     */
    public static void setQuestionsVO(QuestionsVO questionsVO) {
        DataHolder.questionsVO = questionsVO;
        if(questionsVO != null){
            storeToStorage(QUESTIONS_VO_KEY, questionsVO);
        } else {
            removeFromStorage(QUESTIONS_VO_KEY);
        }
    }

    /**
     *
     * @return
     */
    public static List<TextQuestionVO> getQuestionTexts() {
        if(getQuestionsVO() != null){
            return getQuestionsVO().getTextQuestions();
        } else {
            return null;
        }
    }

    /**
     *
     * @return
     */
    public static List<MultipleChoiceQuestionVO> getMCQuestionTexts() {
        if(getQuestionsVO() != null){
            return getQuestionsVO().getMultipleChoiceQuestionVOs();
        } else {
            return null;
        }
    }

    /**
     *
     * @param preferences
     */
    public static void setPreferences(SharedPreferences preferences) {
        DataHolder.preferences = preferences;
    }

    /**
     *
     * @return
     */
    public static boolean isRecolorNavigationList() {
        Object obj = retrieveFromStorage(RECOLOR_NAVIGATION_LIST, Boolean.class);
        if (obj != null){
            recolorNavigationList = (boolean) obj;
        }
        return recolorNavigationList;
    }

    /**
     *
     * @param recolorNavigationList
     */
    public static void setRecolorNavigationList(boolean recolorNavigationList) {
        storeToStorage(RECOLOR_NAVIGATION_LIST, recolorNavigationList);
        DataHolder.recolorNavigationList = recolorNavigationList;
    }

    /**
     *
     * @return
     */
    public static String getUuid() {
        if(uuid == null){
            uuid = retrieveFromStorage(UUID_KEY, String.class);
        }
        return uuid;
    }

    /**
     *
     * @param uuid
     */
    public static void setUuid(String uuid){
        if(uuid != null){
            storeToStorage(UUID_KEY, uuid);
        } else {
            removeFromStorage(UUID_KEY);
        }
        DataHolder.uuid = uuid;
    }

    /**
     *
     * @return
     */
    public static String getHostName() {
        if(hostName == null){
            hostName = retrieveFromStorage(HOST_NAME_KEY, String.class);
        }
        return hostName;
    }

    /**
     *
     * @param hostName
     */
    public static void setHostName(String hostName) {
        if(hostName != null){
            storeToStorage(HOST_NAME_KEY, hostName);
        } else {
            removeFromStorage(HOST_NAME_KEY);
        }
        DataHolder.hostName = hostName;
    }

    /**
     *
     * @return
     */
    public static HashMap<String, ImageDataVO> getCommentaryImageMap() {
        if(commentaryImageMap == null){
            commentaryImageMap = retrieveFromStorage(IMAGE_MAP_KEY, HashMap.class);
            if(commentaryImageMap == null){
                commentaryImageMap = new HashMap<>();
            }
        }
        return commentaryImageMap;
    }

    /**
     *
     * @param commentaryImageMap
     */
    public static void setCommentaryImageMap(HashMap<String, ImageDataVO> commentaryImageMap) {
        if(commentaryImageMap != null){
            storeToStorage(IMAGE_MAP_KEY, commentaryImageMap);
        } else {
            removeFromStorage(IMAGE_MAP_KEY);
        }
        DataHolder.commentaryImageMap = commentaryImageMap;
    }

    public static Instant getAppStart() {
        if(appStart == null){
            DataHolder.appStart = DataHolder.retrieveFromStorage(APP_STARTING_TIME_KEY, Instant.class);
        }
        return appStart;
    }

    public static void setAppStart(Instant appStart) {
        if(appStart != null){
            storeToStorage(APP_STARTING_TIME_KEY, appStart);
        } else {
            removeFromStorage(APP_STARTING_TIME_KEY);
        }
        DataHolder.appStart = appStart;
    }

    public static HashSet<String> getGalleryList() {
        if(galleryList == null) {
            galleryList = retrieveFromStorage(GALLERY_LIST_KEY, HashSet.class);
            if (galleryList == null) {
                galleryList = new HashSet<>();
            }
        }
        return galleryList;
    }

    public static void setGalleryList(HashSet<String> galleryList) {
        if(galleryList != null){
            storeToStorage(GALLERY_LIST_KEY, galleryList);
        } else {
            removeFromStorage(GALLERY_LIST_KEY);
        }
        DataHolder.galleryList = galleryList;
    }

    /**
     * Deletes data of questionsVO, answerDTO, uuid and hostName variables.
     * Deletes also all entries of mentioned variables in sharedPreferences
     */
    public static void deleteAllData(){
        questionsVO = null;
        answersVO = null;
        uuid = null;
        hostName = null;
        commentaryImageMap = null;
        recolorNavigationList = false;
        appStart = null;
        galleryList = null;
        removeFromStorage(RECOLOR_NAVIGATION_LIST);
        removeFromStorage(QUESTIONS_VO_KEY);
        removeFromStorage(ANSWER_VO_KEY);
        removeFromStorage(UUID_KEY);
        removeFromStorage(IMAGE_MAP_KEY);
        removeFromStorage(APP_STARTING_TIME_KEY);
        removeFromStorage(GALLERY_LIST_KEY);
        removeFromStorage(HOST_NAME_KEY);
    }

    /**
     * Saves all data to shared preferences.
     */
    public static void storeAllData(){
        storeToStorage(RECOLOR_NAVIGATION_LIST, recolorNavigationList);
        storeToStorage(QUESTIONS_VO_KEY, questionsVO);
        storeToStorage(ANSWER_VO_KEY, answersVO);
        storeToStorage(UUID_KEY, uuid);
        storeToStorage(IMAGE_MAP_KEY, commentaryImageMap);
        storeToStorage(APP_STARTING_TIME_KEY, appStart);
        storeToStorage(GALLERY_LIST_KEY, galleryList);
        storeToStorage(HOST_NAME_KEY, hostName);
    }

    /************************************************************
    *           DATA PERSISTENCE LOGIC IMPLEMENTED HERE         *
    * ***********************************************************/

    /**
     * Stores variables into the shared preferences.
     * @param key the unique string key with which the data can be retrieved again.
     * @param obj object to be stored. method returns if this is null.
     */
    private static void storeToStorage(@NonNull String key, Object obj){

        if(obj == null){
            return;
        }

        if(mapper == null){
            mapper = new ObjectMapper();
        }

        if(preferences == null){
            throw new NullPointerException("Shared preferences are null. Cannot save variable.");
        }

        try {
            String value = mapper.writeValueAsString(obj);
            SharedPreferences.Editor editor = preferences.edit();
            editor.putString(key, value);
            editor.apply();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     *
     * @param key
     * @param type
     * @param <T>
     * @return
     */
    private static <T> T retrieveFromStorage(String key, Class<T> type){
        T obj = null;

        // instantiate T if type of T is primitive. Primitives shall not be returned as null.
        // causes exceptions otherwise.
       /* if(type == Boolean.class || type == Integer.class || type == Character.class || type == Long.class || type == Short.class){
            try {
                obj = type.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }*/

        if(mapper == null){
            mapper = new ObjectMapper();
        }
        if(preferences == null){
            return null;
        }

        try {
            String value = preferences.getString(key, null);
            if(value != null && type != String.class){
                obj = mapper.readValue(value, type);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return obj;
    }

    /**
     *
     * @param key
     */
    private static void removeFromStorage(String key){
        if(preferences == null){
            return;
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }
}