package de.fhb.campusapp.eval.data;

import com.google.common.collect.Iterables;

import org.joda.time.Instant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import de.fhb.campusapp.eval.utility.vos.AnswersVO;
import de.fhb.campusapp.eval.utility.vos.ChoiceVO;
import de.fhb.campusapp.eval.utility.vos.ImageDataVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceAnswerVO;
import de.fhb.campusapp.eval.utility.vos.MultipleChoiceQuestionVO;
import de.fhb.campusapp.eval.utility.vos.QuestionsVO;
import de.fhb.campusapp.eval.utility.vos.TextAnswerVO;
import de.fhb.campusapp.eval.utility.vos.TextQuestionVO;

/**
 * Created by Admin on 12.11.2016.
 */
 public interface IDataManager {

    /**
     * Weather a question was answered before.
     * Loops through the list of answered text questions stored in AnswersDTO
     * @param question text of the question whose answer should be searched for
     * @return The AnswerDTO that stored the given question or null if none was found.
     */
    boolean isTextQuestionAnswered(String question);

    /**
     * Weather a question was answered before.
     * Loops through the list of answered text questions stored in AnswersDTO
     * @param question text of the question whose answer should be searched for
     * @return The AnswerDTO that stored the given question or null if none was found.
     */
    TextAnswerVO retrieveTextAnswerVO(String question);

    /**
     * Weather a question was answered before.
     * @param question text of the question whose answer should be searched for
     * @return the MultipleChoiceAnswerDTO that stored the given question or null if none was found.
     */
    boolean isMcQuestionAnswered(String question);

    /**
     * Returns a specific MultipleChoiceAnswerVO identified by question text
     * Loops through the list of answered multiple choice questions stored in AnswersDTO
     * @param question question text used to identify correct object
     * @return
     */
    MultipleChoiceAnswerVO retrieveMcQuestion(String question);

    /**
     * Returns a specific ChoiceDTO identified by question text.
     * A provided MultipleChoiceQuestionDTO is searched through.
     * @param choiceText
     * @return
     */
    ChoiceVO retrieveChoiceVO(MultipleChoiceQuestionVO questionVO, String choiceText);

    /**
     * Returns a specific ChoiceDTO identified by text.
     * Loops through all MultipleChoiceQuestionsDTOs stored in QuestionDTO
     * until it found the one which is identified by question parameter
     * @param choiceText
     * @param question
     * @return
     */
    ChoiceVO retrieveChoiceVOByQuestionText(String question, String choiceText);
    
    /**
     * Returns a ChoiceDTO whose grade property matches the given parameter grade.
     * Returns null if no ChoiceDTO with given grade was found.
     * @param question
     * @param grade
     * @return
     */
    ChoiceVO retrieveChoiceByGrade(String question, int grade);

    /**
     *
     * @param question
     * @return
     */
    TextQuestionVO retrieveTextQuestionVO(String question);

    /**
     * Weather a given question is answered or not. The type of given question does not matter.
     * All types will be searched with all possibilities to answer a question.
     * @param question questions that should be tested
     * @return true is questions was answered in any valid way
     */
    boolean isQuestionAnswered(String question);

    /**
     * Creates a list of all questions. It ensures that the returned
     * list is ordered in such a way that it matches how the questions are displayed in the app
     * @return ordered list of question texts
     */
    List<String> retrieveAllQuestionTexts();

    void initAndObserveQuestionRequest();

    /**
     * Saves all data to shared preferences.
     */
    void saveAllData();

    void restoreAllData();

    void removeAllData();

    QuestionsVO getmQuestionsVO();

    List<TextQuestionVO> getTextQuestionTexts();

    List<MultipleChoiceQuestionVO> getMCQuestionTexts();

    void setmQuestionsVO(QuestionsVO mQuestionsVO);
    
    AnswersVO getmAnswersVO();

    int getPositionOffset();
    void setmAnswersVO(AnswersVO mAnswersVO);
    String getmUuid();
    void setmUuid(String mUuid);
    String getmHostName();
    void setmHostName(String mHostName);
    HashMap<String, ImageDataVO> getmImageMap();
    void setmImageMap(HashMap<String, ImageDataVO> mImageMap);
    boolean ismRecolorNavigation();
    void setmRecolorNavigation(boolean mRecolorNavigation);
    HashSet<String> getmGallerySet();
    void setmGallerySet(HashSet<String> mGallerySet);
    String getmCurrentQuestion();
    void setmCurrentQuestion(String mCurrentQuestion);
    int getmCurrentPagerPosition();
    void setmCurrentPagerPosition(int mCurrentPagerPosition);
    Instant getmAppStartTime();
    void setmAppStartTime(Instant mAppStartTime);

}
