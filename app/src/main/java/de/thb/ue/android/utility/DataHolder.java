package de.thb.ue.android.utility;


import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.roboguice.shaded.goole.common.collect.Iterables;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.thb.ca.dto.AnswersDTO;
import de.thb.ca.dto.MultipleChoiceQuestionDTO;
import de.thb.ca.dto.util.ChoiceDTO;
import de.thb.ca.dto.QuestionsDTO;
import de.thb.ca.dto.util.MultipleChoiceAnswerDTO;
import de.thb.ca.dto.util.TextAnswerDTO;
import de.thb.ca.dto.util.TextQuestionDTO;

public class DataHolder {

    private static final String QUESTIONS_DTO_KEY = "QUESTIONS_DTO_KEY";
    private static final String ANSWER_DTO_KEY = "ANSWER_DTO_KEY";
    private static final String UUID_KEY = "UUID_KEY";
    private static final String HOST_NAME_KEY = "HOST_NAME_KEY";
    private static final String IMAGE_MAP_KEY = "IMAGE_MAP_KEY";
    private static final String RECOLOR_NAVIGATION_LIST = "RECOLOR_NAVIGATION_LIST";

    private static QuestionsDTO questionsDTO;
    private static AnswersDTO answersDTO;
    private static String uuid;
    private static String hostName;
    private static HashMap<String, ImagePathsVO> commentaryImageMap;
    private static boolean recolorNavigationList = false;


    private static ObjectMapper mapper;
    private static SharedPreferences preferences;

    /**
     *
     * @return
     */
    public static AnswersDTO getAnswersDTO() {
        if(answersDTO == null){
            answersDTO = retrieveFromStorage(ANSWER_DTO_KEY, AnswersDTO.class);
        }
        if(answersDTO == null){
            answersDTO = new AnswersDTO("", "", new ArrayList<TextAnswerDTO>(), new ArrayList<MultipleChoiceAnswerDTO>(), "");
            storeToStorage(ANSWER_DTO_KEY, answersDTO);
        }
        return answersDTO;
    }

    /**
     * Weather a question was answered before.
     * Loops through the list of answered text questions stored in AnswersDTO
     * @param question text of the question whose answer should be searched for
     * @return The AnswerDTO that stored the given question or null if none was found.
     */
    public static TextAnswerDTO isTextQuestionAnswered(String question){
        return Iterables.tryFind(DataHolder.getAnswersDTO().getTextAnswers(),
                textAnswer -> textAnswer.getQuestionText().equals(question)).orNull();
    }

