package de.thb.ue.android.data;

import android.support.annotation.NonNull;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.thb.ue.android.data.VOs.Answer;
import de.thb.ue.android.data.VOs.AnswersVO;
import de.thb.ue.android.data.VOs.ChoiceVO;
import de.thb.ue.android.data.VOs.ProcessedResponse;
import de.thb.ue.android.data.VOs.QuestionsVO;
import de.thb.ue.android.data.VOs.SingleChoiceAnswerVO;
import de.thb.ue.android.data.VOs.TextAnswerVO;
import de.thb.ue.android.utility.TestQuestionnaire;
import io.reactivex.Single;

/**
 * Created by Sebastian Müller on 12.11.2016.
 */
public interface IDataManager {

    //*************Networking****************

    boolean isConnected();

    Single<ProcessedResponse<QuestionsVO>> getQuestions(String voteToken, String host);
    Single<ProcessedResponse<Void>> sendAnswers();

    //*************Internal Operations*******

    Single<Boolean> putTextAnswer(String question, int questionId, String answer);
    Single<Boolean> putSCAnswer(String question, ChoiceVO answer);
    Single<Boolean> putStudyPath(String studyPath);

    /**
     * Returns the answer currently entered for the specified question.
     * The returned value changes as soon as the user enters a new answer.
     *
     * Therefore the returned value should not be stored. Instead call the method again.
     * @param question the question for which you seek the answer.
     * @return
     */
    ChoiceVO getCurrentScAnswer(@NonNull String question);

    /**
     * Returns the answer currently entered for the specified question.
     * The returned value changes as soon as the user enters a new answer.
     *
     * Therefore the returned value should not be stored. Instead call the method again.
     * @param question the question for which you seek the answer.
     * @return
     */
    String getCurrentTextAnswer(@NonNull String question);

    QuestionsVO getCachedQuestions();

    Set<String> getAnsweredQuestions();

    /**
     * Retrieve the study path the user selected. May return an empty String when no study path was selected.
     * @return the study path or empty string if none was selected.
     */
    String getStudyPath();


    /**
     * Removes the transient data from shared preferences.
     */
    void removeCachedData();


    List<String> getQuestionTexts();
}
