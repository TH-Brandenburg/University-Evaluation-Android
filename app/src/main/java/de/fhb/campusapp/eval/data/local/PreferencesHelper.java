package de.fhb.campusapp.eval.data.local;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.type.CollectionLikeType;
import com.fasterxml.jackson.databind.type.MapLikeType;
import com.fasterxml.jackson.databind.type.TypeFactory;

import org.joda.time.Instant;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.inject.Inject;

import de.fhb.campusapp.eval.injection.ApplicationContext;
import de.fhb.campusapp.eval.utility.vos.AnswersVO;
import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceAnswerVO;
import de.fhb.campusapp.eval.utility.vos.QuestionsVO;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;

/**
 * Created by Sebastian Müller on 12.11.2016.
 */
public class PreferencesHelper {

    private static final String QUESTIONS_VO_KEY = "QUESTIONS_VO_KEY";
    private static final String ANSWER_VO_KEY = "ANSWER_VO_KEY";
    private static final String UUID_KEY = "UUID_KEY";
    private static final String HOST_NAME_KEY = "HOST_NAME_KEY";
    private static final String IMAGE_MAP_KEY = "IMAGE_MAP_KEY";
    private static final String RECOLOR_NAVIGATION_LIST = "RECOLOR_NAVIGATION_LIST";
    private static final String APP_STARTING_TIME_KEY = "APP_STARTING_TIME";
    private static final String GALLERY_LIST_KEY = "GALLERY_LIST_KEY";
    private static final String CURRENT_QUESTION = "CURRENT_QUESTION";
    private static final String CURRENT_PAGER_POSITION = "CURRENT_PAGER_POSITION";

    private static final String COLLECTION_TYPE = "COLLECTION_TYPE";
    private static final String MAP_TYPE = "MAP_TYPE";

    final ObjectMapper mapper;
    final SharedPreferences preferences;

    @Inject
    public PreferencesHelper(ObjectMapper mapper, @ApplicationContext Context context) {
        this.mapper = mapper;
        this.preferences = context.getSharedPreferences("edl_pref_file" ,Context.MODE_PRIVATE);
    }

    /**
     *
     * @return
     */
    public AnswersVO getAnswersVO() {

        AnswersVO answersVO = retrieveFromStorage(ANSWER_VO_KEY, AnswersVO.class);

        if(answersVO == null){
            answersVO = new AnswersVO("", "", new ArrayList<TextAnswerVO>(), new ArrayList<MultipleChoiceAnswerVO>(), "");
            storeToStorage(ANSWER_VO_KEY, answersVO);
        }
        return answersVO;
    }


    public void putAnswersVO(AnswersVO answersVO){
        storeToStorage(ANSWER_VO_KEY, answersVO);
    }

    /**
     *
     */
    public void removeAnswersVO(){
        removeFromStorage(ANSWER_VO_KEY);
    }

    public void putRecolorNavigation(boolean recolorNavigationList){
        storeToStorage(RECOLOR_NAVIGATION_LIST, recolorNavigationList);
    }

    public boolean getRecolorNavigation(){
        return retrieveFromStorage(RECOLOR_NAVIGATION_LIST, Boolean.class);
    }

    public void removeRecolorNavigationList(){
        removeFromStorage(RECOLOR_NAVIGATION_LIST);
    }

    //*********************************************

    public void putQuestionsVO(QuestionsVO questionsVO){
        storeToStorage(QUESTIONS_VO_KEY, questionsVO);
    }

    public QuestionsVO getQuestionsVO(){
        return retrieveFromStorage(QUESTIONS_VO_KEY, QuestionsVO.class);
    }

    public void removeQuestionsVO(){
        removeFromStorage(QUESTIONS_VO_KEY);
    }

    //**********************************************

    public void putUUID(String uuid){
        storeToStorage(UUID_KEY, uuid);
    }

    public String getUUID(){
        return retrieveFromStorage(QUESTIONS_VO_KEY, String.class);
    }

    public void removeUUID(){
        removeFromStorage(UUID_KEY);
    }

    //**********************************************

    public void putImageMap(Map<String, ImageDataVO> imageMap){
        storeToStorage(IMAGE_MAP_KEY, imageMap);
    }