    /**
     * Weather a question was answered before.
     * Loops through the list of answered multiple choice questions stored in AnswersDTO
     * @param question text of the question whose answer should be searched for
     * @return the MultipleChoiceAnswerDTO that stored the given question or null if none was found.
     */
    public static MultipleChoiceAnswerDTO isMcQuestionAnswered(String question){
        return Iterables.tryFind(DataHolder.getAnswersDTO().getMcAnswers(),
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
    public static ChoiceDTO retrieveChoiceDTO(String question, String choiceText){
        //find mcChoiceQuestionDTO that matches with given question
        MultipleChoiceQuestionDTO matchingDTO =
                Iterables.tryFind(DataHolder.getQuestionsDTO().getMultipleChoiceQuestionDTOs(), mcQuestion -> mcQuestion.getQuestion().equals(question)).orNull();

        //retrieve the choiceDTO matching the given grade, null if none was found
        return Iterables.tryFind(matchingDTO.getChoices(), choice -> choice.getChoiceText().equals(choiceText)).orNull();
    }

    /**
     * Returns a ChoiceDTO whose grade property matches the given parameter grade.
     * Returns null if no ChoiceDTO with given grade was found.
     * @param question
     * @param grade
     * @return
     */
    public static ChoiceDTO retrieveChoiceByGrade(String question, int grade){
        //find mcChoiceQuestionDTO that matches with given question
        MultipleChoiceQuestionDTO matchingDTO =
                Iterables.tryFind(DataHolder.getQuestionsDTO().getMultipleChoiceQuestionDTOs(), mcQuestion -> mcQuestion.getQuestion().equals(question)).orNull();

        //retrieve the choiceDTO matching the given grade, null if none was found
        return Iterables.tryFind(matchingDTO.getChoices(), choice -> choice.getGrade() == grade).orNull();
    }

    /**
     *
     * @param question
     * @return
     */
    public static TextQuestionDTO retrieveTextQuestionDTO(String question){
        return Iterables.tryFind(DataHolder.getQuestionsDTO().getTextQuestions(),
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
     * Cretaes a list of all questions. It ensures that the returned
     * list is ordered in such a way that it matches how the questions are displayed in the app
     * @return
     */
    public static List<String> retrieveAllQuestionTexts(){
        ArrayList<String> questions = new ArrayList<>(DataHolder.getQuestionTexts().size() + DataHolder.getMCQuestionTexts().size());

        if(DataHolder.getQuestionsDTO().getTextQuestionsFirst()){
            // textQuestions at the beginning
            for (TextQuestionDTO question : DataHolder.getQuestionTexts()) {
                questions.add(question.getQuestionText());
            }
            for (MultipleChoiceQuestionDTO questionDTO : DataHolder.getMCQuestionTexts()) {
                questions.add(questionDTO.getQuestion());
            }
        } else {
            // mcQuestions at the beginning
            for (MultipleChoiceQuestionDTO questionDTO : DataHolder.getMCQuestionTexts()) {
                questions.add(questionDTO.getQuestion());
            }

            for (TextQuestionDTO question : DataHolder.getQuestionTexts()) {
                questions.add(question.getQuestionText());
            }
        }


        return questions;
    }


    /**
     *
     */
    public static void setAnswersDTOToNull(){
        answersDTO = null;
        removeFromStorage(ANSWER_DTO_KEY);
    }

    /**
     *
     * @return
     */
    public static QuestionsDTO getQuestionsDTO() {
        if(questionsDTO == null){
           questionsDTO = retrieveFromStorage(QUESTIONS_DTO_KEY, QuestionsDTO.class);
        }
        return questionsDTO;
    }

    /**
     *
     * @param questionsDTO
     */
    public static void setQuestionsDTO(QuestionsDTO questionsDTO) {
        DataHolder.questionsDTO = questionsDTO;
        if(questionsDTO != null){
            storeToStorage(QUESTIONS_DTO_KEY, questionsDTO);
        } else {
            removeFromStorage(QUESTIONS_DTO_KEY);
        }
    }

    /**
     *
     * @return
     */
    public static List<TextQuestionDTO> getQuestionTexts() {
        return getQuestionsDTO().getTextQuestions();
    }

    /**
     *
     * @return
     */
    public static List<MultipleChoiceQuestionDTO> getMCQuestionTexts() {
        return getQuestionsDTO().getMultipleChoiceQuestionDTOs();
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
    public static HashMap<String, ImagePathsVO> getCommentaryImageMap() {
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
    public static void setCommentaryImageMap(HashMap<String, ImagePathsVO> commentaryImageMap) {
        if(commentaryImageMap != null){
            storeToStorage(IMAGE_MAP_KEY, commentaryImageMap);
        } else {
            removeFromStorage(IMAGE_MAP_KEY);
        }
        DataHolder.commentaryImageMap = commentaryImageMap;
    }

    /**
     * deletes data of questionsDTO, answerDTO, uuid and hostName variables.
     * Deletes also all entries of mentioned variables in sharedPreferences
     */
    public static void deleteAllData(){
        questionsDTO = null;
        answersDTO = null;
        uuid = null;
        hostName = null;
        commentaryImageMap = null;
        recolorNavigationList = false;
        removeFromStorage(RECOLOR_NAVIGATION_LIST);
        removeFromStorage(QUESTIONS_DTO_KEY);
        removeFromStorage(ANSWER_DTO_KEY);
        removeFromStorage(UUID_KEY);
        removeFromStorage(IMAGE_MAP_KEY);
    }

    /************************************************************
    *           DATA PERSISTENCE LOGIC IMPLEMENTED HERE         *
    * ***********************************************************/

    /**
     *
     * @param key
     * @param obj
     */
    private static void storeToStorage(@NonNull String key, @NonNull Object obj){
        if(mapper == null){
            mapper = new ObjectMapper();
        }

        if(preferences == null){
            return;
        }

        try {
            String value = mapper.writeValueAsString(obj);
            SharedPreferences.Editor editor =preferences.edit();
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