    public HashMap<String, ImageDataVO> getImageMap(){
        return retrieveComplexType(IMAGE_MAP_KEY, MAP_TYPE , HashMap.class, String.class, ImageDataVO.class);
    }

    public void removeImageMap(){
        removeFromStorage(IMAGE_MAP_KEY);
    }

    //***********************************************

    public void putAppStartTime(Instant appStart){
        storeToStorage(APP_STARTING_TIME_KEY, appStart);
    }

    public Instant getAppStartTime(){
        return retrieveFromStorage(APP_STARTING_TIME_KEY, Instant.class);
    }

    public void removeAppStartTime(){
        removeFromStorage(APP_STARTING_TIME_KEY);
    }

    //***********************************************

    public void putGallerySet(HashSet<String> gallerySet){
        storeToStorage(GALLERY_LIST_KEY, gallerySet);
    }

    public HashSet<String> getGallerySet(){
        return retrieveComplexType(GALLERY_LIST_KEY, COLLECTION_TYPE, HashSet.class, String.class);
    }

    public void removeGalleryList(){
        removeFromStorage(GALLERY_LIST_KEY);
    }

    //***********************************************

    public void putHostName(String hostName){
        storeToStorage(HOST_NAME_KEY, hostName);
    }

    public String getHostName(){
        return retrieveFromStorage(HOST_NAME_KEY, String.class);
    }

    public void removeHostName(){
        removeFromStorage(HOST_NAME_KEY);
    }

    //***********************************************

    public void putCurrentPagerPosition(int currentPagerPosition){
        storeToStorage(CURRENT_PAGER_POSITION, currentPagerPosition);
    }

    public int getCurrentPagerPosition(){
        return retrieveFromStorage(CURRENT_PAGER_POSITION, Integer.class);
    }

    public void removeCurrentPagerPosition(){
        removeFromStorage(CURRENT_PAGER_POSITION);
    }

    //***********************************************

    public void putCurrentQuestion(String currentQuestion){
        storeToStorage(CURRENT_QUESTION, currentQuestion);
    }

    public String getCurrentQuestion(){
        return retrieveFromStorage(CURRENT_QUESTION, String.class);
    }

    public void removeCurrentQuestion(){
        removeFromStorage(CURRENT_QUESTION);
    }


    /************************************************************
     *           DATA PERSISTENCE LOGIC IMPLEMENTED HERE         *
     * ***********************************************************/

    /**
     * Stores variables into the shared preferences.
     * @param key the unique string key with which the data can be retrieved again.
     * @param obj object to be stored. method returns if this is null.
     */
    private void storeToStorage(@NonNull String key, Object obj){

        if(obj == null){
            return;
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
    private <T> T retrieveFromStorage(String key, Class<T> type){
        T obj = null;

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
     * Method that retrieves complex objects with custom types from shared preferences
     * @param key The key identifying the object within sharedPreferences
     * @param type technical parameter needed by the ObjectMapper. It is used to identify the method the mapper needs to call.
     *             Concrete: It specifies wether the returned type is a map or a collection. There a different methods but those
     *             2 are currently supported
     * @param returnType The type of the object this method is to return.
     * @param paramType list of parameters containing the typed generic returnType will be resolved with. Example: HashMap<paramType[0], paramType[1]>
     * @return the retrieved object of type returnType.
     */
    private <T> T retrieveComplexType(String key, String type, Class<T> returnType, Class ... paramType){
        T obj = null;

        try {
            String value = preferences.getString(key, null);
            if(value != null){
                TypeFactory typeFactory = mapper.getTypeFactory();
                switch (type){
                    case COLLECTION_TYPE:
                        CollectionLikeType collection = typeFactory.constructCollectionLikeType(returnType, paramType[0]);
                        obj = mapper.readValue(value, collection);
                        break;
                    case MAP_TYPE:
                        MapLikeType map = typeFactory.constructMapLikeType(returnType, paramType[0], paramType[1]);
                        obj = mapper.readValue(value, map);
                        break;
                }
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
    private void removeFromStorage(String key){
        if(preferences == null){
            return;
        }
        SharedPreferences.Editor editor = preferences.edit();
        editor.remove(key);
        editor.apply();
    }


}
